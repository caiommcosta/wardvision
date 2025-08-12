package com.wardvision.features.smoke_path.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wardvision.features.smoke_path.models.SmokePathPoint;
import com.wardvision.helpers.CsvFormatter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvSmokePathRepository implements ICsvSmokePathRepository {

  private static final Logger log = LoggerFactory.getLogger(CsvSmokePathRepository.class);
  private static final boolean TEST_MODE = true;
  private static final String BASE_PATH = TEST_MODE
      ? "F:\\Workspace\\wardvision\\replay-parser\\data\\tests\\parsed"
      : "F:\\Workspace\\wardvision\\replay-parser\\data\\parsed";

  @Override
  public void save(List<SmokePathPoint> entries) throws Exception {
    if (entries == null || entries.isEmpty()) {
      log.info("Nenhum dado recebido para salvar no CSV.");
      return;
    }

    Map<Integer, List<SmokePathPoint>> grouped = groupBySide(entries);

    for (Map.Entry<Integer, List<SmokePathPoint>> sideGroup : grouped.entrySet()) {
      writeToFile(sideGroup.getValue());
    }
  }

  private Map<Integer, List<SmokePathPoint>> groupBySide(List<SmokePathPoint> entries) {
    return entries.stream()
        .filter(e -> e.side() == 2 || e.side() == 3)
        .collect(Collectors.groupingBy(SmokePathPoint::side));
  }

  private void writeToFile(List<SmokePathPoint> data) throws IOException {
    if (data.isEmpty())
      return;

    Path path = buildFilePath(data.get(0));
    Files.createDirectories(path.getParent());

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
      writer.write("smokeId,timestamp,tick,matchId,steamId,heroName,teamName,side,x,y,time");
      writer.newLine();

      for (SmokePathPoint entry : data) {
        writer.write(CsvFormatter.toCsvString(entry));
        writer.newLine();
      }

      log.info("CSV salvo com {} entradas: {}", data.size(), path.getFileName());
    }
  }

  private Path buildFilePath(SmokePathPoint sample) {
    String filename = String.format(
        "%s_%s_%s_%d.csv",
        sample.timestamp(), sample.matchId(), sample.teamName(), sample.side());
    return Paths.get(BASE_PATH, filename);
  }
}
