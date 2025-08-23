package com.wardvision.shared.analyzer;

import java.io.File;
import java.util.List;

import com.wardvision.shared.entities.MatchContext;
import com.wardvision.shared.gametimes.processor.GameTimesProcessor;
import com.wardvision.shared.intercace.IEvent;
import com.wardvision.shared.intercace.IReplayAnalyzer;
import com.wardvision.shared.intercace.ISimpleRunnerFactory;
import com.wardvision.shared.match_details.processor.MatchDetailsProcessor;
import com.wardvision.helpers.ReplayFileHelper;

import skadistats.clarity.processor.runner.SimpleRunner;

/*
 * Para que cada replay seja analisado uma única vez, 
 * é necessário despachar eventos diferentes para cada recurso (feature) da aplicação.
 * Assim, cada '.dem' é processado uma única vez e evita duplicação de análises.
 * Tudo está centralizado no ReplayAnalyzer, que é responsável por iniciar
 * o processamento e despachar os eventos para as features
*/

public class ReplayAnalyzer implements IReplayAnalyzer {

  private final List<IEvent<?>> eventControllers;
  private final ISimpleRunnerFactory runnerFactory;

  public ReplayAnalyzer(List<IEvent<?>> eventControllers) {
    this(eventControllers, new SimpleRunnerFactory());
  }

  public ReplayAnalyzer(List<IEvent<?>> eventControllers, ISimpleRunnerFactory runnerFactory) {
    this.eventControllers = eventControllers;
    this.runnerFactory = runnerFactory;
  }

  @Override
  public void analyzer(File replayFile) {
    try {

      final String matchId = ReplayFileHelper.extractMatchId(replayFile.getName());
      final String timestamp = ReplayFileHelper.extractTimestamp(replayFile.getName());

      // Global processors
      GameTimesProcessor gameTimeProcessor = new GameTimesProcessor();
      MatchDetailsProcessor teamPlayersProcessor = new MatchDetailsProcessor();

      MatchContext context = new MatchContext(
          matchId,
          timestamp,
          gameTimeProcessor.getGameTime(),
          teamPlayersProcessor.getMatchPlayers(),
          teamPlayersProcessor.getHeroToPlayer(),
          teamPlayersProcessor.getTeamNames());

      // Feature processors
      BuybackLogProcessor buybackLogProcessor = new BuybackLogProcessor(context);

      SimpleRunner runner = runnerFactory.create(replayFile);
      runner.runWith(gameTimeProcessor, teamPlayersProcessor,
          buybackLogProcessor);

      dispatch(null);

      for (IEvent<?> eventController : eventControllers) {
        eventController.finish();
      }
    } catch (

    Exception e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private <T> void dispatch(List<T> events) {
    if (events == null || events.isEmpty())
      return;

    Class<?> eventType = events.get(0).getClass();
    for (IEvent<?> controller : eventControllers) {
      if (controller.getEventType().isAssignableFrom(eventType)) {
        ((IEvent<T>) controller).handle(events);
      }
    }
  }
}
