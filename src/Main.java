import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        FileSystemSimulator fs = new FileSystemSimulator("disco.fs", "disco.journal");
        Scanner sc = new Scanner(System.in);

        System.out.println("=================================================");
        System.out.println(" Simulador de Sistema de Arquivos com Journaling ");
        System.out.println("=================================================");
        System.out.println("Digite 'ajuda' para ver os comandos disponiveis.");
        System.out.println("Digite 'sair' para encerrar.");
        System.out.println();

        while (true) {
            System.out.print(fs.getCaminhoAtual() + " > ");
            if (!sc.hasNextLine()) break;
            String linha = sc.nextLine().trim();
            if (linha.isEmpty()) continue;

            String[] partes = linha.split("\\s+", 3);
            String cmd = partes[0].toLowerCase();

            switch (cmd) {
                case "sair":
                    System.out.println("Encerrando o simulador.");
                    sc.close();
                    return;

                case "ajuda":
                    mostrarAjuda();
                    break;

                case "ls":
                    fs.listar();
                    break;

                case "cd":
                    if (partes.length < 2) {
                        System.out.println("uso: cd <nome>");
                    } else {
                        fs.entrar(partes[1]);
                    }
                    break;

                case "mkdir":
                    if (partes.length < 2) {
                        System.out.println("uso: mkdir <nome>");
                    } else {
                        fs.criarDiretorio(partes[1]);
                    }
                    break;

                case "rmdir":
                    if (partes.length < 2) {
                        System.out.println("uso: rmdir <nome>");
                    } else {
                        fs.apagarDiretorio(partes[1]);
                    }
                    break;

                case "renamedir":
                    if (partes.length < 3) {
                        System.out.println("uso: renamedir <antigo> <novo>");
                    } else {
                        fs.renomearDiretorio(partes[1], partes[2]);
                    }
                    break;

                case "touch":
                    if (partes.length < 2) {
                        System.out.println("uso: touch <nome>");
                    } else {
                        fs.criarArquivo(partes[1]);
                    }
                    break;

                case "rm":
                    if (partes.length < 2) {
                        System.out.println("uso: rm <nome>");
                    } else {
                        fs.apagarArquivo(partes[1]);
                    }
                    break;

                case "rename":
                    if (partes.length < 3) {
                        System.out.println("uso: rename <antigo> <novo>");
                    } else {
                        fs.renomearArquivo(partes[1], partes[2]);
                    }
                    break;

                case "cp":
                    if (partes.length < 3) {
                        System.out.println("uso: cp <origem> <destino>");
                    } else {
                        fs.copiarArquivo(partes[1], partes[2]);
                    }
                    break;

                case "write":
                    if (partes.length < 3) {
                        System.out.println("uso: write <arquivo> <texto>");
                    } else {
                        fs.escreverArquivo(partes[1], partes[2]);
                    }
                    break;

                case "cat":
                    if (partes.length < 2) {
                        System.out.println("uso: cat <arquivo>");
                    } else {
                        fs.lerArquivo(partes[1]);
                    }
                    break;

                default:
                    System.out.println("Comando nao reconhecido: " + cmd);
            }
        }
    }

    private static void mostrarAjuda() {
        System.out.println("Comandos disponiveis:");
        System.out.println("  ls                          lista o conteudo do diretorio atual");
        System.out.println("  cd <nome>                   entra em um diretorio (use .. para voltar)");
        System.out.println("  mkdir <nome>                cria um diretorio");
        System.out.println("  rmdir <nome>                apaga um diretorio");
        System.out.println("  renamedir <antigo> <novo>   renomeia um diretorio");
        System.out.println("  touch <nome>                cria um arquivo vazio");
        System.out.println("  rm <nome>                   apaga um arquivo");
        System.out.println("  rename <antigo> <novo>      renomeia um arquivo");
        System.out.println("  cp <origem> <destino>       copia um arquivo");
        System.out.println("  write <arquivo> <texto>     grava texto em um arquivo");
        System.out.println("  cat <arquivo>               mostra o conteudo de um arquivo");
        System.out.println("  ajuda                       mostra esta lista");
        System.out.println("  sair                        encerra o simulador");
    }
}
