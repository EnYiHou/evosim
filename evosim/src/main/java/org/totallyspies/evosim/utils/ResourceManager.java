package org.totallyspies.evosim.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * A ResourceManager class provides resources definition used in
 * this application.
 *
 * @author edeli
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResourceManager {

    private static final String RESOURCE_FOLDER = "/org/totallyspies/evosim";
    private static final String FXML_FOLDER = RESOURCE_FOLDER + "/fxml";
    private static final String IMAGE_FOLDER = RESOURCE_FOLDER + "/images";
    private static final String SOUND_FOLDER = RESOURCE_FOLDER + "/sounds";
    private static final String STYLE_FOLDER = RESOURCE_FOLDER + "/css";
    private static final String JSON_FOLDER = RESOURCE_FOLDER + "/json";

    // FXML files
    public static final String FXML_MAIN_VIEW = FXML_FOLDER + "/main.fxml";
    public static final String FXML_WELCOME_VIEW =
            FXML_FOLDER + "/welcome.fxml";
    public static final String FXML_ABOUT = FXML_FOLDER + "/about.fxml";

    // Images
    public static final String IMAGE_WELCOME = IMAGE_FOLDER + "/welcome.png";

    // CSS
    public static final String CSS_GLOBAL = STYLE_FOLDER + "/styles.css";

    // JSON
    public static final String UI_TEXT = JSON_FOLDER + "/uiText.json";
}
