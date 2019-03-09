package ducky.gui;

import java.util.prefs.Preferences;

public class Settings {
    private static final String SETTINGS_NODE_NAME = "duckyui";
    private static final String INPUT_FILE = "INPUT_FILE";
    private static final String OUTPUT_FILE = "OUTPUT_FILE";
    private static final String LAYOUT = "LAYOUT";
    private static final String EMPTY = "";
    private Preferences preferences;

    public Settings() {
        preferences = Preferences.userRoot().node(SETTINGS_NODE_NAME);
    }

    public String getInputFilePath() {
        return preferences.get(INPUT_FILE, EMPTY);
    }

    public void setInputFilePath(String inputFilePath) {
        preferences.put(INPUT_FILE, inputFilePath);
    }

    public String getOutputFilePath() {
        return preferences.get(OUTPUT_FILE, EMPTY);
    }

    public void setOutputFilePath(String outputFilePath) {
        preferences.put(OUTPUT_FILE, outputFilePath);
    }

    public String getLayout() {
        return preferences.get(LAYOUT, "us");
    }

    public void setLayout(String layout) {
        preferences.put(LAYOUT, layout);
    }
}
