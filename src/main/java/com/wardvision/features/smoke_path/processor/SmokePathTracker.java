package com.wardvision.features.smoke_path.processor;

import com.wardvision.features.smoke_path.entities.SmokeData;
import com.wardvision.features.smoke_path.entities.SmokePathPoint;
import com.wardvision.helpers.NameNormalizer;
import com.wardvision.shared.entities.MatchContext;
import com.wardvision.shared.gametimes.entities.GameTimes;
import com.wardvision.shared.intercace.IProcessorWithResult;
import com.wardvision.shared.match_details.entities.MatchPlayers;
import com.wardvision.shared.match_details.entities.MatchTeams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skadistats.clarity.model.CombatLogEntry;
import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.gameevents.OnCombatLogEntry;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

import java.util.ArrayList;

public class SmokePathTracker implements IProcessorWithResult<SmokePathPoint> {

  private final List<SmokePathPoint> smokePathPoints = new ArrayList<>();
  private final String matchId;
  private final String timestamp;
  private final GameTimes gameTimes;
  private final Map<Integer, MatchPlayers> matchPlayers;
  private Map<String, Integer> heroToPlayer = new HashMap<>();
  private final MatchTeams teamNames;

  public SmokePathTracker(MatchContext context) {
    this.matchId = context.matchId();
    this.timestamp = context.timestamp();
    this.gameTimes = context.gameTimes();
    this.matchPlayers = context.matchPlayers();
    this.heroToPlayer = context.heroToPlayer();
    this.teamNames = context.teamNames();
  }

  @Override
  public List<SmokePathPoint> getResult() {
    return smokePathPoints;
  }

  // Para log de eventos
  private final Logger log = LoggerFactory.getLogger(SmokePathTracker.class);

  private final Map<Integer, SmokeData> heroesInSmoke = new HashMap<>();

  private Integer radiantSmokeCounter = 0;
  private Integer direSmokeCounter = 0;

  private static final int RADIANT_TEAM_ID = 2; // 2 == radiant team
  private static final int DIRE_TEAM_ID = 3; // 3 == dire team

  private int control = 0; // controlar inserções a cada 1 seg (30 ticks)

  // Identificar quando um herói foi afetado por uma smoke, qual herói
  // e quando perde o efeito
  @OnCombatLogEntry
  public void onCombatLogEntry(CombatLogEntry cle) {
    switch (cle.getType()) {
      case DOTA_COMBATLOG_ITEM:
        if ("item_smoke_of_deceit".equals(cle.getInflictorName())) {
          String hero = NameNormalizer.normalize(cle.getAttackerName(), "npc_dota_hero_");
          int playerId = heroToPlayer.get(hero);
          if (!matchPlayers.containsKey(playerId))
            return;

          boolean isRandiant = (matchPlayers.get(playerId).side() == RADIANT_TEAM_ID);

          if (isRandiant) {
            radiantSmokeCounter++;
            log.info(" (Radiant) smoke #{} at {} at sec {}", radiantSmokeCounter, gameTimes.getActuallyTick(),
                gameTimes.getElapsedSeconds());
          } else {
            direSmokeCounter++;
            log.info(" (Dire) smoke #{} at {} at sec {}", direSmokeCounter, gameTimes.getActuallyTick(),
                gameTimes.getElapsedSeconds());
          }
        }
        break;

      case DOTA_COMBATLOG_MODIFIER_ADD:
        if ("modifier_smoke_of_deceit".equals(cle.getInflictorName())) {
          if (cle.isTargetIllusion())
            return;
          String target = cle.getTargetName();
          if (!target.startsWith("npc_dota_hero_"))
            return;

          String heroIn = NameNormalizer.normalize(target, "npc_dota_hero_");
          Integer playerId = heroToPlayer.get(heroIn);
          if (!heroesInSmoke.containsKey(playerId)) {
            if (cle.getTargetTeam() == RADIANT_TEAM_ID)
              heroesInSmoke.put(playerId, new SmokeData(gameTimes.getActuallyTick(), RADIANT_TEAM_ID));
            else if (cle.getTargetTeam() == DIRE_TEAM_ID)
              heroesInSmoke.put(playerId, new SmokeData(gameTimes.getActuallyTick(), DIRE_TEAM_ID));
          }
        }
        break;

      case DOTA_COMBATLOG_MODIFIER_REMOVE:
        if ("modifier_smoke_of_deceit".equals(cle.getInflictorName())) {
          if (cle.isTargetIllusion())
            return;
          String target = cle.getTargetName();
          if (!target.startsWith("npc_dota_hero_"))
            return;

          String heroOut = NameNormalizer.normalize(target, "npc_dota_hero_");
          Integer playerId = heroToPlayer.get(heroOut);

          heroesInSmoke.remove(playerId);
        }
        break;

      default:
        break;
    }

  }

  @OnTickEnd
  public void OnTickEnd(Context ctx, boolean synthetic) {
    if (gameTimes.hasGameEnded() || gameTimes.isPaused())
      return;

    // Executar apenas a cada segundo
    if (control < 30) {
      control++;
      return;
    }

    Entities entities = ctx.getProcessor(Entities.class);
    if (entities == null)
      return;

    if (!heroesInSmoke.isEmpty())
      smokesToDTO(entities);

    updateSmokeStatuses();

    control = 0;
  }

  // Passar dados para o entries
  private void smokesToDTO(Entities ent) {

    for (Map.Entry<Integer, SmokeData> data : heroesInSmoke.entrySet()) {
      int id = data.getKey();

      Entity hero = ent.getByIndex(id);
      if (hero == null || !hero.getDtClass().getDtName().startsWith("CDOTA_Unit_Hero_"))
        continue;

      int team = hero.getProperty("m_iTeamNum");
      Integer smokeCounter = null;
      String teamName = "";
      Integer teamId = null;

      if (team == RADIANT_TEAM_ID) {
        smokeCounter = radiantSmokeCounter;
        teamName = teamNames.getRadiant();
        teamId = RADIANT_TEAM_ID;
      } else if (team == DIRE_TEAM_ID) {
        smokeCounter = direSmokeCounter;
        teamName = teamNames.getDire();
        teamId = DIRE_TEAM_ID;
      }

      SmokePathPoint entry = new SmokePathPoint(
          smokeCounter,
          timestamp,
          gameTimes.getActuallyTick(),
          matchId,
          matchPlayers.get(id).steamId(),
          matchPlayers.get(id).heroName(),
          teamName,
          teamId,
          hero.getProperty("CBodyComponent.m_cellX"),
          hero.getProperty("CBodyComponent.m_cellY"),
          gameTimes.getElapsedSeconds());

      smokePathPoints.add(entry);
    }
  }

  // Remover herói da lista caso não tenha detectado a saída da smoke
  // Talvez aconteça com meepo. Faltam testes
  // remover da smoke após 45seg
  private void updateSmokeStatuses() {
    Iterator<Map.Entry<Integer, SmokeData>> iterator = heroesInSmoke.entrySet().iterator();

    while (iterator.hasNext()) {
      Map.Entry<Integer, SmokeData> entry = iterator.next();
      SmokeData data = entry.getValue();

      if (gameTimes.getEffetiveTick() - data.startTick() >= 1350) {
        iterator.remove();
      }
    }
  }
}