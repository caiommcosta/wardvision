package com.wardvision.shared.gametimes.processor;

import com.wardvision.shared.gametimes.models.GameTimes;

import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.entities.OnEntityPropertyChanged;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;

public class GameTimesProcessor implements IGameTimesProcessor {

  private final GameTimes gameTime = new GameTimes();

  @OnEntityPropertyChanged(classPattern = "CDOTAGamerulesProxy", propertyPattern = "m_pGameRules.m_flPreGameStartTime")
  public void onEntityPropertyChanged(Context ctx, Entity gameRules, FieldPath fp) {
    gameTime.maybeInitStartTimes(gameRules);
  }

  @Override
  @OnTickEnd
  public void onTickEnd(Context ctx, boolean synthetic) {
    int tick = ctx.getTick();
    gameTime.setActuallyTick(tick);

    Entities entities = ctx.getProcessor(Entities.class);
    Entity gameRules = entities.getByDtName("CDOTAGamerulesProxy");

    if (entities == null || gameRules == null)
      return;

    gameTime.updatePause(gameRules);
    gameTime.updateEndgame(gameRules);
  }

  @Override
  public GameTimes getGameTime() {
    return gameTime;
  }

}
