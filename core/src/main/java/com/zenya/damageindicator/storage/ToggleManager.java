package com.zenya.damageindicator.storage;

import com.zenya.damageindicator.DamageIndicator;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ToggleManager {
    public static final ToggleManager INSTANCE = new ToggleManager();
    private ConcurrentMap<String, Boolean> toggleMap = new ConcurrentHashMap<>();

    public Boolean isToggled(String playerName) {
        if(!toggleMap.containsKey(playerName)) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    boolean status = StorageFileManager.getDatabase().getToggleStatus(playerName);
                    cacheToggle(playerName, status);
                }
            }.runTask(DamageIndicator.INSTANCE);
        }
        return toggleMap.getOrDefault(playerName, false);
    }

    public void registerToggle(String playerName, boolean status) {
        cacheToggle(playerName, status);
        new BukkitRunnable() {
            @Override
            public void run() {
                StorageFileManager.getDatabase().setToggleStatus(playerName, status);
            }
        }.runTask(DamageIndicator.INSTANCE);
    }

    public void cacheToggle(String playerName, boolean enabled) {
        toggleMap.put(playerName, enabled);
    }

    public void uncacheToggle(String playerName) {
        toggleMap.remove(playerName);
    }
}