public class Deadlock {
    static final Object recursoA = new Object();
    static final Object recursoB = new Object();
    
    public static void main(String[] args) {
        System.out.println("Demonstração de Deadlock");
        System.out.println("Iniciando...\n");
        
        Thread thread1 = new Thread(() -> {
            synchronized (recursoA) {
                System.out.println("[Thread 1] Adquiriu recurso A");
                System.out.println("[Thread 1] Tentando adquirir recurso B...");
                
                aguardar(100);
                
                synchronized (recursoB) {
                    System.out.println("[Thread 1] Adquiriu recurso B");
                    System.out.println("[Thread 1] Trabalho concluído!");
                }
            }
        }, "Thread-1");
        
        Thread thread2 = new Thread(() -> {
            synchronized (recursoB) {
                System.out.println("[Thread 2] Adquiriu recurso B");
                System.out.println("[Thread 2] Tentando adquirir recurso A...");
                
                aguardar(100);
                
                synchronized (recursoA) {
                    System.out.println("[Thread 2] Adquiriu recurso A");
                    System.out.println("[Thread 2] Trabalho concluído!");
                }
            }
        }, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        try {
            Thread.sleep(3000);
            
            if (thread1.isAlive() || thread2.isAlive()) {
                System.out.println("\nDEADLOCK");
                System.out.println("As threads estão travadas em espera circular.");
                System.out.println("Thread 1 possui A e aguarda B");
                System.out.println("Thread 2 possui B e aguarda A");
                System.out.println("\nEncerrando...");
                System.exit(1);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    static void aguardar(long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
