package com.zenya.damageindicator.file;

import com.zenya.damageindicator.DamageIndicator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static ConfigManager INSTANCE = new ConfigManager();

    //Change this when updating config
    private static final int CONFIG_VERSION = 1;
    //Change this if config should reset when updating
    private boolean RESET_CONFIG = false;
    //These nodes will use the latest resource config's values
    private static final List<String> IGNORED_NODES = new ArrayList<String>(){{
        add("config-version");
    }};
    //These nodes will be emptied and replaced with old values instead of being appended
    //Applicable to keys and lists
    private static final List<String> REPLACE_NODES = new ArrayList<String>(){{

    }};

    private File configFile;
    private FileConfiguration config;

    public ConfigManager() {
        configFile = new File(DamageIndicator.INSTANCE.getDataFolder(), "config.yml");
        if(!getConfigExists()) {
            DamageIndicator.INSTANCE.saveDefaultConfig();
        }
        config = YamlConfiguration.loadConfiguration(configFile);

        if(getConfigVersion() != CONFIG_VERSION) {
            File oldConfigFile = new File(DamageIndicator.INSTANCE.getDataFolder(), "config.yml.v" + String.valueOf(getConfigVersion()));
            FileUtil.copy(configFile, oldConfigFile);
            FileConfiguration oldConfig = YamlConfiguration.loadConfiguration(oldConfigFile);

            //Refresh file
            configFile.delete();
            DamageIndicator.INSTANCE.saveDefaultConfig();
            config = YamlConfiguration.loadConfiguration(configFile);

            //Add old values
            if(!RESET_CONFIG && getConfigVersion() < CONFIG_VERSION) {
                for(String node : oldConfig.getKeys(true)) {
                    if(IGNORED_NODES.contains(node)) continue;
                    if(REPLACE_NODES.contains(node)) {
                        config.set(node, null);
                    }
                    if(oldConfig.getConfigurationSection(node) != null && oldConfig.getConfigurationSection(node).getKeys(false) != null && oldConfig.getConfigurationSection(node).getKeys(false).size() != 0) continue;
                    config.set(node, oldConfig.get(node));
                }
            }

            //Save regardless
            try {
                config.save(configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean getConfigExists() {
        return configFile.exists();
    }

    private int getConfigVersion() {
        return getInt("config-version");
    }

    public String getString(String node) {
        String val;
        try {
            val = config.getString(node);
        } catch(Exception e) {
            val = "";
        }
        return val;
    }

    public int getInt(String node) {
        int val;
        try {
            val = config.getInt(node);
        } catch(Exception e) {
            val = 0;
        }
        return val;
    }

    public double getDouble(String node) {
        double val;
        try {
            val = config.getDouble(node);
        } catch(Exception e) {
            val = 0d;
        }
        return val;
    }

    public boolean getBool(String node) {
        boolean val;
        try {
            val = config.getBoolean(node);
        } catch(Exception e) {
            val = false;
        }
        return val;
    }

    public ArrayList<String> getKeys(String node) {
        ArrayList<String> val = new ArrayList<String>();
        try {
            for(String key : config.getConfigurationSection(node).getKeys(false)) {
                val.add(key);
            }
        } catch(Exception e) {
            val = new ArrayList<String>();
            e.printStackTrace();
        }
        return val;
    }

    public ArrayList<String> getList(String node) {
        ArrayList<String> val = new ArrayList<String>();
        try {
            for(String s : config.getStringList(node)) {
                val.add(s);
            }
        } catch(Exception e) {
            val = new ArrayList<String>();
            e.printStackTrace();
        }
        return val;
    }

    public <T extends Number & Comparable<T>> String getNearestValue(String node, T reference, boolean returnLargerOnly) {
        List<String> keyList = getKeys(node);
        if(keyList != null && keyList.size() != 0) {
            double smallestDiff = Math.abs(Double.valueOf(keyList.get(0)) - reference.doubleValue());
            int smallestIndex = 0;
            for (int i = 1; i < keyList.size(); i++) {
                double difference = Math.abs(Double.valueOf(keyList.get(i)) - reference.doubleValue());
                if(returnLargerOnly) {
                    if ((Math.abs(reference.doubleValue()) >= Math.abs(Double.valueOf(keyList.get(i)))) && (difference < smallestDiff)) {
                        smallestDiff = difference;
                        smallestIndex = i;
                    }
                } else {
                    if (difference < smallestDiff) {
                        smallestDiff = difference;
                        smallestIndex = i;
                    }
                }
            }
            return String.valueOf(getString(node + "." + keyList.get(smallestIndex)));
        }
        return "";
    }

    public static void reloadConfig() {
        INSTANCE = new ConfigManager();
    }
}
