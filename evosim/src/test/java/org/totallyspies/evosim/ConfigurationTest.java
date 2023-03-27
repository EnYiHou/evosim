package org.totallyspies.evosim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.totallyspies.evosim.utils.Configuration;
import org.totallyspies.evosim.utils.Configuration.Defaults;

/**
 * Tests for Configuration class.
 *
 * @author niakouu
 */
public class ConfigurationTest {

  private Configuration configuration = Configuration.getConfiguration();

  @Test
  public void saveConfiguration() throws IOException {
    configuration.saveConfiguration();
    deleteFile(Defaults.DEFAULT_CONFIGURATION_FILE_NAME);
  }

  @Test
  public void loadDefaultConfiguration() throws IOException {
    configuration.saveConfiguration();
    assertEquals(configuration.toString(),
        Configuration.getConfiguration(Defaults.DEFAULT_CONFIGURATION_FILE_NAME)
            .toString());

    deleteFile(Defaults.DEFAULT_CONFIGURATION_FILE_NAME);
  }

  @Test
  public void loadLastConfiguration() throws IOException {
    configuration.setEntityMaxSpeed(67d);
    configuration.setPreySplitEnergyFillingSpeed(45d);
    configuration.saveConfiguration();
    assertEquals(configuration.toString(),
        Configuration.getLast().toString());

    deleteFile(Defaults.DEFAULT_CONFIGURATION_FILE_NAME);
  }

  @Test
  public void loadNamedFile() throws IOException {
    configuration.setNeuralNetworkLayersNumber(8);
    configuration.setEntityMaxSpeed(34d);
    configuration.saveConfiguration("testConfiguration.json");

    deleteFile("testConfiguration.json");
  }

  public void deleteFile(String fileName) {
    File file = new File(System.getProperty("java.io.tmpdir"),
        fileName);
    file.delete();
  }
}
