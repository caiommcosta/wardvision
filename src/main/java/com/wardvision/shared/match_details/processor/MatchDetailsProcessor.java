package com.wardvision.shared.match_details.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wardvision.helpers.EntityPropertyHelper;
import com.wardvision.helpers.NameNormalizer;
import com.wardvision.shared.match_details.entities.MatchPlayers;
import com.wardvision.shared.match_details.entities.MatchTeams;

import skadistats.clarity.model.Entity;
import skadistats.clarity.processor.entities.Entities;
import skadistats.clarity.processor.entities.OnEntityCreated;
import skadistats.clarity.processor.reader.OnTickEnd;
import skadistats.clarity.processor.runner.Context;

public class MatchDetailsProcessor {

  private static final Logger log = LoggerFactory.getLogger(MatchDetailsProcessor.class);

  private final Map<Integer, MatchPlayers> matchPlayers = new HashMap<>();

  // map com key invertida para buscar um herói em O(1) em matchPlayers
  private final Map<String, Integer> heroToPlayer = new HashMap<>();

  private int UNIT_HERO_TYPE = 1;
  private boolean meepoHasTeam = false;
  private int meepoTeam;

  private final int RADIANT_TEAM_ID = 2, DIRE_TEAM_ID = 3;
  private final MatchTeams teamNames = new MatchTeams();

  public MatchDetailsProcessor() {

  }

  public Map<Integer, MatchPlayers> getMatchPlayers() {
    return matchPlayers;
  }

  public Map<String, Integer> getHeroToPlayer() {
    return heroToPlayer;
  }

  public MatchTeams getTeamNames() {
    return teamNames;
  }

  @OnEntityCreated
  public void onEntityCreated(Context ctx, Entity e) {
    if (!e.getDtClass().getDtName().startsWith("CDOTA_Unit_Hero_"))
      return;

    String steamId = "";
    Integer index = e.getIndex();
    String heroName = NameNormalizer.normalize(e.getDtClass().getDtName(), "CDOTA_Unit_Hero_");
    Integer team = EntityPropertyHelper.safeGetProperty(e, "m_iTeamNum");
    Integer unitType = EntityPropertyHelper.safeGetProperty(e, "m_iUnitType");
    boolean bIsClone = EntityPropertyHelper.safeGetProperty(e, "m_bIsClone"); // Necessário para os meepos

    Integer playerId = EntityPropertyHelper.safeGetProperty(e, "m_iPlayerID");

    Entities entities = ctx.getProcessor(Entities.class);
    Iterator<Entity> players = entities
        .getAllByPredicate(ent -> ent.getDtClass().getDtName().equals("CDOTAPlayerController"));

    while (players.hasNext()) {
      Entity player = players.next();
      if (player != null && player.getProperty("m_nPlayerID") == playerId) {
        steamId = player.getProperty("m_steamID").toString();
      }
    }

    if (heroName == null || team == null || unitType == null || unitType != UNIT_HERO_TYPE || index == null
        || playerId == null) {
      return;
    }

    // se for meepo, adicionar o primeiro meepo
    if (heroName.equals("meepo") && !meepoHasTeam) {

      matchPlayers.put(index, new MatchPlayers(heroName, steamId, team));
      heroToPlayer.put(heroName, index);

      meepoTeam = team;
      meepoHasTeam = true;
    }

    // os outros meepos
    if (heroName.equals("meepo") && bIsClone) {
      matchPlayers.put(index, new MatchPlayers(heroName, steamId, meepoTeam));
      heroToPlayer.put(heroName, index);

    } else if (!heroToPlayer.containsKey(heroName)) {
      // outros heróis
      matchPlayers.put(index, new MatchPlayers(heroName, steamId, team));
      heroToPlayer.put(heroName, index);
    }
  }

  @OnTickEnd
  public void onTickEnd(Context ctx, boolean synthetic) {

    Entities entities = ctx.getProcessor(Entities.class);

    if (teamNames.getRadiant() == null || teamNames.getDire() == null) {

      Iterator<Entity> teams = entities.getAllByDtName("CDOTATeam");
      while (teams.hasNext()) {
        Entity team = teams.next();
        if (RADIANT_TEAM_ID == (int) team.getProperty("m_iTeamNum")) {
          teamNames.setRadiant(EntityPropertyHelper.safeGetProperty(team, "m_szTeamname"));
        }
        if (DIRE_TEAM_ID == (int) team.getProperty("m_iTeamNum")) {
          teamNames.setDire(EntityPropertyHelper.safeGetProperty(team, "m_szTeamname"));
        }
      }
    }
  }
}
