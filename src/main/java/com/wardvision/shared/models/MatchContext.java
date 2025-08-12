package com.wardvision.shared.models;

import java.util.Map;

import com.wardvision.shared.gametimes.models.GameTimes;
import com.wardvision.shared.match_details.models.MatchPlayers;
import com.wardvision.shared.match_details.models.MatchTeams;

public record MatchContext(
                String matchId,
                String timestamp,
                GameTimes gameTimes,
                Map<Integer, MatchPlayers> matchPlayers,
                Map<String, Integer> heroToPlayer,
                MatchTeams teamNames) {
}
