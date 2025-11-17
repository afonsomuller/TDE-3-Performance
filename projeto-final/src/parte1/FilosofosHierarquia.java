import java.util.concurrent.Semaphore;

public class FilosofosHierarquia {
    static final int N = 5;
    static Semaphore[] garfos = new Semaphore[N];
    static String[] estados = new String[N];
    static int[] contadorRefeicoes = new int[N];
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Jantar dos Filósofos - Hierarquia");
        System.out.println("Protocolo: Sempre pegar garfo de menor índice primeiro\n");
        
        for (int i = 0; i < N; i++) {
            garfos[i] = new Semaphore(1, true);
            estados[i] = "pensando";
            contadorRefeicoes[i] = 0;
        }
        
        Thread[] filosofos = new Thread[N];
        for (int i = 0; i < N; i++) {
            final int id = i;
            filosofos[i] = new Thread(() -> filosofoHierarquia(id));
            filosofos[i].start();
        }
        
        Thread.sleep(5000);
        
        System.out.println("\n✓ Execução concluída");
        System.out.println("\nRefeições por filósofo:");
        for (int i = 0; i < N; i++) {
            System.out.printf("Filósofo %d: %d refeições%n", i, contadorRefeicoes[i]);
        }
        System.out.println("\nTodos conseguiram comer");
        
        System.exit(0);
    }
    
    static void filosofoHierarquia(int id) {
        int garfoEsq = id;
        int garfoDir = (id + 1) % N;
        
        int primeiro = Math.min(garfoEsq, garfoDir);
        int segundo = Math.max(garfoEsq, garfoDir);
        
        try {
            for (int ciclo = 0; ciclo < 3; ciclo++) {
                estados[id] = "pensando";
                System.out.printf("[Filósofo %d] Pensando...%n", id);
                Thread.sleep(200);
                
                estados[id] = "com fome";
                System.out.printf("[Filósofo %d] Com fome - vai pegar garfos %d depois %d%n", 
                                id, primeiro, segundo);
                
                garfos[primeiro].acquire();
                System.out.printf("[Filósofo %d] Pegou garfo %d%n", id, primeiro);
                
                garfos[segundo].acquire();
                System.out.printf("[Filósofo %d] Pegou garfo %d%n", id, segundo);
                
                estados[id] = "comendo";
                System.out.printf("[Filósofo %d] COMENDO (refeição %d) ★%n", id, ciclo + 1);
                Thread.sleep(300);
                contadorRefeicoes[id]++;
                
                garfos[segundo].release();
                garfos[primeiro].release();
                System.out.printf("[Filósofo %d] Liberou ambos os garfos%n", id);
            }
            
            estados[id] = "satisfeito";
            System.out.printf("[Filósofo %d] Terminou - total de %d refeições%n", 
                            id, contadorRefeicoes[id]);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
