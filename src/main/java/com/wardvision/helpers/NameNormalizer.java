package com.wardvision.helpers;

public class NameNormalizer {
  public static String normalize(String name, String prefixToStrip) {
    return name
        .replace(prefixToStrip, "")
        .replaceAll("[^0-9a-zA-Z]+", "")
        .toLowerCase();
  }
}
