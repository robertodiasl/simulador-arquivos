# Simulador de Sistema de Arquivos

**Disciplina:** FUND SIST COMPUTACIONAIS
**Alunos:** Roberto Dias (2315748) e Carlos Yan (2315769)

Repositório no GitHub: https://github.com/robertodiasl/simulador-arquivos

## Metodologia

O simulador foi desenvolvido em linguagem de programação Java. Ele recebe as
chamadas de métodos com os devidos parâmetros. Em seguida, foram implementados
os métodos correspondentes aos comandos de um SO.

O programa executa cada funcionalidade e exibe o resultado na tela quando
necessário.

## Parte 1: Introdução ao Sistema de Arquivos com Journaling

### Descrição do sistema de arquivos

Um sistema de arquivos é a parte do sistema operacional responsável por
organizar e gerenciar como os dados são armazenados em uma mídia de
armazenamento como HD, SSD ou pen drive. Ele define como os arquivos são
nomeados, onde ficam guardados e como podem ser encontrados, lidos, escritos,
copiados ou apagados.

Sua importância está em dar sentido aos dados gravados no disco. Sem um
sistema de arquivos, o que existe na mídia é apenas uma sequência de bits.
É o sistema de arquivos que organiza esses bits em uma estrutura de arquivos
e pastas que o usuário consegue manipular. Exemplos de sistemas de arquivos
reais são o NTFS (Windows), o ext4 (Linux), o APFS (MacOS) e o FAT32.

O conceito de journaling surgiu como uma forma de resolver o problema da
inconsistência de dados que pode acontecer quando o sistema é desligado de
forma inesperada no meio de uma operação. Em vez de aplicar a mudança
diretamente no disco, o sistema primeiro registra em um log o que pretende
fazer, e só depois efetiva a alteração. Isso permite que o sistema seja
recuperado em caso de falha.

### Journaling

O propósito do journaling é proteger o sistema de arquivos contra corrupção
de dados em situações de queda de energia, travamento ou desligamento
incorreto. Seu funcionamento se baseia em manter um arquivo de log (o
journal) onde são registradas as operações antes delas serem aplicadas no
disco. Depois que uma operação é concluída, o log recebe uma marcação
indicando que ela terminou com sucesso.

Caso o sistema seja interrompido no meio de uma operação, na próxima vez que
for iniciado basta analisar o journal: as operações que foram registradas
mas não foram marcadas como concluídas podem ser desfeitas ou refeitas,
fazendo com que o sistema de arquivos volte a um estado consistente.

Existem diferentes tipos de journaling:

- **Write-ahead logging (WAL):** o sistema grava a operação no log antes de
  aplicá-la no disco. É o tipo mais comum e o adotado neste simulador.
- **Log-structured:** todo o disco é tratado como um log. As escritas são
  sempre feitas no final do log, e o sistema mantém estruturas auxiliares
  para localizar a versão mais recente de cada bloco.
- **Journaling de metadados:** somente as alterações nos metadados (nomes,
  datas, estrutura de pastas) são registradas no log. É mais rápido, mas
  não protege contra a corrupção do conteúdo dos arquivos.
- **Journaling completo:** tanto os metadados quanto o conteúdo dos
  arquivos são registrados no log. Garante mais segurança em troca de
  desempenho.

## Parte 2: Arquitetura do Simulador

### Estrutura de Dados

As estruturas de dados utilizadas para representar o sistema de arquivos
foram implementadas como classes Java. Foram criadas classes para
representar arquivos, diretórios e o sistema de arquivos em si.

A classe que representa um arquivo guarda o nome e o conteúdo (como texto).
A classe que representa um diretório guarda o nome, a referência para o
diretório pai e duas listas, uma com os arquivos contidos nele e outra com
os subdiretórios. Já a classe que representa o sistema de arquivos como um
todo mantém o diretório raiz, o diretório atual em que o usuário está e a
referência para o journal.

Ambas as classes de arquivo e diretório implementam a interface
Serializable do Java, o que permite que toda a árvore de diretórios e
arquivos seja gravada em um único arquivo no disco com uma única chamada
de ObjectOutputStream. Esse arquivo (`disco.fs`) é regravado a cada
operação e relido quando o simulador é aberto.

### Journaling

O journaling foi implementado no modelo write-ahead logging. O log fica
em um arquivo separado, chamado `disco.journal`, e cada operação gera duas
entradas nesse arquivo:

```
[data_hora] BEGIN  | descricao_da_operacao
[data_hora] COMMIT | descricao_da_operacao
```

