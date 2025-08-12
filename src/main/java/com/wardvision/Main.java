package com.wardvision;

public class Main {

    public static void main(String[] args) throws Exception {
        Run run = new Run();

        // Pode ser um argumento da linha de comando
        String pathToRead = args.length > 0 ? args[0] : System.getenv("REPLAY_PATH");

        if (pathToRead == null || pathToRead.isEmpty()) {
            System.err.println("Caminho para os replays não definido.");
            System.exit(1);
        }

        run.processFolder(pathToRead);
    }
}
