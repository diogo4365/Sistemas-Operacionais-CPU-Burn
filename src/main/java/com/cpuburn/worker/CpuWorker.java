package com.cpuburn.worker;

public class CpuWorker implements Runnable {

    private volatile boolean running = true;
    private volatile double resultado = 0;
    private final int intensidade;

    public CpuWorker(int intensidade) {
        this.intensidade = intensidade;
    }

    
    //Retorna o resultado acumulado pelo worker.
    public double getResultado() {
        return resultado;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        resultado = 0;

        while (running) {
            for (int i = 0; i < 1_000_000; i++) {
                resultado += Math.sqrt(i);
            }

            try {
                int pausa = 100 - intensidade;
                if (pausa > 0) {
                    Thread.sleep(pausa);
                }
            } catch (InterruptedException e) {
                // Restaura o status de interrupção e encerra o loop corretamente
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}