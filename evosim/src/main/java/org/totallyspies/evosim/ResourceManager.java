package org.totallyspies.evosim;

/**
 * A ResourceManager class provides resources definition used in
 * this application.
 *
 * @author edeli
 */
public final class ResourceManager {

  /**
   * Private constructor to make static class.
   */
  private ResourceManager() {
  }

  private static final String RESOURCE_FOLDER = "/org/totallyspies/evosim";
  private static final String FXML_FOLDER = RESOURCE_FOLDER + "/fxml";
  private static final String IMAGE_FOLDER = RESOURCE_FOLDER + "/images";
  private static final String SOUND_FOLDER = RESOURCE_FOLDER + "/sounds";

  // FXML files
  public static final String FXML_MAIN_VIEW = FXML_FOLDER + "/main.fxml";

}
