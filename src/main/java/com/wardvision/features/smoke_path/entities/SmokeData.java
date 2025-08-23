package com.wardvision.features.smoke_path.entities;

public record SmokeData(int startTick, int team, boolean isSmoked) {
  public SmokeData(int startTick, int team) {
    this(startTick, team, true);
  }
}
