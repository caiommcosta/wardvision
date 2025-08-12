package com.wardvision.features.smoke_path.models;

public record SmokePathPoint(
    int smokeId,
    String timestamp,
    int tick,
    String matchId,
    String steamId,
    String heroName,
    String teamName, // teamliquid
    int side, // 2 == Radiant, 3 == Dire
    int x,
    int y,
    int time // tempo
) {
}
