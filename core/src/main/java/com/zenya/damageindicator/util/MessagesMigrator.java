/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2022  Pierce Thompson
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

package com.zenya.damageindicator.util;

import com.zenya.damageindicator.DamageIndicator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class MessagesMigrator {

    private static void migrate_v1_v2(JavaPlugin plugin, File localeFile) throws IOException {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists())
            return;

        YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);
        YamlConfiguration locale = new YamlConfiguration();

        locale.set("commands.no-permission", messages.getString("no-permission"));
        locale.set("commands.player-only", messages.getString("player-required"));
        locale.set("commands.toggle.enable", messages.getString("command.toggle.enable"));
        locale.set("commands.toggle.disable", messages.getString("command.toggle.disable"));
        locale.set("commands.reload", messages.getString("command.reload"));
        locale.set("health", messages.getString("health"));

        locale.save(localeFile);

        plugin.getLogger().info("Moved all 'messages.yml' messages to 'locale/en-us.yml'.");
        messagesFile.delete();
    }

    public static boolean migrate(DamageIndicator plugin, File localeFile) throws IOException {
        migrate_v1_v2(plugin, localeFile);

        YamlConfiguration messages = YamlConfiguration.loadConfiguration(localeFile);
        if (!messages.contains("prefix") && !messages.contains("commands.player-only"))
            return false; // The prefix key is only in legacy configs.

        YamlConfiguration locale = new YamlConfiguration();

        String prefix = messages.contains("prefix") ? messages.getString("prefix") + " " : "";

        String toggleEnable = messages.getString("commands.toggle.enable");
        locale.set("commands.toggle.enable", toggleEnable.contains("%p% ") ? prefix + toggleEnable.replace("%p% ", "") : toggleEnable);

        String toggleDisable = messages.getString("commands.toggle.disable");
        locale.set("commands.toggle.disable", toggleDisable.contains("%p% ") ? prefix + toggleDisable.replace("%p% ", "") : toggleDisable);

        locale.set("commands.reload", prefix + messages.getString("commands.reload", "").replace("%p% ", ""));
        locale.set("health", messages.getString("health"));

        locale.save(localeFile);

        plugin.getLogger().info("Migrated all legacy locale files messages to the new format.");
        return true;
    }

    private MessagesMigrator() {
        throw new UnsupportedOperationException("Utility class");
    }

}
