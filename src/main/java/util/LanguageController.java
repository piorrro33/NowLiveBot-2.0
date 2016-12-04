package util;

import util.database.calls.GetBotLang;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Veteran Software by Ague Mort
 */
public class LanguageController {

    private String lang;

    public ResourceBundle LanguageController(String guildId) {
        String language = new GetBotLang().action(guildId);

        if (language != null) {
            this.lang = language;
        } else {
            this.lang = "en";
        }
        Locale locale = new Locale(this.lang);
        ResourceBundle languageFile = ResourceBundle.getBundle("LanguageBundle", locale);

        return languageFile;
    }

}
