package com.wardvision.helpers;

public class NameNormalizer {
  public static String normalize(String name, String prefixToStrip) {
    if (name == null || name.isEmpty()) {
      throw new IllegalArgumentException("name não pode ser nulo ou vazio");
    }
    if (prefixToStrip == null) {
      prefixToStrip = "";
    }
    return name
        .replace(prefixToStrip, "")
        .replaceAll("[^0-9a-zA-Z]+", "")
        .toLowerCase();
  }
}
