package by.siarhei.beerfest.config;

import java.util.ResourceBundle;

public class MailConfigurationManager {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("mail");

    private MailConfigurationManager() {
    }

    public static String getProperty(String key) {
        return resourceBundle.getString(key);
    }
}
