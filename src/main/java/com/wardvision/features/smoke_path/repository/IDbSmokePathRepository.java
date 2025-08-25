package com.wardvision.features.smoke_path.repository;

import java.sql.SQLException;
import java.util.List;

import com.wardvision.features.smoke_path.entities.SmokePathPoint;
import com.wardvision.features.smokes_outcome.entities.SmokePathMinDTO;

public interface IDbSmokePathRepository {

  void save(List<SmokePathPoint> entries) throws SQLException;

  List<SmokePathMinDTO> findByMatchId(String matchId) throws SQLException;
}
