package com.wardvision.features.smoke_path.repository;

import java.sql.SQLException;
import java.util.List;

import com.wardvision.features.smoke_path.models.SmokePathPoint;

public interface IDbSmokePathRepository {

  void save(List<SmokePathPoint> entries) throws SQLException;

}
