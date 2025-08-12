package com.wardvision.helpers;

import skadistats.clarity.model.Entity;

public class EntityPropertyHelper {
  public static <T> T safeGetProperty(Entity e, String prop) {
    try {
      return e.getProperty(prop);
    } catch (IllegalArgumentException ex) {
      return null;
    }
  }
}
