package org.totallyspies.evosim.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.totallyspies.evosim.utils.Configuration.Defaults;

/**
 * Tests for Configuration class.
 *
 * @author niakouu
 */
public class ConfigurationTest {

  private Configuration configuration = Configuration.getCONFIGURATION();

  @Test
  public void saveConfiguration() throws IOException {
    configuration.saveLatestConfiguration();
    Defaults.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadDefaultConfiguration() throws IOException {
    configuration.saveLatestConfiguration();
    Configuration.getCONFIGURATION().loadLastConfiguration();

    Defaults.LATEST_CONFIGURATION.delete();
  }

  @Test
  public void loadLastConfiguration() throws IOException {
    configuration.setEntityMaxSpeed(67d);
    configuration.setPreySplitEnergyFillingSpeed(45d);
    configuration.saveLatestConfiguration();

    Defaults.LATEST_CONFIGURATION.delete();
  }

}
