package org.totallyspies.evosim.utils;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.totallyspies.evosim.utils.Configuration.Defaults;

/**
 * Tests for Configuration class.
 *
 * @author niakouu
 */
@SuppressWarnings({
        "checkstyle:JavadocVariable",
        "checkstyle:MissingJavadocMethod"})
public class ConfigurationTest {

  private Configuration configuration = Configuration.getConfiguration();

  @Test
  public void saveConfiguration() throws IOException {
    configuration.saveLatestConfiguration(null);
    Defaults.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadDefaultConfiguration() throws IOException {
    configuration.saveLatestConfiguration(null);
    Configuration.getConfiguration().loadLastConfiguration();

    Defaults.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadLastConfiguration() throws IOException {
    configuration.setEntityMaxSpeed(67d);
    configuration.setPreySplitEnergyFillingSpeed(45d);
    configuration.saveLatestConfiguration(null);

    Defaults.LATEST_CONFIGURATION.delete();
  }

}
