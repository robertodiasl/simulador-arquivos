import java.io.Serializable;

public class Arquivo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String conteudo;

    public Arquivo(String nome) {
        this.nome = nome;
        this.conteudo = "";
    }

    public Arquivo(String nome, String conteudo) {
        this.nome = nome;
        this.conteudo = conteudo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public int getTamanho() {
        return conteudo.length();
    }
}
