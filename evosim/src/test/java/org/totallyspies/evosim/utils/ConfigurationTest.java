package org.totallyspies.evosim.utils;

import org.junit.jupiter.api.Test;

/**
 * Tests for Configuration class.
 *
 * @author niakouu
 */
@SuppressWarnings({
        "checkstyle:JavadocVariable",
        "checkstyle:MissingJavadocMethod",
        "checkstyle:MagicNumber"
})
public class ConfigurationTest {

  private Configuration configuration = Configuration.getConfiguration();

  @Test
  public void saveConfiguration() throws EvosimException {
    configuration.saveLatestConfiguration(null);
    configuration.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadDefaultConfiguration() throws EvosimException {
    configuration.saveLatestConfiguration(null);
    Configuration.getConfiguration().loadLastFile();

    configuration.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadLastConfiguration() throws EvosimException {
    configuration.setEntityMaxSpeed(67d);
    configuration.setPreySplitEnergyFillingSpeed(45d);
    configuration.saveLatestConfiguration(null);

    configuration.LATEST_CONFIGURATION.delete();
  }

}
