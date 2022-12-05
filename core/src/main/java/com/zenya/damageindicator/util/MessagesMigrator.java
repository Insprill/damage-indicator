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

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MessagesMigrator {

    public static void migrate(JavaPlugin plugin) {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists())
            return;

        YamlConfiguration messages = YamlConfiguration.loadConfiguration(messagesFile);

        Lang.getLocaleConfig().set("commands.no-permission", messages.getStringRaw("no-permission"));
        Lang.getLocaleConfig().set("commands.player-only", messages.getStringRaw("player-required"));
        Lang.getLocaleConfig().set("commands.toggle.enable", messages.getStringRaw("command.toggle.enable"));
        Lang.getLocaleConfig().set("commands.toggle.disable", messages.getStringRaw("command.toggle.disable"));
        Lang.getLocaleConfig().set("commands.reload", messages.getStringRaw("command.reload"));
        Lang.getLocaleConfig().set("health", messages.getStringRaw("health"));
        Lang.getLocaleConfig().save();

        plugin.getLogger().info("Moved all 'messages.yml' messages to 'locale/en-us.yml'.");
        messagesFile.delete();
    }

}
