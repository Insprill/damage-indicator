package com.zenya.damageindicator.util;

import co.aikar.commands.BukkitLocales;

import java.io.File;

public class Lang {

    private final BukkitLocales locales;
    private final File file;

    public Lang(BukkitLocales locales, File file) {
        this.locales = locales;
        this.file = file;
    }

    public BukkitLocales getLocales() {
        return this.locales;
    }

    public File getFile() {
        return this.file;
    }

}
