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

package com.zenya.damageindicator.storage;

import com.zenya.damageindicator.DamageIndicator;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {

    public static final ToggleManager INSTANCE = new ToggleManager();

    private final ConcurrentMap<UUID, Boolean> toggleMap = new ConcurrentHashMap<>();

    public Boolean isToggled(UUID uuid) {
        if (!toggleMap.containsKey(uuid)) {
            Bukkit.getScheduler().runTaskAsynchronously(DamageIndicator.INSTANCE, () -> {
                boolean status = StorageFileManager.getDatabase().getToggleStatus(uuid);
                cacheToggle(uuid, status);
            });
        }
        return toggleMap.getOrDefault(uuid, false);
    }

    public void registerToggle(UUID uuid, boolean status) {
        cacheToggle(uuid, status);
        Bukkit.getScheduler().runTaskAsynchronously(DamageIndicator.INSTANCE, () -> {
            StorageFileManager.getDatabase().setToggleStatus(uuid, status);
        });
    }

    public void cacheToggle(UUID uuid, boolean enabled) {
        toggleMap.put(uuid, enabled);
    }

    public void uncacheToggle(UUID uuid) {
        toggleMap.remove(uuid);
    }

}
