package com.wardvision.helpers;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ReplayFileHelperTest {

  @TempDir
  Path tempDir;

  private Path processedDir;
  private Path errorDir;

  @BeforeEach
  void setup() throws Exception {
    processedDir = tempDir.resolve("processed");
    errorDir = tempDir.resolve("error");

    setStaticField("PROCESSED_DIR", processedDir.toString());
    setStaticField("ERROR_DIR", errorDir.toString());
  }

  private void setStaticField(String fieldName, String value) throws Exception {
    Field field = ReplayFileHelper.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(null, value);
  }

  @Test
  void testMoveToProcessed() throws Exception {
    File replay = tempDir.resolve("123_456.dem").toFile();
    Files.writeString(replay.toPath(), "dummy content");

    ReplayFileHelper.moveToProcessed(replay);

    File movedFile = processedDir.resolve("123_456.dem").toFile();
    assertTrue(movedFile.exists());
    assertFalse(replay.exists());
  }

  @Test
  void testMoveToError() throws Exception {
    File replay = tempDir.resolve("789_1011.dem").toFile();
    Files.writeString(replay.toPath(), "dummy content");

    ReplayFileHelper.moveToError(replay);

    File movedFile = errorDir.resolve("789_1011.dem").toFile();
    assertTrue(movedFile.exists());
    assertFalse(replay.exists());
  }

  @Test
  void testExtractMatchId() {
    String matchId = ReplayFileHelper.extractMatchId("1718293829_67890.dem");
    assertEquals("67890", matchId);
  }

  @Test
  void testExtractMatchIdInvalid() {
    Exception ex = assertThrows(IllegalArgumentException.class,
        () -> ReplayFileHelper.extractMatchId("invalid.dem"));
    assertTrue(ex.getMessage().contains("Nome do arquivo inválido"));
  }

  @Test
  void testExtractTimestampIsValidUnix() {
    String filename = "1690000000_12345.dem"; // timestamp plausível
    String timestampStr = ReplayFileHelper.extractTimestamp(filename);
    long timestamp = Long.parseLong(timestampStr);

    assertTrue(timestamp > 0, "Timestamp deve ser positivo");
    assertTrue(timestamp <= 4102444800L, "Timestamp fora do intervalo esperado");
  }

  @Test
  void testExtractTimestampIsInvalidUnix() {
    String filename = "50000000000_12345.dem"; // timestamp muito grande, fora do limite

    assertThrows(IllegalArgumentException.class, () -> {
      ReplayFileHelper.extractTimestamp(filename);
    });
  }
}
