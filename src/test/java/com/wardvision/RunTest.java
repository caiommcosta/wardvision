package com.wardvision;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import com.wardvision.helpers.ReplayFileHelper;
import com.wardvision.shared.analyzer.ReplayAnalyzer;

public class RunTest {

  private Run run;

  @BeforeEach
  public void setup() throws IOException {
    run = new Run();
  }

  @Test
  public void testPathDoesNotExist() throws IOException {
    try (MockedStatic<ReplayFileHelper> helper = Mockito.mockStatic(ReplayFileHelper.class)) {
      run.processFolder("pasta_que_nao_existe");
      helper.verifyNoInteractions();
    }
  }

  @Test
  public void testPathIsFile(@TempDir File tempDir) throws IOException {
    File fakeFile = new File(tempDir, "fakefile.txt");
    fakeFile.createNewFile();

    try (MockedStatic<ReplayFileHelper> helper = Mockito.mockStatic(ReplayFileHelper.class)) {
      run.processFolder(fakeFile.getAbsolutePath());
      helper.verifyNoInteractions();
    }
  }

  @Test
  public void testFolderWithInvalidFile(@TempDir File tempDir) throws IOException {
    File invalidFile = new File(tempDir, "arquivo.txt");
    invalidFile.createNewFile();

    try (MockedStatic<ReplayFileHelper> helper = Mockito.mockStatic(ReplayFileHelper.class)) {
      run.processFolder(tempDir.getAbsolutePath());
      helper.verify(() -> ReplayFileHelper.moveToError(invalidFile), times(1));
    }
  }

  @Test
  public void testFolderWithValidDemFile_Success(@TempDir File tempDir) throws IOException {
    File demFile = new File(tempDir, "192163448_8426635790.dem");
    demFile.createNewFile();

    try (MockedStatic<ReplayFileHelper> helper = Mockito.mockStatic(ReplayFileHelper.class);
        MockedConstruction<ReplayAnalyzer> analyzerMock = Mockito.mockConstruction(ReplayAnalyzer.class,
            (mock, context) -> {
              doNothing().when(mock).analyzer(any(File.class));
            })) {

      run.processFolder(tempDir.getAbsolutePath());

      helper.verify(() -> ReplayFileHelper.moveToProcessed(demFile), times(1));
      helper.verify(() -> ReplayFileHelper.moveToError(any(File.class)), never());
    }
  }

  @Test
  public void testFolderWithValidDemFile_Error(@TempDir File tempDir) throws IOException {
    File demFile = new File(tempDir, "192163448_8426635790.dem");
    demFile.createNewFile();

    try (MockedStatic<ReplayFileHelper> helper = Mockito.mockStatic(ReplayFileHelper.class);
        MockedConstruction<ReplayAnalyzer> analyzerMock = Mockito.mockConstruction(ReplayAnalyzer.class,
            (mock, context) -> {
              doThrow(new RuntimeException("erro parsing")).when(mock).analyzer(any(File.class));
            })) {

      run.processFolder(tempDir.getAbsolutePath());

      helper.verify(() -> ReplayFileHelper.moveToError(demFile), times(1));
      helper.verify(() -> ReplayFileHelper.moveToProcessed(any(File.class)), never());
    }
  }
}
