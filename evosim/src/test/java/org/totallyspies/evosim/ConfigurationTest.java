package org.totallyspies.evosim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests for Configuration class.
 *
 * @author edeli
 */
public class ConfigurationTest {

  @Test
  public void saveConfigurationsInTempFolder() {
    Configuration defaultConfiguration = new Configuration();
    assertEquals("Saved!", defaultConfiguration.saveFileConfiguration());
  }

  @Test
  public void something() {

  }

}
