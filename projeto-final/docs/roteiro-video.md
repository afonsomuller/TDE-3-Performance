# Roteiro para Gravação do Vídeo
## Trabalho de Programação Concorrente

**Duração total:** 8-10 minutos  

---

## Introdução e Jantar dos Filósofos (3 minutos)

### Abertura (30s)

"Olá, pessoal. Neste vídeo vamos apresentar nosso trabalho sobre programação concorrente, focando em três problemas clássicos: o Jantar dos Filósofos, condições de corrida e deadlock. Esses são problemas fundamentais que aparecem quando temos múltiplas threads compartilhando recursos."

### O Problema dos Filósofos (1min 30s)

"Vou começar falando sobre o Jantar dos Filósofos. Imaginem cinco filósofos sentados numa mesa circular. Entre cada par de filósofos tem um garfo. Cada filósofo passa o tempo pensando e, quando fica com fome, precisa dos dois garfos ao lado dele pra comer."

"O protocolo mais simples seria: pego o garfo da esquerda, depois pego o da direita, como, e devolvo os garfos."

[Executar FilosofosHierarquia.java na tela]

"Parece funcionar, mas tem um problema sério. Se todos os cinco filósofos tentarem comer ao mesmo tempo, cada um vai pegar o garfo da esquerda. Agora todos estão segurando um garfo e esperando pelo garfo da direita, que tá na mão do vizinho. Ninguém consegue progredir. Isso é um deadlock."

[Executar FilosofosDeadlock.java na tela]

### Condições de Coffman (1min)

"Esse deadlock acontece porque quatro condições estão presentes ao mesmo tempo. São as chamadas Condições de Coffman."

"Primeira: exclusão mútua. O garfo só pode ser usado por uma pessoa de cada vez."

"Segunda: manter e esperar. O filósofo segura um garfo enquanto aguarda o outro."

"Terceira: não preempção. Você não pode arrancar o garfo da mão de alguém."

"E quarta: espera circular. Forma-se um ciclo onde cada um aguarda o próximo."

"Nossa solução usa hierarquia de recursos. Numeramos os garfos de zero a quatro e forçamos todo mundo a sempre pegar primeiro o garfo de menor número. Isso quebra a espera circular, porque não tem como formar um ciclo de espera quando todo mundo segue a mesma ordem de aquisição."

"No pseudocódigo que tá no relatório, cada filósofo calcula qual garfo é o menor e qual é o maior, e sempre pega nessa ordem. Simples e efetivo."

---

## Condições de Corrida e Semáforos (3-4 minutos)

### Introdução ao Problema (1min)

"Agora vou falar sobre outro problema clássico: a condição de corrida. Criamos um contador compartilhado que várias threads incrementam ao mesmo tempo."

"A operação contador++ parece simples, mas no processador ela tem três passos: carregar o valor da memória, somar um, e gravar de volta. Quando duas threads fazem isso ao mesmo tempo, pode dar problema."

"Por exemplo, se duas threads leem o valor 100 ao mesmo tempo, as duas calculam 101, e as duas gravam 101. Resultado: fizemos dois incrementos mas o contador só subiu uma vez. Um incremento foi perdido."

### Demonstração do Código (1min 30s)

"Vou mostrar nosso código rodando. No ContadorDeadlock, temos oito threads, cada uma fazendo 250 mil incrementos. O valor esperado seria dois milhões."

[Executar ContadorDeadlock.java na tela]

"Olha só o resultado. O valor obtido foi bem menor que dois milhões. Essa diferença representa os incrementos que foram perdidos por causa da race condition."

"Agora vou rodar a versão corrigida, que usa um semáforo."

[Executar ContadorSemaforo.java na tela]

"Aqui o resultado é exatamente dois milhões. Todos os incrementos foram preservados. Mas olhem o tempo de execução, bem maior que a versão sem sincronização."

### Explicação do Semáforo (1min)

