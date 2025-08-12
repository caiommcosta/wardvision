package com.wardvision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.wardvision.shared.analyzer.BuybackLogProcessor;
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
    // arrange

    // quando a factory for chamada, devolve o mock runner
    when(mockFactory.create(any(File.class))).thenReturn(mockRunner);

    // instância do analyzer com a nossa factory mock
    ReplayAnalyzer analyzer = new ReplayAnalyzer(List.of(mockEvent), mockFactory);

    // arquivo com nome no padrão esperado (não precisa existir fisicamente)
    File fakeReplay = new File("1680000000_1234567890.dem");

    // act
    analyzer.analyzer(fakeReplay);

    // assert
    // verificamos que a factory foi invocada (opcional)
    verify(mockFactory, times(1)).create(any(File.class));
    // e, principalmente, que o método finish() do IEvent foi chamado
    verify(mockEvent, times(1)).finish();

    ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
    verify(mockRunner).runWith(captor.capture());
    Object[] processors = captor.getValue();

    assertEquals(3, processors.length);
    assertTrue(processors[0] instanceof GameTimesProcessor);
    assertTrue(processors[1] instanceof MatchDetailsProcessor);
    assertTrue(processors[2] instanceof BuybackLogProcessor);
  }

  @Test
  void testAnalyzerCallsRunWithWithRequiredProcessors() throws Exception {
    File mockFile = mock(File.class);
    when(mockFile.getName()).thenReturn("1234_5678.dem");

    SimpleRunner mockRunner = mock(SimpleRunner.class);
    when(mockFactory.create(any(File.class))).thenReturn(mockRunner);

    ReplayAnalyzer replayAnalyzer = new ReplayAnalyzer(List.of(mockEvent), mockFactory);

    replayAnalyzer.analyzer(mockFile);

    // Captura o array de processadores passados para runWith
    ArgumentCaptor<Object[]> captor = ArgumentCaptor.forClass(Object[].class);
    verify(mockRunner, times(1)).runWith(captor.capture());

    Object[] processors = captor.getValue();

    // Verificações:
    assertTrue(processors.length >= 2, "Deve conter GameTimesProcessor e MatchDetailsProcessor");

    boolean hasGameTimesProcessor = false;
    boolean hasMatchDetailsProcessor = false;

    for (Object proc : processors) {
      if (proc instanceof GameTimesProcessor) {
        hasGameTimesProcessor = true;
      }
      if (proc instanceof MatchDetailsProcessor) {
        hasMatchDetailsProcessor = true;
      }
    }

    assertTrue(hasGameTimesProcessor, "Processor obrigatório ausente: GameTimesProcessor");
    assertTrue(hasMatchDetailsProcessor, "Processor obrigatório ausente: MatchDetailsProcessor");
  }

}
