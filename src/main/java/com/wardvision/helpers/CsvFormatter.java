package com.wardvision.helpers;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CsvFormatter {
  public static String toCsvString(Object dto) {
    Object[] values = Arrays.stream(dto.getClass().getDeclaredFields()).map(field -> {
      field.setAccessible(true);
      try {
        return field.get(dto);
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Erro ao acessar o campo: " + field.getName(), e);
      }
    }).toArray();

    return sepByComma(values);
  }

  private static String sepByComma(Object... fields) {
    return Arrays.stream(fields)
        .map(String::valueOf)
        .collect(Collectors.joining(","));
  }
}
