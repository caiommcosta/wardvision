package com.wardvision;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerLevelTest {

  @Test
  void testLoggerLevels() {
    Logger runnerLogger = LoggerFactory.getLogger("runner");
    Logger myLogger = LoggerFactory.getLogger(LoggerLevelTest.class);

    runnerLogger.warn("Esse WARN do runner deve sumir se a configuração estiver correta.");
    myLogger.info("Esse INFO da minha classe deve aparecer normalmente.");
  }
}