"O semáforo funciona como um porteiro controlando quem pode entrar na seção crítica. Iniciamos ele com valor um, que significa uma permissão disponível."

"Quando uma thread chama acquire, ela pega essa permissão. Se outra thread tentar acquire, ela fica bloqueada esperando. Quando a primeira thread termina e chama release, ela devolve a permissão, e a próxima thread pode prosseguir."

"Usamos o modo fair igual a true, que significa justo. Isso garante que threads são atendidas na ordem que chegaram, como uma fila normal. Ninguém fica esperando pra sempre."

"O semáforo também garante visibilidade de memória. Tudo que foi escrito antes do release fica visível pra thread que faz o acquire depois. Isso resolve problemas de cache e ordem de execução."

"O preço disso é performance. Como só uma thread pode incrementar por vez, perdemos o paralelismo. Mas esse é o custo da correção. Em aplicações reais, a gente minimiza o tamanho da seção crítica pra reduzir esse impacto."

---

## Deadlock com Múltiplos Locks (3-4 minutos)

### O Problema (1min)

"Vou fechar falando sobre deadlock com múltiplos locks. Esse é um problema que pode travar um sistema inteiro se não for bem tratado."

"Nosso cenário tem duas threads e dois recursos, A e B. A Thread 1 pega A e depois tenta pegar B. A Thread 2 pega B e depois tenta pegar A."

"O que pode acontecer? Se a Thread 1 conseguir A e a Thread 2 conseguir B ao mesmo tempo, a Thread 1 fica travada esperando B, e a Thread 2 fica travada esperando A. Nenhuma das duas pode progredir. Deadlock completo."

### Demonstração (1min 30s)

"Vou executar o Deadlock pra vocês verem isso acontecer."

[Executar Deadlock.java na tela]

"Vejam que a Thread 1 adquiriu o recurso A e tá tentando adquirir B. A Thread 2 adquiriu B e tá tentando adquirir A. E agora... nada. O programa trava. Depois de três segundos, nosso código detecta o deadlock e encerra."

"Isso satisfaz todas as quatro condições de Coffman que já foram mencionadas. Exclusão mútua nos locks, manter e esperar porque cada thread segura um recurso enquanto aguarda outro, não preempção porque não dá pra tirar um lock à força, e espera circular porque temos um ciclo: Thread 1 espera Thread 2 que espera Thread 1."

### A Solução (1min 30s)

"A correção é aplicar o mesmo princípio dos filósofos: hierarquia de recursos. Forçamos ambas as threads a adquirirem os recursos na mesma ordem."

"No DeadlockCorrigido, tanto a Thread 1 quanto a Thread 2 pegam primeiro A, depois B. Sempre nessa ordem."

[Executar DeadlockCorrigido.java na tela]

"Olhem agora. A Thread 1 adquire A, depois B, executa e libera tudo. Depois a Thread 2 faz o mesmo. Nenhum travamento. Execução completa."

"Por que funciona? Porque não tem como formar espera circular quando todo mundo segue a mesma ordem. Se a Thread 1 pegou A primeiro, a Thread 2 vai esperar por A. Quando a Thread 1 liberar ambos os recursos, a Thread 2 pode pegar os dois. Nunca vai ter uma situação onde uma tem A esperando B enquanto outra tem B esperando A."

### Conclusão (30s)

"Pra finalizar, esses três problemas mostram a importância da sincronização correta em programação concorrente. Seja no Jantar dos Filósofos, no contador compartilhado, ou no deadlock explícito, a solução passa por entender as condições que causam esses problemas e aplicar estratégias que quebrem essas condições."

"A hierarquia de recursos é especialmente poderosa porque elimina a espera circular mantendo as outras condições. É simples de implementar e efetiva na prática."

"Todo o código fonte, o relatório completo e o link deste vídeo estão no nosso repositório do GitHub. Obrigado pela atenção!"

---

