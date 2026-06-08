import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileSystemSimulator {

    private Diretorio raiz;
    private Diretorio atual;
    private Journal journal;
    private String arquivoDisco;

    public FileSystemSimulator(String arquivoDisco, String arquivoJournal) {
        this.arquivoDisco = arquivoDisco;
        this.journal = new Journal(arquivoJournal);
        this.journal.verificarPendentes();
        carregar();
    }

    private void carregar() {
        File f = new File(arquivoDisco);
        if (!f.exists()) {
            raiz = new Diretorio("/", null);
            atual = raiz;
            salvar();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            raiz = (Diretorio) ois.readObject();
            atual = raiz;
        } catch (Exception e) {
            System.out.println("Erro ao carregar o disco, criando um novo: " + e.getMessage());
            raiz = new Diretorio("/", null);
            atual = raiz;
        }
    }

    private void salvar() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivoDisco))) {
            oos.writeObject(raiz);
        } catch (IOException e) {
            System.out.println("Erro ao salvar o disco: " + e.getMessage());
        }
    }

    public String getCaminhoAtual() {
        return atual.getCaminho();
    }

    public void criarArquivo(String nome) {
        String op = "criar_arquivo " + nome + " em " + atual.getCaminho();
        journal.inicio(op);
        if (atual.buscarArquivo(nome) != null) {
            System.out.println("Ja existe um arquivo com esse nome: " + nome);
            return;
        }
        atual.getArquivos().add(new Arquivo(nome));
        salvar();
        journal.commit(op);
        System.out.println("Arquivo criado: " + nome);
    }

    public void apagarArquivo(String nome) {
        String op = "apagar_arquivo " + nome + " em " + atual.getCaminho();
        journal.inicio(op);
        Arquivo a = atual.buscarArquivo(nome);
        if (a == null) {
            System.out.println("Arquivo nao encontrado: " + nome);
            return;
        }
        atual.getArquivos().remove(a);
        salvar();
        journal.commit(op);
        System.out.println("Arquivo apagado: " + nome);
    }

    public void renomearArquivo(String antigo, String novo) {
        String op = "renomear_arquivo " + antigo + " -> " + novo + " em " + atual.getCaminho();
        journal.inicio(op);
        Arquivo a = atual.buscarArquivo(antigo);
        if (a == null) {
            System.out.println("Arquivo nao encontrado: " + antigo);
            return;
        }
        if (atual.buscarArquivo(novo) != null) {
            System.out.println("Ja existe um arquivo com o nome: " + novo);
            return;
        }
        a.setNome(novo);
        salvar();
        journal.commit(op);
        System.out.println("Arquivo renomeado para: " + novo);
    }

    public void copiarArquivo(String origem, String destino) {
        String op = "copiar_arquivo " + origem + " -> " + destino + " em " + atual.getCaminho();
        journal.inicio(op);
        Arquivo a = atual.buscarArquivo(origem);
        if (a == null) {
            System.out.println("Arquivo de origem nao encontrado: " + origem);
            return;
        }
        if (atual.buscarArquivo(destino) != null) {
            System.out.println("Ja existe um arquivo com o nome do destino: " + destino);
            return;
        }
        Arquivo copia = new Arquivo(destino, a.getConteudo());
        atual.getArquivos().add(copia);
        salvar();
        journal.commit(op);
        System.out.println("Arquivo copiado de " + origem + " para " + destino);
    }

    public void escreverArquivo(String nome, String texto) {
        String op = "escrever_arquivo " + nome + " em " + atual.getCaminho();
        journal.inicio(op);
        Arquivo a = atual.buscarArquivo(nome);
        if (a == null) {
            System.out.println("Arquivo nao encontrado: " + nome);
            return;
        }
        a.setConteudo(texto);
        salvar();
        journal.commit(op);
        System.out.println("Conteudo gravado em: " + nome);
    }

    public void lerArquivo(String nome) {
        Arquivo a = atual.buscarArquivo(nome);
        if (a == null) {
            System.out.println("Arquivo nao encontrado: " + nome);
            return;
        }
        System.out.println("--- " + nome + " ---");
        System.out.println(a.getConteudo());
        System.out.println("--- fim ---");
    }

    public void criarDiretorio(String nome) {
        String op = "criar_diretorio " + nome + " em " + atual.getCaminho();
        journal.inicio(op);
        if (atual.buscarSubDiretorio(nome) != null) {
            System.out.println("Ja existe um diretorio com esse nome: " + nome);
            return;
        }
        atual.getSubDiretorios().add(new Diretorio(nome, atual));
        salvar();
        journal.commit(op);
        System.out.println("Diretorio criado: " + nome);
    }

    public void apagarDiretorio(String nome) {
        String op = "apagar_diretorio " + nome + " em " + atual.getCaminho();
        journal.inicio(op);
        Diretorio d = atual.buscarSubDiretorio(nome);
        if (d == null) {
            System.out.println("Diretorio nao encontrado: " + nome);
            return;
        }
        atual.getSubDiretorios().remove(d);
        salvar();
        journal.commit(op);
        System.out.println("Diretorio apagado: " + nome);
    }

    public void renomearDiretorio(String antigo, String novo) {
        String op = "renomear_diretorio " + antigo + " -> " + novo + " em " + atual.getCaminho();
        journal.inicio(op);
        Diretorio d = atual.buscarSubDiretorio(antigo);
        if (d == null) {
            System.out.println("Diretorio nao encontrado: " + antigo);
            return;
        }
        if (atual.buscarSubDiretorio(novo) != null) {
            System.out.println("Ja existe um diretorio com o nome: " + novo);
            return;
        }
        d.setNome(novo);
        salvar();
        journal.commit(op);
        System.out.println("Diretorio renomeado para: " + novo);
    }

    public void listar() {
        System.out.println("Conteudo de " + atual.getCaminho() + ":");
        if (atual.getSubDiretorios().isEmpty() && atual.getArquivos().isEmpty()) {
            System.out.println("  (vazio)");
            return;
        }
        for (Diretorio d : atual.getSubDiretorios()) {
            System.out.println("  [DIR] " + d.getNome());
        }
        for (Arquivo a : atual.getArquivos()) {
            System.out.println("  [ARQ] " + a.getNome() + "  (" + a.getTamanho() + " bytes)");
        }
    }

    public void entrar(String nome) {
        if (nome.equals("..")) {
            if (atual.getPai() != null) {
                atual = atual.getPai();
            }
            return;
        }
        if (nome.equals("/")) {
            atual = raiz;
            return;
        }
        Diretorio d = atual.buscarSubDiretorio(nome);
        if (d == null) {
            System.out.println("Diretorio nao encontrado: " + nome);
            return;
        }
        atual = d;
    }
}
