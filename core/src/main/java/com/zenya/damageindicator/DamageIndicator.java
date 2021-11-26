package com.zenya.damageindicator;

import com.zenya.damageindicator.command.DamageIndicatorCommand;
import com.zenya.damageindicator.command.DamageIndicatorTab;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
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
