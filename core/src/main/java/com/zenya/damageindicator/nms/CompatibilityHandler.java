package com.zenya.damageindicator.nms;

import com.zenya.damageindicator.DamageIndicator;
import net.insprill.xenlib.MinecraftVersion;

public class CompatibilityHandler {

    private static final String FULLY_QUALIFIED_PATH = "com.zenya.damageindicator.nms.%s.ProtocolNMSImpl";

    @SuppressWarnings("unchecked")
    public static Class<? extends ProtocolNMS> getProtocolNMS() throws ClassNotFoundException {
        try {
            // 1.17.1 has breaking changes from 1.17, but is the same CraftBukkit version :/
            String version = (MinecraftVersion.is(MinecraftVersion.v1_17_0)) ? "v1_17_R0" : MinecraftVersion.getCraftBukkitVersion();
            return (Class<? extends ProtocolNMS>) Class.forName(String.format(FULLY_QUALIFIED_PATH, version));
        } catch (Exception exc) {
            DamageIndicator.INSTANCE.getLogger().warning("You are running DamageIndicator on an unsupported NMS version " + MinecraftVersion.getCraftBukkitVersion());
            DamageIndicator.INSTANCE.getLogger().warning("Some features may be disabled or broken, and expect degraded performance.");
            return (Class<? extends ProtocolNMS>) Class.forName(String.format(FULLY_QUALIFIED_PATH, "fallback"));
        }
    }

}

