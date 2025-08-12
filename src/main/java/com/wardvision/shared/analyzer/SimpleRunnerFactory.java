package com.wardvision.shared.analyzer;

import java.io.File;

import com.wardvision.shared.intercace.ISimpleRunnerFactory;

import skadistats.clarity.processor.runner.SimpleRunner;
import skadistats.clarity.source.MappedFileSource;

public class SimpleRunnerFactory implements ISimpleRunnerFactory {

  @Override
  public SimpleRunner create(File replayFile) throws Exception {
    MappedFileSource source = new MappedFileSource(replayFile);

    return new SimpleRunner(source);
  }
}