A linha BEGIN é gravada antes da modificação acontecer. Em seguida o
simulador aplica a alteração no estado e regrava o `disco.fs`. Só depois
disso a linha COMMIT é escrita no log, confirmando que a operação terminou.
Quando o simulador é iniciado, ele verifica se existem entradas BEGIN sem
o COMMIT correspondente, o que indicaria que uma operação foi interrompida
em uma execução anterior. Nesse caso, o usuário é avisado.

As operações registradas no journal são:

- criar_arquivo
- apagar_arquivo
- renomear_arquivo
- copiar_arquivo
- escrever_arquivo
- criar_diretorio
- apagar_diretorio
- renomear_diretorio

## Parte 3: Implementação em Java

### Classe "FileSystemSimulator"

A classe FileSystemSimulator implementa o simulador do sistema de arquivos
em si. Ela mantém a referência para o diretório raiz, o diretório atual e
o journal, e disponibiliza os métodos correspondentes a cada operação:

- criarArquivo
- apagarArquivo
- renomearArquivo
- copiarArquivo
- criarDiretorio
- apagarDiretorio
- renomearDiretorio
- listar
- entrar (equivalente ao cd)
- escreverArquivo e lerArquivo

Em cada um desses métodos, a operação é registrada no journal antes de ser
aplicada, o estado em memória é atualizado, o arquivo `disco.fs` é regravado
e por fim a operação é marcada como concluída no journal.

### Classes File e Directory

Essas classes representam, respectivamente, um arquivo e um diretório dentro
do simulador. Como o Java já possui uma classe nativa chamada File no pacote
java.io, neste projeto elas foram nomeadas em português, como Arquivo e
Diretorio, para evitar conflito de nomes, mas exercem exatamente o mesmo
papel descrito no enunciado.

A classe Arquivo armazena o nome e o conteúdo do arquivo, além de métodos
para acessar e modificar essas informações. A classe Diretorio armazena o
nome do diretório, a referência para o diretório pai e as listas de arquivos
e subdiretórios contidos. Também oferece métodos auxiliares para procurar
arquivos e subdiretórios pelo nome e para montar o caminho completo do
diretório (por exemplo, /pasta/subpasta).

### Classe Journal

A classe Journal é responsável por gerenciar o log de operações. Ela escreve
no arquivo `disco.journal` cada vez que uma operação é iniciada (BEGIN) e
cada vez que uma operação é concluída (COMMIT), incluindo a data, a hora e
a descrição do que foi feito.

Além disso, ela tem um método de verificação que é executado ao iniciar o
simulador. Esse método percorre o log e conta quantas entradas BEGIN não
têm a entrada COMMIT correspondente. Se encontrar alguma, exibe um aviso
informando que houve uma operação interrompida em uma execução anterior.

## Parte 4: Instalação e funcionamento

Para utilizar o simulador é necessário ter instalado o Java JDK na versão 8
ou superior. Não são utilizadas bibliotecas externas, apenas as bibliotecas
padrão do Java (java.io, java.nio.file, java.util e java.text).

O passo a passo para executar o simulador é:

1. Baixar o projeto do repositório do GitHub.
2. Abrir um terminal na pasta do projeto.
3. Entrar na pasta `src` com o comando `cd src`.
4. Compilar os arquivos Java com o comando `javac *.java`.
5. Executar o programa com o comando `java Main`.

Após a execução, o simulador abre um shell interativo, no qual o usuário
pode digitar os comandos. Os arquivos `disco.fs` e `disco.journal` são
criados automaticamente na pasta onde o programa foi executado e
permanecem entre execuções, o que faz com que o estado do sistema de
arquivos seja preservado.

Os comandos disponíveis no shell são:

| Comando | Descrição |
|---------|-----------|
| `ls` | Lista o conteúdo do diretório atual |
| `cd <nome>` | Entra em um diretório (`cd ..` volta, `cd /` vai para a raiz) |
| `mkdir <nome>` | Cria um diretório |
| `rmdir <nome>` | Apaga um diretório |
| `renamedir <antigo> <novo>` | Renomeia um diretório |
| `touch <nome>` | Cria um arquivo vazio |
| `rm <nome>` | Apaga um arquivo |
| `rename <antigo> <novo>` | Renomeia um arquivo |
| `cp <origem> <destino>` | Copia um arquivo |
| `write <arquivo> <texto>` | Grava texto em um arquivo |
| `cat <arquivo>` | Mostra o conteúdo de um arquivo |
| `ajuda` | Mostra a lista de comandos |
| `sair` | Encerra o simulador |

## Resultados Esperados

Espera-se que o simulador forneça insights sobre o funcionamento de um
sistema de arquivos. Com base nos resultados obtidos, poderemos avaliar e
entender como funciona esse elemento de um SO.
