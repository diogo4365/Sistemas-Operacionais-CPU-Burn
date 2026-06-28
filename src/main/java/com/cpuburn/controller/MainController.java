package com.cpuburn.controller;

import com.cpuburn.engine.CpuBurnEngine;
import com.cpuburn.monitor.CpuMonitor;
import com.cpuburn.monitor.CpuMonitor.CpuSnapshot;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MainController {

    // Labels de status
    @FXML private Label cpuLabel;
    @FXML private Label kernelLabel;
    @FXML private Label threadLabel;
    @FXML private Label coresLabel;
    @FXML private Label intensityLabel;
    @FXML private Label pidLabel;
    @FXML private Label memLabel;
    @FXML private Label tempLabel;
    @FXML private Label statusLabel;

    // Gráfico 
    @FXML private LineChart<Number, Number> chart;

    // Controles 
    @FXML private Slider intensitySlider;
    @FXML private Button startButton;
    @FXML private Button stopButton;

    // Container de labels por núcleo
    @FXML private HBox coreLabelsBox;

    // Lógica 
    private final CpuBurnEngine engine = new CpuBurnEngine();
    private final CpuMonitor monitor = new CpuMonitor();

    private Timeline timeline;
    private XYChart.Series<Number, Number> seriesTotal  = new XYChart.Series<>();
    private XYChart.Series<Number, Number> seriesKernel = new XYChart.Series<>();
    private int time = 0;

    @FXML
    public void initialize() {
        int cores = monitor.getCoreCount() * 100;

        // Informações estáticas de hardware
        coresLabel.setText("Nucleos detectados: " + cores);

        // PID do processo atual
        long pid = ProcessHandle.current().pid();
        pidLabel.setText("PID: " + pid);

        // Cria um label por nucleo no HBox
        for (int i = 0; i < cores; i++) {
            Label l = new Label("Core " + i + ": --");
            l.setStyle("-fx-font-size: 11px; -fx-padding: 2 6;");
            coreLabelsBox.getChildren().add(l);
        }

        // Slider label de intensidade em tempo real
        intensitySlider.valueProperty().addListener((obs, old, val) ->
            intensityLabel.setText("Intensidade: " + val.intValue() + "%")
        );
        intensityLabel.setText("Intensidade: " + (int) intensitySlider.getValue() + "%");

        // Duas series no grafico
        seriesTotal.setName("Total");
        seriesKernel.setName("Kernel");
        chart.getData().add(seriesTotal);
        chart.getData().add(seriesKernel);

        // Estado inicial dos botoes e status
        stopButton.setDisable(true);
        statusLabel.setText("Parado");
        statusLabel.setStyle("-fx-text-fill: #888888; -fx-font-family: monospace; -fx-font-size: 14px;");

        // Timeline de atualizacao (1s)
        timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> update())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    @FXML
    public void start() {
        int cores = monitor.getCoreCount();
        int intensidade = (int) intensitySlider.getValue();

        engine.start(cores, intensidade);
        timeline.play();

        startButton.setDisable(true);
        stopButton.setDisable(false);
        statusLabel.setText("Rodando");
        statusLabel.setStyle("-fx-text-fill: #ff4444; -fx-font-family: monospace; -fx-font-size: 14px;");
    }

    @FXML
    public void stop() {
        engine.stop();
        timeline.stop();

        startButton.setDisable(false);
        stopButton.setDisable(true);
        statusLabel.setText("Parado");
        statusLabel.setStyle("-fx-text-fill: #888888; -fx-font-family: monospace; -fx-font-size: 14px;");
    }

    /**
     * Chamado a cada segundo pela Timeline.
     * Uma unica chamada getSnapshot() garante consistencia entre total e kernel.
     */
    private void update() {
        // Um unico snapshot por ciclo — total e kernel lidos juntos
        CpuSnapshot snap = monitor.getSnapshot();

        cpuLabel.setText(String.format("CPU Total: %.1f%%", snap.totalUsage));
        kernelLabel.setText(String.format("Kernel: %.1f%%", snap.kernelUsage));

        // Threads ativas na JVM
        threadLabel.setText("Threads ativas: " + Thread.activeCount());

        // Memoria usada pela JVM
        Runtime rt = Runtime.getRuntime();
        long usedMB = (rt.totalMemory() - rt.freeMemory()) / 1024 / 1024;
        memLabel.setText("Memoria JVM: " + usedMB + " MB");

        // Temperatura
        double temp = monitor.getTemperature();
        tempLabel.setText(temp > 0
            ? String.format("Temp: %.1f grC", temp)
            : "Temp: N/A");

        // Grafico — janela deslizante de 60s com duas linhas
        seriesTotal.getData().add(new XYChart.Data<>(time, snap.totalUsage));
        seriesKernel.getData().add(new XYChart.Data<>(time, snap.kernelUsage));
        time++;

        if (seriesTotal.getData().size() > 60) {
            seriesTotal.getData().remove(0);
            seriesKernel.getData().remove(0);
        }

        // Uso por nucleo
        double[] perCore = monitor.getUsagePerCore();
        var labels = coreLabelsBox.getChildren();
        for (int i = 0; i < labels.size() && i < perCore.length; i++) {
            Label l = (Label) labels.get(i);
            l.setText(String.format("Core %d: %.0f%%", i, perCore[i]));
        }
    }

    public void shutdown() {
        stop();
    }
}