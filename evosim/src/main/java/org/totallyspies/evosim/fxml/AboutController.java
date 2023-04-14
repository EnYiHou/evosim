package org.totallyspies.evosim.fxml;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import lombok.NoArgsConstructor;
import org.json.JSONObject;
import org.totallyspies.evosim.utils.ResourceManager;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Controller for the {@code about.fxml} file. Dynamically adds all input fields.
 *
 * @author mattlep11
 */
@NoArgsConstructor
public final class AboutController {

    /**
     * A Text object used to display the developer info to the user.
     */
    @FXML
    private Text txtDevelopers;

    /**
     * A Text object used to display the project description to the user.
     */
    @FXML
    private Text txtDescription;

    /**
     * The key needed to access the saved strings in the project's json.
     */
    private static final String ABT_TEXT_JSON_KEY = "about-ui-text";

    /**
     * The key needed to access the text for {@code #txtDevelopers}.
     */
    private static final String DEVELOPERS_TEXT_JSON_KEY = "txtDevelopers";

    /**
     * The key needed to access the text for {@code #txtDescription}.
     */
    private static final String DESCRIPTION_TEXT_JSON_KEY = "txtDescription";

    /**
     * Initializes {@code about.fxml} by adding strings from a json to the
     * Text nodes.
     */
    public void initialize() {
        addMenuText(getClass().getResourceAsStream(ResourceManager.UI_TEXT));
    }

    /**
     * Loads the AboutWindow text from the project's json file.
     *
     * @param path The input stream path
     */
    private void addMenuText(final InputStream path) {
        String jsonText = "";

        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(path))) {
                String line = reader.readLine();

                while (line != null) {
                    jsonText += line;
                    line = reader.readLine();
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // create json object for the about-ui specifically
        JSONObject ui = new JSONObject(jsonText);
        JSONObject aboutUi = ui.getJSONObject(ABT_TEXT_JSON_KEY);

        txtDevelopers.setText(aboutUi.getString(DEVELOPERS_TEXT_JSON_KEY));
        txtDescription.setText(aboutUi.getString(DESCRIPTION_TEXT_JSON_KEY));
    }
}
