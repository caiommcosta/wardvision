package com.wardvision.features.smoke_path.controller;

import java.util.ArrayList;
import java.util.List;

import com.wardvision.features.smoke_path.models.SmokePathPoint;
import com.wardvision.features.smoke_path.repository.CsvSmokePathRepository;
import com.wardvision.features.smoke_path.repository.DbSmokePathRepository;
import com.wardvision.shared.intercace.IEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmokePathController implements IEvent<SmokePathPoint> {

  private static final Logger log = LoggerFactory.getLogger(SmokePathController.class);

  private final List<SmokePathPoint> entries = new ArrayList<>();

  @Override
  public void handle(List<SmokePathPoint> events) {
    if (events != null && !events.isEmpty()) {
      entries.addAll(events);
    }
  }

  @Override
  public void finish() {
    if (entries.isEmpty()) {
      log.info("SmokePathProcessor: nenhuma entrada para salvar.");
      return;
    }

    try {
      new DbSmokePathRepository().save(entries);
      new CsvSmokePathRepository().save(entries);
    } catch (Exception e) {

      log.error("Erro ao salvar entradas do SmokePath no banco de dados: {}", e.getMessage(), e);
    }
  }

  @Override
  public Class<SmokePathPoint> getEventType() {
    return SmokePathPoint.class;
  }
}
