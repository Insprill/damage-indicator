package com.zenya.damageindicator.util;

import net.insprill.xenlib.files.YamlFile;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MessagesMigrator {

    public static void migrate(JavaPlugin plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists())
            return;

        YamlFile messages = new YamlFile(messagesFile);

        if (!Lang.getLocaleConfig().isModifiable())
            return;

        Lang.getLocaleConfig().set("commands.no-permission", messages.getStringRaw("no-permission"));
        Lang.getLocaleConfig().set("commands.player-only", messages.getStringRaw("player-required"));
        Lang.getLocaleConfig().set("commands.help", messages.getStringRaw("command.help"));
        Lang.getLocaleConfig().set("commands.toggle.enable", messages.getStringRaw("command.toggle.enable"));
        Lang.getLocaleConfig().set("commands.toggle.disable", messages.getStringRaw("command.toggle.disable"));
        Lang.getLocaleConfig().set("commands.reload", messages.getStringRaw("command.reload"));
        Lang.getLocaleConfig().set("health", messages.getStringRaw("health"));
        Lang.getLocaleConfig().save();

        plugin.getLogger().info("Moved all 'messages.yml' messages to 'locale/en-us.yml'.");
        messagesFile.delete();
    }

}
