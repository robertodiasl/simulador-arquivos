import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Diretorio implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private Diretorio pai;
    private List<Arquivo> arquivos;
    private List<Diretorio> subDiretorios;

    public Diretorio(String nome, Diretorio pai) {
        this.nome = nome;
        this.pai = pai;
        this.arquivos = new ArrayList<>();
        this.subDiretorios = new ArrayList<>();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Diretorio getPai() {
        return pai;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public List<Diretorio> getSubDiretorios() {
        return subDiretorios;
    }

    public Arquivo buscarArquivo(String nome) {
        for (Arquivo a : arquivos) {
            if (a.getNome().equals(nome)) {
                return a;
            }
        }
        return null;
    }

    public Diretorio buscarSubDiretorio(String nome) {
        for (Diretorio d : subDiretorios) {
            if (d.getNome().equals(nome)) {
                return d;
            }
        }
        return null;
    }

    public String getCaminho() {
        if (pai == null) {
            return "/";
        }
        String cam = pai.getCaminho();
        if (cam.equals("/")) {
            return "/" + nome;
        }
        return cam + "/" + nome;
    }
}
