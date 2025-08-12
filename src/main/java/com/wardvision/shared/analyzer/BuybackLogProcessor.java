package com.wardvision.shared.analyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wardvision.shared.models.MatchContext;

import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;

/*
 * Este processor serve apenas para alguns ambientes de testes
 */

public class BuybackLogProcessor {

  BuybackLogProcessor(MatchContext context) {

  }

  private final Logger log = LoggerFactory.getLogger(BuybackLogProcessor.class);

  @OnCombatLogEntry
  public void onCombatLogEntry(CombatLogEntry cle) {

    switch (cle.getType()) {
      case DOTA_COMBATLOG_BUYBACK:
        log.info("buyback: {}", cle.getInflictorName());
        break;

      default:
        break;
    }
  }
}
