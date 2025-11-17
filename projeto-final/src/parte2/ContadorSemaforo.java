import java.util.concurrent.*;

public class ContadorSemaforo {
    static int contador = 0;
    static final Semaphore semaforo = new Semaphore(1, true);
    
    public static void main(String[] args) throws Exception {
        int numThreads = 8;
        int incrementosPorThread = 250_000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        Runnable tarefa = () -> {
            for (int i = 0; i < incrementosPorThread; i++) {
                try {
                    semaforo.acquire();
                    contador++;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                } finally {
                    semaforo.release();
                }
            }
        };
        
        long inicio = System.nanoTime();
        
        for (int i = 0; i < numThreads; i++) {
            executor.submit(tarefa);
        }
        
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        
        long fim = System.nanoTime();
        double tempoDecorrido = (fim - inicio) / 1_000_000_000.0;
        
        int valorEsperado = numThreads * incrementosPorThread;
        
        System.out.println("Contador com Semáforo");
        System.out.println("Threads: " + numThreads);
        System.out.println("Incrementos por thread: " + incrementosPorThread);
        System.out.println("Valor esperado: " + valorEsperado);
        System.out.println("Valor obtido: " + contador);
        System.out.println("Diferença: " + (valorEsperado - contador));
        System.out.printf("Tempo de execução: %.4f segundos%n", tempoDecorrido);
        System.out.println();
        
        if (contador == valorEsperado) {
            System.out.println("Sincronização realizada");
            System.out.println("Todos os incrementos foram preservados.");
        }
    }
}
