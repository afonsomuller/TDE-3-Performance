# Trabalho Prático: Programação Concorrente
## Jantar dos Filósofos, Semáforos e Deadlock

---

## 1. Introdução

Este trabalho explora três problemas fundamentais em programação concorrente: o clássico problema do Jantar dos Filósofos, condições de corrida em contadores compartilhados, e situações de deadlock. O objetivo é demonstrar como esses problemas surgem e implementar soluções práticas usando Java.

A programação concorrente apresenta desafios únicos quando múltiplas threads compartilham recursos. Sem mecanismos adequados de sincronização, podemos ter perda de dados, travamentos e comportamentos imprevisíveis. Este trabalho analisa esses cenários e propõe soluções fundamentadas em conceitos teóricos de sistemas operacionais.

---

## 2. Parte 1: O Jantar dos Filósofos

### 2.1 O Problema

O Jantar dos Filósofos é um problema clássico proposto por Edsger Dijkstra em 1965. Ele modela uma situação onde cinco filósofos sentam-se ao redor de uma mesa circular. Entre cada par de filósofos há um garfo. Cada filósofo alterna entre dois estados: pensar e comer. Para comer, um filósofo precisa dos dois garfos adjacentes a ele (o da esquerda e o da direita).

O problema surge quando todos os filósofos tentam comer ao mesmo tempo usando um protocolo simples:
1. Pegar o garfo da esquerda
2. Pegar o garfo da direita
3. Comer
4. Devolver os garfos

### 2.2 Por que o Deadlock Ocorre

Se todos os cinco filósofos executarem o passo 1 simultaneamente, cada um segurará seu garfo esquerdo e aguardará o garfo direito, que está sendo segurado pelo vizinho. Ninguém pode progredir porque todos estão esperando. Esta é a definição exata de deadlock.

Este cenário satisfaz as **quatro condições de Coffman** necessárias para deadlock:

1. **Exclusão Mútua:** Cada garfo só pode ser usado por um filósofo por vez
2. **Manter e Esperar:** Um filósofo segura um garfo enquanto aguarda outro
3. **Não Preempção:** Um garfo não pode ser tomado à força de um filósofo
4. **Espera Circular:** Forma-se um ciclo onde cada filósofo aguarda o próximo

### 2.3 Solução: Hierarquia de Recursos

Para evitar o deadlock, implementamos uma **hierarquia de recursos**. A ideia é simples: atribuímos um número único a cada garfo (0, 1, 2, 3, 4) e forçamos todos os filósofos a sempre pegarem primeiro o garfo de menor número, depois o de maior número.

#### Por que isso funciona?

Esta estratégia **quebra a condição de espera circular**. Vamos analisar:

- O Filósofo 0 está entre os garfos 0 e 4. Ele pega primeiro o 0, depois o 4.
- O Filósofo 1 está entre os garfos 1 e 0. Ele pega primeiro o 0, depois o 1.
- O Filósofo 2 está entre os garfos 2 e 1. Ele pega primeiro o 1, depois o 2.
- O Filósofo 3 está entre os garfos 3 e 2. Ele pega primeiro o 2, depois o 3.
- O Filósofo 4 está entre os garfos 4 e 3. Ele pega primeiro o 3, depois o 4.

Com essa ordem, não é possível formar um ciclo de espera. Se o Filósofo 0 tem o garfo 0, o Filósofo 1 não pode pegá-lo e fica bloqueado antes de pegar qualquer recurso. Isso impede que se forme uma cadeia circular de esperas.

### 2.4 Pseudocódigo da Solução

```
DADOS:
  N = 5 filósofos
  garfos[0..4] são semáforos binários
  
PARA cada filósofo p de 0 até 4:
  
  garfo_esq = p
  garfo_dir = (p + 1) mod N
  
  primeiro = min(garfo_esq, garfo_dir)
  segundo = max(garfo_esq, garfo_dir)
  
  LOOP infinito:
    estado[p] = "pensando"
    pensar()
    
    estado[p] = "com fome"
    
    adquirir(garfos[primeiro])
    adquirir(garfos[segundo])
    
    estado[p] = "comendo"
    comer()
    
    liberar(garfos[segundo])
    liberar(garfos[primeiro])
```

### 2.5 Preservação de Justiça e Progresso

A solução garante:
- **Progresso:** Sempre que um filósofo quer comer, eventualmente conseguirá os dois garfos
- **Justiça:** Usando semáforos em modo justo (FIFO), quem espera mais tempo é atendido primeiro
- **Ausência de Deadlock:** A hierarquia elimina a espera circular
- **Ausência de Starvation:** Com semáforos justos, nenhum filósofo fica eternamente sem comer

---

## 3. Parte 2: Condições de Corrida e Semáforos

### 3.1 O Problema do Contador Compartilhado

Uma condição de corrida ocorre quando múltiplas threads acessam dados compartilhados sem sincronização adequada, e o resultado final depende da ordem de execução das threads.

O exemplo clássico é um contador simples. A operação `contador++` parece atômica, mas na verdade envolve três passos no nível de máquina:

1. **LOAD:** Carregar o valor atual da memória para um registrador
2. **ADD:** Incrementar o valor no registrador
3. **STORE:** Gravar o novo valor de volta na memória

Quando duas threads executam isso simultaneamente, pode acontecer o seguinte:

```
Thread A: LOAD (lê 100)
Thread B: LOAD (lê 100)
Thread A: ADD  (calcula 101)
Thread B: ADD  (calcula 101)
Thread A: STORE (grava 101)
Thread B: STORE (grava 101)
```

Resultado: dois incrementos foram executados, mas o contador só aumentou de 100 para 101. Um incremento foi perdido!

