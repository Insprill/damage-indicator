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

package com.zenya.damageindicator.nms;

import com.zenya.damageindicator.DamageIndicator;
import net.insprill.spigotutils.MinecraftVersion;

public class CompatibilityHandler {

    private static final String FULLY_QUALIFIED_PATH = "com.zenya.damageindicator.nms.%s.ProtocolNMSImpl";

    @SuppressWarnings("unchecked")
    public static Class<? extends ProtocolNMS> getProtocolNMS() throws ClassNotFoundException {
        Class<?> clazz;
        try {
            clazz = getNMSClass();
        } catch (Exception exc) {
            DamageIndicator.INSTANCE.getLogger().warning("You are running DamageIndicator on an unsupported version (" + MinecraftVersion.getCurrentVersion().getDisplayName() + ")");
            DamageIndicator.INSTANCE.getLogger().warning("Some features may be disabled or broken, and expect degraded performance.");
            clazz = Class.forName(String.format(FULLY_QUALIFIED_PATH, "fallback"));
        }
        return (Class<? extends ProtocolNMS>) clazz;
    }

    static Class<?> getNMSClass() throws ClassNotFoundException {
        @SuppressWarnings("deprecation") // Need to do it this way on older versions \:
        // 1.17.1 has breaking changes from 1.17, but is the same CraftBukkit version :/
        String version = MinecraftVersion.isOlderThan(MinecraftVersion.v1_20_5)
                ? MinecraftVersion.is(MinecraftVersion.v1_17_0)
                ? "v1_17_R0"
                : MinecraftVersion.getCraftBukkitVersion()
                : "v" + MinecraftVersion.getCurrentVersion().getDisplayName(true).replace(".", "_");
        return Class.forName(String.format(FULLY_QUALIFIED_PATH, version));
    }

}

