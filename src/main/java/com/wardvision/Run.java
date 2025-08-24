package com.wardvision;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.cdimascio.dotenv.Dotenv;

import com.wardvision.features.smoke_path.controller.SmokePathController;
import com.wardvision.helpers.ReplayFileHelper;
import com.wardvision.shared.analyzer.ReplayAnalyzer;

public class Run {

  private static final Logger log = LoggerFactory.getLogger(Run.class);
  private int i;
  Dotenv dotenv = Dotenv.load();

  public Run() {
    this.i = 1;
  }

  public void processFolder() throws IOException {
    String path = dotenv.get("REPLAY_PATH");

    if (path == null || path.isEmpty())
      path = "F:\\Workspace\\wardvision\\replay-parser\\data\\raw";

    processFolder(path);
  }

  public void processFolder(String pathToRead) throws IOException {
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
          ReplayFileHelper.moveToError(file);
          log.debug("Movido para ../error: arquivo não suportado: {}", file.getName());
          continue;
        }

        log.info("Lendo {}/{}: {}", i, files.length, file.getName());
        i++;

        try {
          replayAnalyzer.analyzer(file);

          ReplayFileHelper.moveToProcessed(file);
          log.info("Sucesso! Movido para ../processed: {}", file.getName());

        } catch (Exception e) {
          ReplayFileHelper.moveToError(file);
          log.error("Movido para ../error: Arquivo de replay inválido: {} - {}", file.getName(), e);
        }

      } catch (Exception e) {
        ReplayFileHelper.moveToError(file);
        log.error("Movido para ../error: Erro ao processar arquivo {}", file.getName(), e);
      }
    }
  }
}
