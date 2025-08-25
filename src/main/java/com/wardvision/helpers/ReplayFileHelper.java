package com.wardvision.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import io.github.cdimascio.dotenv.Dotenv;

public class ReplayFileHelper {

  private static Dotenv dotenv;
  private static String PROCESSED_DIR;
  private static String ERROR_DIR;

  static {
    dotenv = Dotenv.load();
    PROCESSED_DIR = dotenv.get("PROCESSED_DIR");
    ERROR_DIR = dotenv.get("ERROR_DIR");
  }

  public static void moveToProcessed(File file) throws IOException {
    Path processedDir = Paths.get(PROCESSED_DIR);
    Files.createDirectories(processedDir); // Garante que a pasta existe

    Path sourcePath = file.toPath();
    Path targetPath = processedDir.resolve(file.getName());

    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
  }

  public static void moveToError(File file) throws IOException {

    Path errorDir = Paths.get(ERROR_DIR);
    Files.createDirectories(errorDir); // Garante que a pasta existe

    Path sourcePath = file.toPath();
    Path targetPath = errorDir.resolve(file.getName());

    Files.move(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
  }

  public static String extractMatchId(String filename) {
    String[] parts = filename.replace(".dem", "").split("_");
    if (parts.length > 1) {
      return parts[1];
    } else {
      throw new IllegalArgumentException("Nome do arquivo inválido para extrair matchId: " + filename);
    }
  }

  public static String extractTimestamp(String filename) {
    String[] parts = filename.replace(".dem", "").split("_");
    if (parts.length == 0) {
      throw new IllegalArgumentException("Nome de arquivo inválido para extrair timestamp: " + filename);
    }

    long timestamp;
    try {
      timestamp = Long.parseLong(parts[0]);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Timestamp não é um número válido: " + parts[0]);
    }

    // Intervalo Unix plausível: 1970-01-01 até 2100-01-01
    long earliest = 0L;
    long latest = 4102444800L;
    if (timestamp < earliest || timestamp > latest) {
      throw new IllegalArgumentException("Timestamp fora do intervalo esperado: " + timestamp);
    }

    return parts[0];
  }

}
