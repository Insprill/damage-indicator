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
