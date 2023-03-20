package org.totallyspies.evosim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

/**
 * Tests for Configuration class.
 *
 * @author edeli
 */
public class ConfigurationTest {

  private Configuration defaultConfiguration = new Configuration();

  @Test
  public void savesFilePrint() {
    assertEquals("Saved!", defaultConfiguration.saveFileConfiguration());
  }

  @Test
  public void savedAtRightPath() {
    assertEquals(System.getProperty("java.io.tmpdir"), defaultConfiguration.loadFileConfiguration().getPath());
  }

  @Test
  public void loadContent() {

  }

}
