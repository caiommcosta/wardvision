package com.wardvision.features.smoke_path.repository;

import java.util.List;

import com.wardvision.features.smoke_path.models.SmokePathPoint;

public interface ICsvSmokePathRepository {

  public void save(List<SmokePathPoint> entries) throws Exception;

}
