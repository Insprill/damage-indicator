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

package com.zenya.damageindicator;

import co.aikar.commands.PaperCommandManager;
import com.zenya.damageindicator.command.CommandCompletion;
import com.zenya.damageindicator.command.DiCommand;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.util.MessagesMigrator;
import net.insprill.spigotutils.MinecraftVersion;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DamageIndicator extends JavaPlugin {

    private static final int BSTATS_ID = 15403;

    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;

    @Override
    public void onEnable() {
        INSTANCE = this;

        new Metrics(this, BSTATS_ID);

        // Init all configs and storage files
        StorageFileManager.INSTANCE.reloadFiles();

        String requestedLang = StorageFileManager.getConfig().getString("language");
        File localeFolder = new File(getDataFolder(), "locale");
        File localeFile = new File(localeFolder, requestedLang + ".yml");
        if (!localeFile.isFile()) {
            getLogger().severe("Failed to find locale file for '" + requestedLang + "'!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Migrate legacy configs
        try {
            if (MessagesMigrator.migrate(this, localeFile)) {
                // Update locale file if changed
                StorageFileManager.INSTANCE.reloadFiles();
            }
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Failed to migrate messages file!", e);
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Disable for versions below 1.8
        if (MinecraftVersion.isOlderThan(MinecraftVersion.v1_8_0)) {
            getLogger().severe("DamageIndicator does not support pre-1.8 versions of Minecraft!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Init NMS
        try {
            PROTOCOL_NMS = (ProtocolNMS) CompatibilityHandler.getProtocolNMS().getConstructors()[0].newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        // Commands
        PaperCommandManager commandManager = new PaperCommandManager(this);

        Locale requestedLocale = new Locale(requestedLang);
        commandManager.addSupportedLanguage(requestedLocale);

        Optional<Locale> locale = commandManager.getSupportedLanguages().stream().filter(it -> it.equals(requestedLocale)).findFirst();
        if (locale.isPresent() && localeFile.exists()) {
            commandManager.getLocales().setDefaultLocale(locale.get());
        } else {
            getLogger().log(Level.SEVERE, "Unsupported language '{}'. Defaulting to 'en'. Please choose from one of the following: {}", new Object[]{ requestedLang, commandManager.getSupportedLanguages().stream().map(Locale::getLanguage).collect(Collectors.toList()) });
        }

        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");

        CommandCompletion.register(commandManager);

        DiCommand cjmCommand = new DiCommand();
        commandManager.registerCommand(cjmCommand);

        HealthIndicator.INSTANCE.reload();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(INSTANCE);
    }

}
