package com.wardvision.features.smoke_path.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.wardvision.config.DatabaseConfig;
import com.wardvision.features.smoke_path.models.SmokePathPoint;

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
}
