package com.zenya.damageindicator.nms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class CompatibilityHandler {

    private static final String PACKAGE_DOMAIN = "com.zenya.damageindicator.nms.";
    private static final String CLASS_NAME = ".ProtocolNMSImpl";

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
        1.17 - v1_17_R0 (due to breaking changes)
        1.17.1 - v_17_R1
        1.18 - v_18_R1
        */
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1);
        if (version.contains("v1_17") && !Bukkit.getServer().getVersion().matches("(.*)1\\.17\\.\\d(.*)"))
            version = "v1_17_R0";
        return version;
    }

    public static int getProtocol() {
        return Integer.parseInt(getVersion().split("_")[1]);
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends ProtocolNMS> getProtocolNMS() throws ClassNotFoundException {
        try {
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + getVersion() + CLASS_NAME);
        } catch (Exception exc) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "You are running DamageIndicator on an unsupported NMS version " + getVersion());
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.DARK_RED + "Some features may be disabled or broken");
            return (Class<? extends ProtocolNMS>) Class.forName(PACKAGE_DOMAIN + "fallback" + CLASS_NAME);
        }
    }

}

