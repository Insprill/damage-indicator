/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2024  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        if (getDatabase() != null) {
            getDatabase().shutdown();
        }
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
