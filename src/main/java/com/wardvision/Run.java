package com.wardvision;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wardvision.features.smoke_path.controller.SmokePathController;
import com.wardvision.features.watcher_activations.controller.WatcherActivationController;
import com.wardvision.shared.analyzer.ReplayAnalyzer;

public class Run {

  private static final Logger log = LoggerFactory.getLogger(Run.class);
  private int i;

  public Run() {
    this.i = 1;
  }

  public void processFolder() {
    String path = System.getenv("REPLAY_PATH");

    if (path == null || path.isEmpty())
      path = "F:\\Workspace\\wardvision\\replay-parser\\data\\raw";

    processFolder(path);
  }

  public void processFolder(String pathToRead) {
    i = 1;
    File folder = new File(pathToRead);

    if (!folder.exists()) {
      log.error("O caminho não existe: {}", pathToRead);
      return;
    }

    if (!folder.isDirectory()) {
      log.error("O caminho informado não é uma pasta: {}", pathToRead);
      return;
    }

    // Controllers
    SmokePathController smokePathController = new SmokePathController();

    // Replay Analyzer
    ReplayAnalyzer replayAnalyzer = new ReplayAnalyzer(List.of(smokePathController));

    File[] files = folder.listFiles();
    if (files == null || files.length == 0) {
      log.warn("Nenhum arquivo encontrado na pasta: {}", pathToRead);
      return;
    }

    for (File file : files) {
      try {
        if (!file.isFile() || !file.getName().toLowerCase().endsWith(".dem")) {
          log.debug("Ignorando arquivo não suportado: {}", file.getName());
          continue;
        }

        log.info("Lendo {}/{}: {}", i, files.length, file.getName());
        i++;

        try {
          replayAnalyzer.analyzer(file);

        } catch (Exception e) {
          log.error("Arquivo de replay inválido: {} - {}", file.getName(), e);
        }

      } catch (Exception e) {
        log.error("Erro ao processar arquivo {}", file.getName(), e);
      }
    }
  }
}
