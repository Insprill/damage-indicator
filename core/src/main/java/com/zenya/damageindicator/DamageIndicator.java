package com.zenya.damageindicator;

import com.zenya.damageindicator.command.DamageIndicatorCommand;
import com.zenya.damageindicator.command.DamageIndicatorTab;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageIndicator extends JavaPlugin {

    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;

    public void onEnable() {
        INSTANCE = this;

        //Init all configs and storage files
        //noinspection UnusedDeclaration
        StorageFileManager storageFileManager = StorageFileManager.INSTANCE;

        //Disable for versions below 1.8
        if (CompatibilityHandler.getProtocol() < 8) {
            onDisable();
            getServer().getPluginManager().disablePlugin(INSTANCE);
            return;
        }

        //Init NMS
        //Spigot buyer ID check in here
        try {
            PROTOCOL_NMS = CompatibilityHandler.getProtocolNMS().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        //Register events
        this.getServer().getPluginManager().registerEvents(new Listeners(), this);

        //Register commands
        this.getCommand("damageindicator").setExecutor(new DamageIndicatorCommand());
        try {
            this.getCommand("damageindicator").setTabCompleter(new DamageIndicatorTab());
        } catch (Exception exc) {
            //Do nothing, version doesn't support tabcomplete
        }

        HealthIndicator.INSTANCE.reload();
    }

    public void onDisable() {
        HandlerList.unregisterAll(INSTANCE);
    }
}