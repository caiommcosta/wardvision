package com.wardvision.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CsvFormatterTest {

  static class SimpleDTO {
    public int id = 1;
    public String heroName = "drowranger";
  }

  static class NullDTO {
    public String field1 = null;
    public String field2 = "test";
  }

  static class EmptyDTO {
  }

  @SuppressWarnings("unused")
  static class PrivateDTO {
    private String value = "hidden";
  }

  @Test
  void testToCsvStringSimple() {
    SimpleDTO dto = new SimpleDTO();
    String result = CsvFormatter.toCsvString(dto);
    assertEquals("1,drowranger", result);
  }

  @Test
  void testToCsvStringWithNulls() {
    NullDTO dto = new NullDTO();
    String result = CsvFormatter.toCsvString(dto);
    assertEquals("null,test", result);
  }

  @Test
  void testToCsvStringEmptyDTO() {
    EmptyDTO dto = new EmptyDTO();
    String result = CsvFormatter.toCsvString(dto);
    assertEquals("", result);
  }

  @Test
  void testToCsvStringPrivateField() {

    PrivateDTO dto = new PrivateDTO();
    String result = CsvFormatter.toCsvString(dto);
    assertEquals("hidden", result);
  }
}
