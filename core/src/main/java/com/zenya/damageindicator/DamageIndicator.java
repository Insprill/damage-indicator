package com.zenya.damageindicator;

import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageIndicator extends JavaPlugin {
    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;

    public void onEnable() {
        INSTANCE = this;
        try {
            PROTOCOL_NMS = CompatibilityHandler.getProtocolNMS().newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        getServer().getPluginManager().registerEvents(new Listeners(), this);
    }

    public void onDisable() {

    }
}
