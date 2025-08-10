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

package com.zenya.damageindicator;

import com.zenya.damageindicator.command.ReloadArg;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import com.zenya.damageindicator.util.BukkitScheduler;
import com.zenya.damageindicator.util.MessagesMigrator;
import com.zenya.damageindicator.util.Scheduler;
import net.insprill.spigotutils.MinecraftVersion;
import net.insprill.spigotutils.ServerEnvironment;
import net.insprill.xenlib.XenLib;
import net.insprill.xenlib.commands.Command;
import net.insprill.xenlib.files.YamlFile;
import net.insprill.xenlib.files.YamlFolder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DamageIndicator extends JavaPlugin {

    private static final int BSTATS_ID = 15403;

    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;
    public static Scheduler SCHEDULER;

    @Override
    public void onEnable() {
        INSTANCE = this;

        new Metrics(this, BSTATS_ID);

        XenLib.init(this);

        // Migrate messages.yml to the default locale file.
        MessagesMigrator.migrate(this);

        // Fix syntax error in lang files caused by the '❤' character in the health line.
        boolean fixedAnyLangFiles = false;
        for (YamlFile langFile : YamlFolder.LOCALE.getDataFiles()) {
            Path path = langFile.getFile().toPath();
            try {
                boolean hasSyntaxError = false;
                List<String> contents = Files.readAllLines(path, StandardCharsets.UTF_8);
                for (int i = 0; i < contents.size(); i++) {
                    String content = contents.get(i);
                    if (content.contains("â¤")) {
                        contents.set(i, content.replace("â¤", "\\u2764"));
                        hasSyntaxError = true;
                        fixedAnyLangFiles = true;
                    }
                }
                if (hasSyntaxError) {
                    Files.write(path, contents, StandardCharsets.UTF_8);
                    getLogger().info("Fixed syntax error in " + langFile.getName() + ".yml");
                }
            } catch (IOException e) {
                getLogger().severe("Failed to fix syntax error in " + path + " (" + e.getMessage() + "). Please inspect the file and correct any syntax errors manually");
            }
        }
        if (fixedAnyLangFiles) {
            getLogger().info("Reloading lang files");
            YamlFolder.LOCALE.reload();
        }

        //Init all configs and storage files
        StorageFileManager.INSTANCE.reloadFiles();

        //Disable for versions below 1.8
        if (MinecraftVersion.isOlderThan(MinecraftVersion.v1_8_0)) {
            getLogger().severe("DamageIndicator does not support pre-1.8 versions of Minecraft!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Init scheduler
        try {
            SCHEDULER = ServerEnvironment.isPaper()
                    ? (Scheduler) Class.forName("net.insprill.damageindicator.util.PaperScheduler").getConstructor().newInstance()
                    : new BukkitScheduler();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Init NMS
        try {
            PROTOCOL_NMS = (ProtocolNMS) CompatibilityHandler.getProtocolNMS().getConstructors()[0].newInstance();
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands. Legacy servers try to register permissions again from the plugin.yml and throw errors.
        SCHEDULER.runDelayed(this, () -> new Command("damageindicator", ReloadArg.class.getPackage().getName()), 10L);

        HealthIndicator.INSTANCE.reload();

        // Cache toggle states for online players. Helps during debugging.
        for (Player player : Bukkit.getOnlinePlayers()) {
            ToggleManager.INSTANCE.isToggled(player.getUniqueId());
        }
    }

    @Override
    public void onDisable() {
        if (StorageFileManager.getDatabase() != null) {
            StorageFileManager.getDatabase().shutdown();
        }
        HandlerList.unregisterAll(INSTANCE);
    }

}
