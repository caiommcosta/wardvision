package com.wardvision.shared.intercace;

import java.io.File;

import skadistats.clarity.processor.runner.SimpleRunner;

public interface ISimpleRunnerFactory {
  SimpleRunner create(File replayFile) throws Exception;
}
