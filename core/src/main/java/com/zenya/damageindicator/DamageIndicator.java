package com.zenya.damageindicator;

import com.zenya.damageindicator.command.ReloadArg;
import com.zenya.damageindicator.event.Listeners;
import com.zenya.damageindicator.nms.CompatibilityHandler;
import com.zenya.damageindicator.nms.ProtocolNMS;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.util.MessagesMigrator;
import net.insprill.xenlib.MinecraftVersion;
import net.insprill.xenlib.XenLib;
import net.insprill.xenlib.commands.Command;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageIndicator extends JavaPlugin {

    private static final int BSTATS_ID = 15403;

    public static DamageIndicator INSTANCE;
    public static ProtocolNMS PROTOCOL_NMS;

    @Override
    public void onEnable() {
        INSTANCE = this;

        new Metrics(this, BSTATS_ID);

        XenLib.init(this);

        // Migrate messages.yml to the default locale file.
        MessagesMigrator.migrate(this);

        //Init all configs and storage files
        StorageFileManager.INSTANCE.reloadFiles();

        //Disable for versions below 1.8
        if (MinecraftVersion.isOlderThan(MinecraftVersion.v1_8_0)) {
            getLogger().severe("DamageIndicator does not support pre-1.8 versions of Minecraft!");
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
        new Command("damageindicator", ReloadArg.class.getPackage().getName());

        HealthIndicator.INSTANCE.reload();
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(INSTANCE);
    }

}
