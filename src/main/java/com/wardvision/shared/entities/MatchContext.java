package com.wardvision.shared.entities;

import java.util.Map;

import com.wardvision.shared.gametimes.entities.GameTimes;
import com.wardvision.shared.match_details.entities.MatchPlayers;
import com.wardvision.shared.match_details.entities.MatchTeams;

public record MatchContext(
                String matchId,
                String timestamp,
                GameTimes gameTimes,
                Map<Integer, MatchPlayers> matchPlayers,
                Map<String, Integer> heroToPlayer,
                MatchTeams teamNames) {
}
