package de.acepe.onkyoremote;

public enum Screens {
    MAIN_VIEW("ui/MainView.fxml", "Remote", 830, 470), SETTINGS("ui/SettingsView.fxml", "Einstellungen", 630, 430);

    private final String resource;
    private final String title;
    private final int width;
    private final int height;

    Screens(String resource, String title, int width, int height) {
        this.resource = resource;
        this.title = title;
        this.width = width;
        this.height = height;
    }

    public String getResource() {
        return resource;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
