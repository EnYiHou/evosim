package org.totallyspies.evosim;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests for Configuration class.
 *
 * @author edeli
 */
public class ConfigurationTest {

  private Configuration defaultConfiguration = Configuration.getConfiguration();

  private Configuration testConfiguration = Configuration.getConfiguration();

  @Test
  public void savesFilePrint() {
    assertEquals("Saved!", defaultConfiguration.saveLastConfiguration());
    assertEquals("Saved!",
        defaultConfiguration.saveFileConfiguration("helloWorld.json"));
  }

  @Test
  public void loadFileSavesDefaultConfiguration() {
    assertEquals(defaultConfiguration.toString(),
        Configuration.getConfiguration("defaultConfigurations.json")
            .toString());
  }

  @Test
  public void loadFileSavesLastConfiguration() {
    defaultConfiguration.setEntityMaxSpeed(67d);
    defaultConfiguration.setPreySplitEnergyFillingSpeed(45d);
    defaultConfiguration.saveLastConfiguration();
    assertEquals(defaultConfiguration.toString(),
        Configuration.getLast().toString());
  }

  @Test
  public void loadFile() {
    testConfiguration.setNeuralNetworkLayersNumber(8);
    testConfiguration.setEntityMaxSpeed(34d);
    testConfiguration.saveFileConfiguration("testConfiguration.json");

  }
}
