package be.howest.photoweave.model.util;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConfigUtil {

    public static PropertiesConfiguration getPropertiesConfig() {
        if (!Files.exists(Paths.get("config.properties"))) {
            File f = new File("config.properties");
            boolean success;

            try {
                success = f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Configurations configs = new Configurations();
        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder("config.properties");
        builder.setAutoSave(true);

        PropertiesConfiguration config = null;

        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }

        return config;
    }

    public static ArrayList<String> getRecentFiles() {
        Object obj = getPropertiesConfig().getProperty("recentfiles");

        ArrayList<String> recentFiles = new ArrayList<>();

        if (obj != null) {
            if (obj instanceof String) {
                recentFiles.add((String) obj);
            } else if (obj instanceof ArrayList) {
                recentFiles = (ArrayList<String>) obj;
            }
        }

        return recentFiles;
    }
}
