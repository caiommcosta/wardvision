package com.wardvision;
/*1753668915_8392306732 */
import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) throws Exception {
        Run run = new Run();
        Dotenv dotenv = Dotenv.load();

        String pathToRead = args.length > 0 ? args[0] : dotenv.get("REPLAY_PATH");
        System.out.println(pathToRead);

        if (pathToRead == null || pathToRead.isEmpty()) {
            System.err.println("Não há replays no caminho.");
            System.exit(1);
        }

        run.processFolder(pathToRead);
    }
}
