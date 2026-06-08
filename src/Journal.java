import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Journal {

    private String caminhoLog;

    public Journal(String caminhoLog) {
        this.caminhoLog = caminhoLog;
        try {
            File f = new File(caminhoLog);
            if (!f.exists()) {
                f.createNewFile();
            }
        } catch (IOException e) {
            System.out.println("Erro ao criar o journal: " + e.getMessage());
        }
    }

    private void escrever(String tipo, String detalhes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(caminhoLog, true))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String linha = "[" + sdf.format(new Date()) + "] " + tipo + " | " + detalhes;
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Erro ao gravar no journal: " + e.getMessage());
        }
    }

    public void inicio(String operacao) {
        escrever("BEGIN", operacao);
    }

    public void commit(String operacao) {
        escrever("COMMIT", operacao);
    }

    // verifica se ficou alguma operacao sem COMMIT (caso o sistema tenha caido no meio)
    public void verificarPendentes() {
        try {
            List<String> linhas = Files.readAllLines(Paths.get(caminhoLog));
            if (linhas.isEmpty()) {
                return;
            }
            int begins = 0;
            int commits = 0;
            for (String l : linhas) {
                if (l.contains("] BEGIN |")) begins++;
                if (l.contains("] COMMIT |")) commits++;
            }
            if (begins > commits) {
                System.out.println("Aviso: foram encontradas " + (begins - commits) +
                        " operacao(oes) sem COMMIT no journal (provavel queda anterior).");
            }
        } catch (IOException e) {
            System.out.println("Nao foi possivel ler o journal: " + e.getMessage());
        }
    }
}
