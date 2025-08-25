package com.wardvision.helpers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class NameNormalizerTest {

  @ParameterizedTest(name = "normalize(\"{0}\", \"{1}\") -> \"{2}\"")
  @CsvSource(value = {
      "Player123,prefix_,player123",
      "prefix_Player-Name,prefix_,playername",
      "prefix_Player Name,prefix_,playername",
      "SomePlayer,null,someplayer",
      "'',prefix_,exception",
      "null,prefix_,exception",
      "null,null,exception"
  }, nullValues = "null")
  void testNormalize(String input, String prefix, String expected) {
    if ("exception".equals(expected)) {
      // Lançar exceção quando name for null ou vazio
      assertThrows(IllegalArgumentException.class, () -> NameNormalizer.normalize(input, prefix));
    } else {
      assertEquals(expected, NameNormalizer.normalize(input, prefix));
    }
  }
}
