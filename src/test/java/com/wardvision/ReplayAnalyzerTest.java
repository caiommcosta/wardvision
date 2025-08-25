package com.wardvision;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.wardvision.shared.analyzer.ReplayAnalyzer;
import com.wardvision.shared.gametimes.processor.GameTimesProcessor;
import com.wardvision.shared.intercace.IEvent;
import com.wardvision.shared.intercace.ISimpleRunnerFactory;
import com.wardvision.shared.match_details.processor.MatchDetailsProcessor;

import skadistats.clarity.processor.runner.SimpleRunner;

class ReplayAnalyzerTest {

  ISimpleRunnerFactory mockFactory = mock(ISimpleRunnerFactory.class); // fábrica mock
  IEvent<?> mockEvent = mock(IEvent.class); // mock do seu IEvent
  SimpleRunner mockRunner = mock(SimpleRunner.class); // mock do SimpleRunner

  @Test
  void testAnalyzerCallsFinishOnControllers() throws Exception {
    when(mockFactory.create(any(File.class))).thenReturn(mockRunner);

    ReplayAnalyzer analyzer = new ReplayAnalyzer(List.of(mockEvent), mockFactory);

    File fakeReplay = new File("1680000000_1234567890.dem");

    analyzer.analyzer(fakeReplay);

    // Factory chamada
    verify(mockFactory, times(1)).create(any(File.class));
    // Controllers finalizados
    verify(mockEvent, times(1)).finish();

    ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
    verify(mockRunner).runWith(captor.capture());
    Object[] processors = captor.getValue();

    // Apenas 2 são obrigatórios; pode haver mais
    assertTrue(processors.length >= 2, "Deve conter pelo menos GameTimesProcessor e MatchDetailsProcessor");

    boolean hasGameTimesProcessor = false;
    boolean hasMatchDetailsProcessor = false;

    for (Object proc : processors) {
      if (proc instanceof GameTimesProcessor)
        hasGameTimesProcessor = true;
      if (proc instanceof MatchDetailsProcessor)
        hasMatchDetailsProcessor = true;
    }

    assertTrue(hasGameTimesProcessor, "Processor obrigatório ausente: GameTimesProcessor");
    assertTrue(hasMatchDetailsProcessor, "Processor obrigatório ausente: MatchDetailsProcessor");
  }

  @Test
  void testAnalyzerCallsRunWithWithRequiredProcessors() throws Exception {
    File mockFile = mock(File.class);
    when(mockFile.getName()).thenReturn("1234_5678.dem");

    when(mockFactory.create(any(File.class))).thenReturn(mockRunner);

    ReplayAnalyzer replayAnalyzer = new ReplayAnalyzer(List.of(mockEvent), mockFactory);

    replayAnalyzer.analyzer(mockFile);

    ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
    verify(mockRunner, times(1)).runWith(captor.capture());

    Object[] processors = captor.getValue();

    boolean hasGameTimesProcessor = false;
    boolean hasMatchDetailsProcessor = false;

    for (Object proc : processors) {
      if (proc instanceof GameTimesProcessor)
        hasGameTimesProcessor = true;
      if (proc instanceof MatchDetailsProcessor)
        hasMatchDetailsProcessor = true;
    }

    assertTrue(hasGameTimesProcessor, "Processor obrigatório ausente: GameTimesProcessor");
    assertTrue(hasMatchDetailsProcessor, "Processor obrigatório ausente: MatchDetailsProcessor");
  }
}
