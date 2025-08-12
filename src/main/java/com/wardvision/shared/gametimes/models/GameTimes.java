package com.wardvision.shared.gametimes.models;

import com.wardvision.helpers.EntityPropertyHelper;

import skadistats.clarity.model.Entity;

// Gerencia o relógio do jogo baseado em ticks e propriedades do GameRulesProxy

public class GameTimes {

  private int actuallyTick = -1;
  private int pregameStartTick = -1;
  private int gameStartTick = -1;
  private int pauseStartTick = -1;
  private int totalPausedTicks = 0;
  private int finalTick = -1;
  private boolean wasPaused = false;
  private boolean hasEnded = false;

  // Atualiza o tick atual
  public void setActuallyTick(int actuallyTick) {
    this.actuallyTick = actuallyTick;
  }

  // Pegar o tick atual
  public int getActuallyTick() {
    return this.actuallyTick;
  }

  // Atualiza tempos de início quando o pregame começar
  public void maybeInitStartTimes(Entity gameRules) {

    if (pregameStartTick < 0) {
      Float pre = EntityPropertyHelper.safeGetProperty(gameRules, "m_pGameRules.m_flPreGameStartTime");
      if (pre != null && pre > 1) {
        pregameStartTick = actuallyTick;

        gameStartTick = pregameStartTick + 2700;
        System.out.println("pregame: " + pregameStartTick + " gamestart: " + gameStartTick);
      }
    }
  }

  // Marca o fim do jogo se detectado em gameRules
  public void updateEndgame(Entity gameRules) {
    Float end = EntityPropertyHelper.safeGetProperty(gameRules, "m_pGameRules.m_flGameEndTime");
    if (end != null && end > 1.0f) {
      hasEnded = true;
    }
    finalTick = actuallyTick;
  }

  // Atualiza pausas com base no flag de pausa
  public void updatePause(Entity gameRules) {
    Boolean isPaused = EntityPropertyHelper.safeGetProperty(gameRules, "m_pGameRules.m_bGamePaused");
    if (isPaused == null)
      return;

    if (!wasPaused && isPaused && pregameStartTick > 0) {
      pauseStartTick = actuallyTick;
    } else if (wasPaused && !isPaused && pauseStartTick >= 0) {
      totalPausedTicks += actuallyTick - pauseStartTick;
    }
    wasPaused = isPaused;
  }

  public boolean isPaused() {
    return wasPaused;
  }

  // Fim de jogo
  public boolean hasGameEnded() {
    return hasEnded;
  }

  public int getPauseTotalTicks() {
    return totalPausedTicks;
  }

  // Calcula tempo atual de jogo descontando pré‑jogo e pausas
  public int getElapsedSeconds() {
    int effetiveTicks = getEffetiveTick();

    return effetiveTicks / 30;
  }

  public int getEffetiveTick() {
    if (pregameStartTick <= 0 || gameStartTick < 0) {
      return 0;
    }

    if (hasEnded) {
      return finalTick - totalPausedTicks;
    }

    int netTicks = gameStartTick <= actuallyTick
        ? actuallyTick - gameStartTick
        : actuallyTick - pregameStartTick - 2700;

    return netTicks - totalPausedTicks;
  }
}
