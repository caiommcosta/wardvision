package com.wardvision.shared.intercace;

import java.util.List;

public interface IEvent<T> {

  public void handle(List<T> events);

  public void finish();

  Class<T> getEventType();
}
