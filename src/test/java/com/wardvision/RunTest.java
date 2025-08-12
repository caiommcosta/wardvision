package com.wardvision;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RunTest {

  private Run run;
  private final String testDirPath = "data\\tests\\raw";

  @BeforeEach
  public void setup() throws IOException {
    run = new Run();
    File testDir = new File(testDirPath);
    if (!testDir.exists()) {
      testDir.mkdir();
    }
  }

  @Test
  public void testPathDoesNotExist() {
    run.processFolder("pasta_que_nao_existe");
    // Verifica que não lança exceção, logs são gerenciados pela classe
  }

  @Test
  public void testPathIsFile() throws IOException {
    File fakeFile = new File("fakefile.txt");
    fakeFile.createNewFile();

    run.processFolder(fakeFile.getAbsolutePath());

    fakeFile.delete();
  }

  @Test
  public void testFolderWithInvalidFile() throws IOException {
    File invalidFile = new File(testDirPath + "/arquivo.txt");
    invalidFile.createNewFile();

    run.processFolder(testDirPath);

    invalidFile.delete();
  }

  @Test
  public void testFolderWithValidDemFile() throws IOException {
    File demFile = new File(testDirPath + "/1753668915_8392306732.dem");
    demFile.createNewFile();

    run.processFolder(testDirPath);
  }
}
