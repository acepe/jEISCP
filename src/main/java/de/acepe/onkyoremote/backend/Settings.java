package de.acepe.onkyoremote.backend;

import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.acepe.onkyoremote.backend.data.StoredSettings;

public class Settings {
    private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
    private static final String APP_DIR = ".OnkyoRemote";
    private static final String SETTINGS_FILE = "settings.json";

    private static Settings instance;

    private StoredSettings storedSettings;

    private Settings() {
        storedSettings = loadSettings();
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    private StoredSettings loadSettings() {
        File file = new File(getSettingsFile());
        if (!file.exists()) {
            return createDefault();
        }

        try (Reader jsonReader = new InputStreamReader(new FileInputStream(file), "utf-8")) {
            Gson gson = new GsonBuilder().create();
            return gson.fromJson(jsonReader, StoredSettings.class);
        } catch (IOException e) {
            LOG.error("Couldn't read data file: {}", SETTINGS_FILE, e);
            return createDefault();
        }
    }

    private StoredSettings createDefault() {
        return new StoredSettings();
    }

    public void saveSettings() {
        snycToSettings();

        Gson gson = new GsonBuilder().create();
        String settingsJson = gson.toJson(storedSettings);
        createAppDir();

        String settingsFile = getSettingsFile();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(settingsFile), "utf-8")) {
            LOG.info("writing settings to file {}", settingsFile);
            writer.write(settingsJson);
        } catch (Exception e) {
            LOG.error("Couldn't write settings to file {}", settingsFile);
        }
    }

    private void snycToSettings() {
        // storedSettings.setAutocrawlEnabled(autoCrawlEnabled.get());
    }

    private void createAppDir() {
        String path = getAppDir();
        File appDir = new File(path);
        if (!appDir.exists()) {
            LOG.debug("creating App-Dir {}" + path);
            if (!appDir.mkdir()) {
                LOG.error("Couldn't create settings directory: " + path);
            }
        }
    }

    private String getSettingsFile() {
        return getAppDir() + File.separator + SETTINGS_FILE;
    }

    private String getAppDir() {
        return System.getProperty("user.home") + File.separator + APP_DIR;
    }

    public StoredSettings getStoredSettings() {
        return storedSettings;
    }

}
