package com.zenya.damageindicator.storage;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.file.DBFile;
import com.zenya.damageindicator.file.StorageFile;
import com.zenya.damageindicator.file.YAMLFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StorageFileManager {
    /**
     * config.yml
     **/
    private static final int CONFIG_FILE_VERSION = 4;
    private static final boolean CONFIG_RESET_FILE = false;
    private static final List<String> CONFIG_IGNORED_NODES = new ArrayList<String>() {{
        add("config-version");
    }};
    private static final List<String> CONFIG_REPLACE_NODES = new ArrayList<String>() {{
        add("disabled-worlds");
    }};

    /**
     * database.db
     **/
    private static final int DATABASE_FILE_VERSION = 0; //Unused for now
    private static final boolean DATABASE_RESET_FILE = false;

    public static final StorageFileManager INSTANCE = new StorageFileManager();
    private final HashMap<String, StorageFile> fileMap = new HashMap<>();

    public void reloadFiles() {
        INSTANCE.fileMap.clear();
        registerFile("config.yml", new YAMLFile(DamageIndicator.INSTANCE.getDataFolder().getPath(), "config.yml", CONFIG_FILE_VERSION, CONFIG_RESET_FILE, CONFIG_IGNORED_NODES, CONFIG_REPLACE_NODES));
        registerFile("database.db", new DBFile(DamageIndicator.INSTANCE.getDataFolder().getPath(), "database.db", DATABASE_FILE_VERSION, DATABASE_RESET_FILE));
    }

    public StorageFile getFile(String fileName) {
        return fileMap.get(fileName);
    }

    public YAMLFile getYAMLFile(String fileName) {
        return (YAMLFile) getFile(fileName);
    }

    public DBFile getDBFile(String fileName) {
        return (DBFile) getFile(fileName);
    }

    public Set<String> getFileNames() {
        return fileMap.keySet();
    }

    public void registerFile(String fileName, StorageFile file) {
        fileMap.put(fileName, file);
    }

    public void unregisterFile(String fileName) {
        fileMap.remove(fileName);
    }

    public static YAMLFile getConfig() {
        return (YAMLFile) INSTANCE.getFile("config.yml");
    }

    public static DBFile getDatabase() {
        return (DBFile) INSTANCE.getFile("database.db");
    }
}
