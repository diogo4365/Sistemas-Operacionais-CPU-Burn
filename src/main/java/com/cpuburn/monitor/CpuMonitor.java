package com.cpuburn.monitor;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.Sensors;

public class CpuMonitor {

    private final CentralProcessor processor;
    private final Sensors sensors;
    private long[] ticks;

    public CpuMonitor() {
        SystemInfo info = new SystemInfo();
        processor = info.getHardware().getProcessor();
        sensors = info.getHardware().getSensors();
        ticks = processor.getSystemCpuLoadTicks();
    }

    
    // Atualiza os ticks e retorna um snapshot com todos os valores calculados.
    // Deve ser chamado uma vez por ciclo para evita múltiplas leituras inconsistentes.
     
    public CpuSnapshot getSnapshot() {
        long[] currentTicks = processor.getSystemCpuLoadTicks();

        // Diferença de cada estado desde a última leitura
        long kernel  = currentTicks[CentralProcessor.TickType.SYSTEM.getIndex()]
                     - ticks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle    = currentTicks[CentralProcessor.TickType.IDLE.getIndex()]
                     - ticks[CentralProcessor.TickType.IDLE.getIndex()];

        long total = 0;
        for (int i = 0; i < currentTicks.length; i++) {
            total += currentTicks[i] - ticks[i];
        }

        double totalUsage  = total > 0 ? (double)(total - idle) / total * 100 : 0;
        double kernelUsage = total > 0 ? (double) kernel / total * 100 : 0;

        // Atualiza ticks para o próximo ciclo
        ticks = currentTicks;

        return new CpuSnapshot(totalUsage, kernelUsage);
    }

    
     // Retorna o uso individual de cada núcleo lógico em percentual.
    
    public double[] getUsagePerCore() {
        double[] load = processor.getProcessorCpuLoad(500);
        double[] percent = new double[load.length];
        for (int i = 0; i < load.length; i++) {
            percent[i] = load[i] * 100;
        }
        return percent;
    }

    
     // Retorna o número de núcleos lógicos disponíveis.
     
    public int getCoreCount() {
        return processor.getLogicalProcessorCount();
    }

    
     // Retorna a temperatura da CPU em graus Celsius.
     // Retorna 0 se o hardware/SO não expuser essa informação (caso da minha maquina).
     
    public double getTemperature() {
        return sensors.getCpuTemperature();
    }

    
    // Snapshot com todos os valores de CPU calculados em um único ciclo.
     
    public static class CpuSnapshot {
        public final double totalUsage;
        public final double kernelUsage;

        public CpuSnapshot(double totalUsage, double kernelUsage) {
            this.totalUsage  = totalUsage;
            this.kernelUsage = kernelUsage;
        }
    }
}