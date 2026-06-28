# CPU Burn 🔥

Ferramenta de estresse e monitoramento de CPU em tempo real, desenvolvida em Java com JavaFX.

## Sobre o projeto

O CPU Burn foi desenvolvido como trabalho final da disciplina de Sistemas Operacionais. O objetivo é demonstrar na prática conceitos fundamentais de SO como gerenciamento de threads, escalonamento de processos, uso de CPU e monitoramento de hardware.

O app cria uma thread por núcleo lógico da máquina e as coloca em loop de cálculo pesado, estressando a CPU de forma controlada. Ao mesmo tempo, monitora e exibe em tempo real o impacto disso no sistema operacional.

## Funcionalidades

- Detecção automática do número de núcleos da máquina
- Estresse de CPU com intensidade ajustável (1% a 100%)
- Gráfico em tempo real com uso total e uso de kernel separados
- Monitoramento por núcleo individual
- Exibição de PID, threads ativas, memória da JVM e temperatura da CPU
- Encerramento seguro de threads ao fechar o app

## Conceitos de SO aplicados

- Criação e gerenciamento de threads via `ExecutorService`
- Controle de intensidade via `Thread.sleep()` — cede CPU ao escalonador
- Visibilidade entre threads via flag `volatile`
- Leitura de ticks de CPU para cálculo de uso total e de kernel
- Exibição do PID do processo — verificável no Gerenciador de Tarefas

## Tecnologias

- Java 17
- JavaFX 21
- OSHI 6.6.5 — leitura de hardware via API do SO
- Maven

## Estrutura do projeto

```
src/main/java/com/cpuburn/
├── app/
│   └── Main.java              # Ponto de entrada, ciclo de vida da janela
├── controller/
│   └── MainController.java    # Conecta interface com a logica
├── engine/
│   └── CpuBurnEngine.java     # Gerencia o pool de threads
├── monitor/
│   └── CpuMonitor.java        # Le dados de CPU via OSHI
└── worker/
    └── CpuWorker.java         # Tarefa executada por cada thread
```
