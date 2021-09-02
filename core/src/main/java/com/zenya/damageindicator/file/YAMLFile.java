package com.zenya.damageindicator.file;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class YAMLFile extends StorageFile {

    private final FileConfiguration origConfig;
    private FileConfiguration config;

    public YAMLFile(String fileName) {
        this(DamageIndicator.INSTANCE.getDataFolder().getPath(), fileName);
    }

    public YAMLFile(String directory, String fileName) {
        this(directory, fileName, null, false, null, null);
    }

    /**
     * @param directory    Directory the file exists in.
     * @param fileName     Full name of the file, excluding its directory.
     * @param fileVersion  Version of the file as specified in "config-version.
     * @param resetFile    Whether the file should be deleted and overwritten by the original resource.
     * @param ignoredNodes Nodes that will use the latest resource config's values.
     * @param replaceNodes Nodes that will use old config values instead of being appended (applicable to nested keys)
     */
    public YAMLFile(String directory, String fileName, Integer fileVersion, boolean resetFile, List<String> ignoredNodes, List<String> replaceNodes) {
        super(directory, fileName, fileVersion, resetFile);

        this.origConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(DamageIndicator.INSTANCE.getResource(fileName)));
        this.config = YamlConfiguration.loadConfiguration(file);

        if (fileVersion != null) {
            try {
                updateFile(ignoredNodes, replaceNodes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int getFileVersion() {
        return getInt("config-version");
    }

    private void updateFile(List<String> ignoredNodes, List<String> replaceNodes) throws IOException {
        if (!file.exists()) {
            //Init file
            DamageIndicator.INSTANCE.saveResource(fileName, false);
            config = YamlConfiguration.loadConfiguration(file);
        } else {
            //Reset file for backward-compatibility
            if (getFileVersion() > fileVersion) resetFile = true;

            //Update file
            if (getFileVersion() != fileVersion) {
                File oldConfigFile = new File(directory, fileName + ".v" + getFileVersion());
                FileUtil.copy(file, oldConfigFile);
                file.delete();
                origConfig.save(file);

                FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);
                config = YamlConfiguration.loadConfiguration(file);

                //Add old values
                if (!resetFile) {
                    for (String node : config.getKeys(true)) {
                        if (ignoredNodes != null && ignoredNodes.contains(node)) continue;
                        if (oldConfig.getKeys(true).contains(node + ".")) continue;
                        if (replaceNodes != null && replaceNodes.contains(node)) {
                            config.set(node, null);
                            config.createSection(node);
                        }
                        if (oldConfig.contains(node, true)) {
                            config.set(node, oldConfig.get(node));
                        }
                    }
                    config.save(file);
                } else {
                    //Doing this keeps all the yml file comments
                    DamageIndicator.INSTANCE.saveResource(fileName, true);
                }
            }
        }
    }

    public String getString(String node) {
        return config.getString(node);
    }

    public int getInt(String node) {
        return config.getInt(node);
    }

    public double getDouble(String node) {
        return config.getDouble(node);
    }

    public boolean getBool(String node) {
        return config.getBoolean(node);
    }

    public List<String> getKeys(String node) {
        List<String> val;
        try {
            val = new ArrayList<>(config.getConfigurationSection(node).getKeys(false));
        } catch (Exception e) {
            e.printStackTrace();
            val = new ArrayList<>();
        }
        return val;
    }

    public List<String> getList(String node) {
        return config.getStringList(node);
    }

    public boolean isList(String node) {
        return config.isList(node);
    }

    public boolean listContains(String node, String item) {
        List<String> list = getList(node);
        return list != null && list.size() != 0 && list.contains(item);
    }

    /**
     * Checks if a String is blacklisted.
     *
     * @param node   Path to blacklist.
     * @param string String to test.
     * @return True if the string is blacklist is enabled and the string is blacklisted, false otherwise.
     */
    public boolean isBlackListed(String node, String string) {
        if (!StorageFileManager.getConfig().getBool(node + "-enabled:"))
            return false;
        return StorageFileManager.getConfig().listContains(node, string);
    }

    /**
     * Checks if a String is whitelisted.
     *
     * @param node   Path to whitelist.
     * @param string String to test.
     * @return True if the whitelist is disabled, or the string is whitelisted, false otherwise.
     */
    public boolean isWhiteListed(String node, String string) {
        if (!StorageFileManager.getConfig().getBool(node + "-enabled:"))
            return true;
        return !StorageFileManager.getConfig().listContains(node, string);
    }

    public <T extends Number & Comparable<T>> String getNearestValue(String node, T reference, RoundingMode mode) {
        List<String> keyList = getKeys(node);
        if (keyList != null && keyList.size() != 0) {
            double smallestDiff = Math.abs(Double.parseDouble(keyList.get(0)) - reference.doubleValue());
            int smallestIndex = 0;
            for (int i = 1; i < keyList.size(); i++) {
                double difference = Math.abs(Double.parseDouble(keyList.get(i)) - reference.doubleValue());
                if (difference <= smallestDiff) {
                    switch (mode) {
                        case DOWN:
                            if (Math.abs(reference.doubleValue()) >= Math.abs(Double.parseDouble(keyList.get(i)))) {
                                smallestDiff = difference;
                                smallestIndex = i;
                            }
                            break;
                        case UP:
                            if (Math.abs(reference.doubleValue()) <= Math.abs(Double.parseDouble(keyList.get(i)))) {
                                smallestDiff = difference;
                                smallestIndex = i;
                            }
                            break;
                        default:
                            smallestDiff = difference;
                            smallestIndex = i;
                    }
                }
            }
            return String.valueOf(getString(node + "." + keyList.get(smallestIndex)));
        }
        return "";
    }
}


