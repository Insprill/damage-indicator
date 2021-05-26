package com.zenya.damageindicator.file;


import com.zenya.damageindicator.DamageIndicator;

import java.io.File;

public abstract class StorageFile {
    public String directory;
    public String fileName;
    public Integer fileVersion;
    public boolean resetFile;
    public File file;

    public StorageFile(String fileName) {
        this(DamageIndicator.INSTANCE.getDataFolder().getPath(), fileName);
    }

    public StorageFile(String directory, String fileName) {
        this(directory, fileName, null, false);
    }

    public StorageFile(String directory, String fileName, Integer fileVersion, boolean resetFile) {
        this.directory = directory;
        this.fileName = fileName;
        this.fileVersion = fileVersion;
        this.resetFile = resetFile;
        this.file = new File(directory, fileName);
    }
}
