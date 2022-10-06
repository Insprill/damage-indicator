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

package com.zenya.damageindicator.util;

import com.zenya.damageindicator.DamageIndicator;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public final class Logger {
    public static void logInfo(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public static void logError(String message, Object... args) {
        log(Level.SEVERE, message, args);
    }

    private static void log(Level level, String message, Object... args) {
        if (args != null && args.length > 0) {
            message = String.format(message, args);
        }

        if (DamageIndicator.INSTANCE.getLogger().isLoggable(level)) {
            Bukkit.getConsoleSender().sendMessage(String.format(
                    "§7[§6%s§7] %s",
                    "DamageIndicator",
                    adjustResetFormat("§r" + message, level == Level.SEVERE ? "§c" : "§f")
            ));
        }
    }

    private static String adjustResetFormat(String message, String append) {
        return message.replaceAll("§r", "§r" + append);
    }
}
