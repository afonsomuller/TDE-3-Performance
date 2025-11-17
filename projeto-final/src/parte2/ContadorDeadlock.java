import java.util.concurrent.*;

public class ContadorDeadlock {
    static int contador = 0;
    
    public static void main(String[] args) throws Exception {
        int numThreads = 8;
        int incrementosPorThread = 250_000;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        
        Runnable tarefa = () -> {
            for (int i = 0; i < incrementosPorThread; i++) {
                contador++;
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
        
        System.out.println("Contador sem Sincronização");
        System.out.println("Threads: " + numThreads);
        System.out.println("Incrementos por thread: " + incrementosPorThread);
        System.out.println("Valor esperado: " + valorEsperado);
        System.out.println("Valor obtido: " + contador);
        System.out.println("Diferença: " + (valorEsperado - contador));
        System.out.printf("Tempo de execução: %.4f segundos%n", tempoDecorrido);
        System.out.println();
        
        if (contador != valorEsperado) {
            System.out.println("RACE CONDITION");
            System.out.println("Incrementos perdidos devido à falta de sincronização.");
        }
    }
}
