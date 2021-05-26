package com.zenya.damageindicator;

import com.zenya.damageindicator.command.DamageIndicatorCommand;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageIndicator extends JavaPlugin {
    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;
    private StorageFileManager storageFileManager;

    public void onEnable() {
        INSTANCE = this;
        storageFileManager = StorageFileManager.INSTANCE;

        if(CompatibilityHandler.getProtocol() < 8) {
            onDisable();
            getServer().getPluginManager().disablePlugin(INSTANCE);
            return;
        }

        try {
            PROTOCOL_NMS = CompatibilityHandler.getProtocolNMS().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        this.getServer().getPluginManager().registerEvents(new Listeners(), this);
        this.getCommand("damageindicator").setExecutor(new DamageIndicatorCommand());
    }

    public void onDisable() {
        HandlerList.unregisterAll(INSTANCE);
    }
}
