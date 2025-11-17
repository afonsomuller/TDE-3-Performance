import java.util.concurrent.Semaphore;

public class FilosofosDeadlock {
    static final int N = 5;
    static Semaphore[] garfos = new Semaphore[N];
    static String[] estados = new String[N];
    
    public static void main(String[] args) {
        System.out.println("Jantar dos Filósofos - Deadlock");
        System.out.println("Protocolo: Cada filósofo pega esquerda, depois direita\n");
        
        for (int i = 0; i < N; i++) {
            garfos[i] = new Semaphore(1);
            estados[i] = "pensando";
        }
        
        Thread[] filosofos = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            filosofos[i] = new Thread(() -> filosofoDeadlock(id));
            filosofos[i].start();
        }
        
        try {
            Thread.sleep(3000);
            
            System.out.println("\nDeadlock");
            System.out.println("\nEstado final dos filósofos:");
            for (int i = 0; i < N; i++) {
                System.out.printf("Filósofo %d: %s%n", i, estados[i]);
            }
            System.out.println("\nTodos estão com fome, segurando um garfo e aguardando outro.");
            System.out.println("Ninguém pode progredir");
            System.exit(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    static void filosofoDeadlock(int id) {
        int esquerda = id;
        int direita = (id + 1) % N;
        
        try {
            estados[id] = "pensando";
            Thread.sleep(100);
            
            estados[id] = "com fome";
            System.out.printf("[Filósofo %d] Com fome, tentando pegar garfos %d e %d%n", 
                            id, esquerda, direita);
            
            garfos[esquerda].acquire();
            System.out.printf("[Filósofo %d] Pegou garfo %d (esquerda)%n", id, esquerda);
            
            Thread.sleep(100);
            
            System.out.printf("[Filósofo %d] Tentando pegar garfo %d (direita)...%n", id, direita);
            garfos[direita].acquire();
            
            estados[id] = "comendo";
            System.out.printf("[Filósofo %d] Comendo%n", id);
            Thread.sleep(200);
            
            garfos[direita].release();
            garfos[esquerda].release();
            estados[id] = "pensando";
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