### 3.2 Implementação e Resultados

Implementamos dois programas em Java:

#### ContadorSemSincronizacao.java
- 8 threads
- Cada uma executa 250.000 incrementos
- Total esperado: 2.000.000
- **Resultado típico: 1.800.000 ~ 1.950.000**
- Incrementos perdidos devido à race condition

#### ContadorComSemaforo.java
- Mesma configuração
- Usa `Semaphore(1, true)` para exclusão mútua
- **Resultado: exatamente 2.000.000**
- Todos os incrementos preservados

### 3.3 Análise dos Resultados

**Sem sincronização:**
- Tempo de execução: ~0.05 segundos
- Valor incorreto devido à race condition
- Perda de incrementos imprevisível

**Com semáforo:**
- Tempo de execução: ~2.5 segundos
- Valor sempre correto
- Overhead devido à contenção e serialização

### 3.4 Semáforo e Happens-Before

O semáforo em Java garante uma relação **happens-before**: todas as escritas feitas por uma thread antes de chamar `release()` são visíveis para qualquer thread que chamar `acquire()` depois.

Isso resolve dois problemas:
1. **Exclusão mútua:** Apenas uma thread acessa a seção crítica por vez
2. **Visibilidade:** Mudanças feitas por uma thread são vistas por outras

O modo `fair=true` garante que threads aguardando por mais tempo sejam atendidas primeiro, evitando starvation.

### 3.5 Trade-off: Correção vs. Performance

O semáforo introduz um custo:
- **Serialização:** Threads executam sequencialmente na seção crítica
- **Troca de contexto:** Threads bloqueadas deixam a CPU
- **Overhead do sistema:** Gerenciamento de filas de espera

No nosso caso, o tempo aumentou 50x. Mas essa é a garantia necessária para correção. Em aplicações reais, minimizamos o tamanho da seção crítica para reduzir contenção.

---

## 4. Parte 3: Deadlock com Múltiplos Locks

### 4.1 Cenário do Problema

Deadlock ocorre quando threads ficam eternamente bloqueadas, cada uma esperando que a outra libere um recurso. No nosso exemplo:

- **Thread 1:** Adquire A, depois tenta adquirir B
- **Thread 2:** Adquire B, depois tenta adquirir A

Se Thread 1 pegar A e Thread 2 pegar B simultaneamente, ambas ficam travadas esperando o outro recurso.

### 4.2 Condições de Coffman no Código

Analisando `ExemploDeadlock.java`:

1. **Exclusão Mútua:** 
   ```java
   synchronized (recursoA) { ... }
   ```
   Apenas uma thread pode ter o lock por vez

2. **Manter e Esperar:**
   ```java
   synchronized (recursoA) {
       // Thread segura A aqui
       synchronized (recursoB) { // Aguarda B
   ```

3. **Não Preempção:**
   O Java não permite remover locks à força

4. **Espera Circular:**
   Thread 1 (possui A, quer B) → Thread 2 (possui B, quer A) → Thread 1

### 4.3 Solução: Hierarquia de Recursos

Em `DeadlockCorrigido.java`, aplicamos a mesma estratégia do Jantar dos Filósofos: **ambas as threads adquirem os recursos na mesma ordem**.

```java
// Thread 1
synchronized (recursoA) {
    synchronized (recursoB) {
        // trabalho
    }
}

// Thread 2 - MESMA ORDEM
synchronized (recursoA) {
    synchronized (recursoB) {
        // trabalho
    }
}
```

### 4.4 Por que a Correção Funciona

A hierarquia imposta (A antes de B) quebra a **espera circular**:

- Se Thread 1 consegue A primeiro, Thread 2 espera por A
- Quando Thread 1 libera A e B, Thread 2 pode adquirir ambos
- Nunca existe uma situação onde T1 tem A esperando B enquanto T2 tem B esperando A

A condição de espera circular é **necessária** para deadlock. Ao eliminá-la, tornamos o deadlock impossível.

### 4.5 Relação com o Jantar dos Filósofos

As duas soluções aplicam o mesmo princípio:

| Aspecto | Filósofos | Deadlock |
|---------|-----------|----------|
| Recursos | Garfos 0-4 | Locks A e B |
| Problema | Cada um pega esquerda-direita | T1 pega A-B, T2 pega B-A |
| Solução | Sempre pegar menor índice primeiro | Sempre pegar A antes de B |
| Condição quebrada | Espera circular | Espera circular |

Em ambos os casos, uma **ordem global de aquisição** impede ciclos de espera.

---

## 5. Conclusão

Este trabalho demonstrou três problemas fundamentais em programação concorrente:

1. **Jantar dos Filósofos:** Deadlock em alocação de recursos compartilhados, resolvido por hierarquia
2. **Race Conditions:** Perda de dados em acessos concorrentes, resolvido por semáforos
3. **Deadlock Explícito:** Travamento por ordem de aquisição, resolvido por ordem global

Os três problemas estão interligados pelas Condições de Coffman. Para prevenir deadlock, precisamos negar pelo menos uma das quatro condições. A hierarquia de recursos é eficaz porque elimina a espera circular, mantendo as outras três condições mas tornando deadlock impossível.

A programação concorrente exige compreensão profunda desses mecanismos. Semáforos e locks são ferramentas poderosas, mas devem ser usados com disciplina. Uma única inversão na ordem de aquisição pode causar deadlock. Um único acesso sem sincronização pode causar race conditions.

Os códigos desenvolvidos demonstram esses conceitos de forma prática e reproduzível, permitindo observar tanto os problemas quanto suas soluções em execução real.

---
