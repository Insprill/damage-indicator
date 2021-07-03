package com.zenya.damageindicator.nms;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompatibilityHandler {
    private static final String UUID = "%%__USER__%%";
    private static final String PACKAGE_DOMAIN = "com.zenya.damageindicator.nms.";
    private static final String CLASS_NAME = ".ProtocolNMSImpl";

    public CompatibilityHandler() {
        List<String> blacklist = new ArrayList<>();

        try {
            URL url = new URL("http://plugins.zenya.dev/blacklist.txt");
            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length; (length = in.read(buffer)) != -1; ) {
                out.write(buffer, 0, length);
            }
            blacklist = Arrays.asList(out.toString("UTF-8").split("\\r?\\n|\\r"));
        } catch (IOException exc) {
            exc.printStackTrace();
        }

        if(UUID.startsWith("%")) {
            Logger.logInfo("Thank you for helping to beta test DamageIndicator :)");
        }
        if(blacklist.contains(UUID)) {
            Logger.logInfo("You are currently using a leaked version of DamageIndicator :(");
            Logger.logInfo("This plugin took me a whole lot of time, effort and energy to make <3");
            Logger.logInfo("If you like my work, consider purchasing a legitimate copy instead at");
            Logger.logInfo("https://www.spigotmc.org/resources/%E2%98%A0%EF%B8%8Fdamageindicator%E2%98%A0%EF%B8%8F-customisable-damage-indicator-multicolor-support-100-lagless.92423/");
            Logger.logError("Shame on Spigot user ID " + UUID + " for pirating my work D:");
            Bukkit.getServer().getPluginManager().disablePlugin(DamageIndicator.INSTANCE);
            Bukkit.getServer().shutdown();
            return;
        }
    }

    public static String getVersion() {
        /*
        1.8 - v1_8_R1
        1.8.3 - v1_8_R2
        1.8.8 - v1_8_R3
        1.9.2 - v1_9_R1
        1.9.4 - v1_9_R2
        1.10.2 - v1_10_R1
        1.11.2 - v1_11_R1
        1.12.2 - v1_12_R1
        1.13 - v1_13_R1
        1.13.2 - v1_13_R2
        1.14.4 - v1_14_R1
        1.15.2 - v1_15_R1
        1.16.1 - v1_16_R1
        1.16.3 - v1_16_R2
        1.16.5 - v1_16_R3
        */
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        return version;
    }

    public static int getProtocol() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }

    public static Class<? extends ProtocolNMS> getProtocolNMS() throws ClassNotFoundException {
        try {
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + getVersion() + CLASS_NAME);
        } catch (Exception exc) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "You are running DamageIndicator on an unsupported NMS version " + getVersion());
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Some features like may be disabled or broken");
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + "fallback" + CLASS_NAME);
        }
    }
}

