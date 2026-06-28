package com.cpuburn.engine;

import com.cpuburn.worker.CpuWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CpuBurnEngine {

    private ExecutorService executor;
    private final List<CpuWorker> workers = new ArrayList<>();

    /**
     * Inicia o estresse de CPU criando uma thread por núcleo via ExecutorService.
     *
     * @param cores      número de núcleos a sobrecarregar
     * @param intensidade percentual de intensidade (1–100)
     */
    public void start(int cores, int intensidade) {
        stop(); // garante que não haja execução anterior

        executor = Executors.newFixedThreadPool(cores);

        for (int i = 0; i < cores; i++) {
            CpuWorker worker = new CpuWorker(intensidade);
            workers.add(worker);
            executor.submit(worker);
        }
    }

    /**
     * Para todos os workers e aguarda as threads finalizarem (até 2s).
     */
    public void stop() {
        workers.forEach(CpuWorker::stop);
        workers.clear();

        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
            try {
                executor.awaitTermination(2, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Retorna o número de workers ativos no momento.
     */
    public int getActiveWorkerCount() {
        return workers.size();
    }
}