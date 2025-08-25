package com.wardvision.helpers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;

import skadistats.clarity.model.Entity;

class EntityPropertyHelperTest {

  @Test
  void testSafeGetPropertyReturnsValueWhenExists() {
    Entity mockEntity = mock(Entity.class);
    when(mockEntity.getProperty("health")).thenReturn(100);

    Integer health = EntityPropertyHelper.safeGetProperty(mockEntity, "health");

    assertEquals(100, health);
  }

  @Test
  void testSafeGetPropertyReturnsNullWhenPropertyNotFound() {
    Entity mockEntity = mock(Entity.class);
    when(mockEntity.getProperty("mana")).thenThrow(new IllegalArgumentException("property not found"));

    Object mana = EntityPropertyHelper.safeGetProperty(mockEntity, "mana");

    assertNull(mana);
  }
}
