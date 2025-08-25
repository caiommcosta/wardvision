package com.wardvision.features.smoke_path.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.wardvision.config.DatabaseConfig;
import com.wardvision.features.smoke_path.entities.SmokePathPoint;
import com.wardvision.features.smokes_outcome.entities.SmokePathMinDTO;

public class DbSmokePathRepository implements IDbSmokePathRepository {

  private static final String INSERT_QUERY = """
          INSERT INTO smoke_path (
              match_date, smoke_id, match_id, steam_id, hero_name,
              team_name, side, x_coord, y_coord, tick, time
          ) VALUES (
              to_timestamp(?::BIGINT), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
          )
          ON CONFLICT ON CONSTRAINT unique_smoke_path_entry DO NOTHING;
      """;

  private static final String SELECT_OUTCOME_QUERY = """
      SELECT smoke_id, match_id, steam_id, hero_name, team_name, side, tick, time
      FROM smoke_path
      WHERE match_id = ?
      ORDER BY tick ASC
        """;

  @Override
  public void save(List<SmokePathPoint> entries) throws SQLException {
    try (Connection conn = DatabaseConfig.getConnection();
        PreparedStatement statement = conn.prepareStatement(INSERT_QUERY)) {

      for (SmokePathPoint entry : entries) {
        statement.setString(1, entry.timestamp());
        statement.setInt(2, entry.smokeId());
        statement.setString(3, entry.matchId());
        statement.setString(4, entry.steamId());
        statement.setString(5, entry.heroName());
        statement.setString(6, entry.teamName());
        statement.setInt(7, entry.side());
        statement.setInt(8, entry.x());
        statement.setInt(9, entry.y());
        statement.setInt(10, entry.tick());
        statement.setInt(11, entry.time());

        statement.addBatch();
      }

      statement.executeBatch();
    }
  }

  @Override
  public List<SmokePathMinDTO> findByMatchId(String matchId) throws SQLException {
    List<SmokePathMinDTO> results = new ArrayList<>();

    try {
      Connection conn = DatabaseConfig.getConnection();
      PreparedStatement statement = conn.prepareStatement(SELECT_OUTCOME_QUERY);

      statement.setString(1, matchId);

      try (ResultSet rs = statement.executeQuery()) {
        while (rs.next()) {
          SmokePathMinDTO dto = new SmokePathMinDTO(
              rs.getInt("smoke_id"),
              rs.getString("match_id"),
              rs.getString("steam_id"),
              rs.getString("hero_name"),
              rs.getString("team_name"),
              rs.getInt("side"),
              rs.getInt("tick"),
              rs.getInt("time"));
          results.add(dto);
        }
      }

    } catch (SQLException e) {

      e.printStackTrace();
    }
    return results;
  }
}
