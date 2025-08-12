package com.wardvision.shared.gametimes.processor;

import com.wardvision.shared.gametimes.models.GameTimes;

import skadistats.clarity.model.Entity;
import skadistats.clarity.model.FieldPath;
import skadistats.clarity.processor.runner.Context;

public interface IGameTimesProcessor {

  void onEntityPropertyChanged(Context ctx, Entity e, FieldPath fp);

  void onTickEnd(Context ctx, boolean synthetic);

  GameTimes getGameTime();
}
