public class DeadlockCorrigido {
    static final Object recursoA = new Object();
    static final Object recursoB = new Object();
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("Deadlock com Hierarquia");
        System.out.println("Ambas as threads agora adquirem A antes de B\n");
        
        Thread thread1 = new Thread(() -> {
            synchronized (recursoA) {
                System.out.println("[Thread 1] Adquiriu recurso A");
                
                aguardar(100);
                
                synchronized (recursoB) {
                    System.out.println("[Thread 1] Adquiriu recurso B");
                    System.out.println("[Thread 1] Trabalho concluído!");
                }
                System.out.println("[Thread 1] Liberou recurso B");
            }
            System.out.println("[Thread 1] Liberou recurso A");
        }, "Thread-1");
        
        Thread thread2 = new Thread(() -> {
            synchronized (recursoA) {
                System.out.println("[Thread 2] Adquiriu recurso A");
                
                aguardar(100);
                
                synchronized (recursoB) {
                    System.out.println("[Thread 2] Adquiriu recurso B");
                    System.out.println("[Thread 2] Trabalho concluído!");
                }
                System.out.println("[Thread 2] Liberou recurso B");
            }
            System.out.println("[Thread 2] Liberou recurso A");
        }, "Thread-2");
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        System.out.println("\nExecução concluída");
        System.out.println("A hierarquia de recursos eliminou a espera circular.");
    }
    
    static void aguardar(long milissegundos) {
        try {
            Thread.sleep(milissegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
