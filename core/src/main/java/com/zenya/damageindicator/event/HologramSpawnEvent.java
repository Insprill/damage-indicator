package com.zenya.damageindicator.event;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import com.zenya.damageindicator.util.DisplayBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.math.RoundingMode;

public class HologramSpawnEvent extends Event {
    private LivingEntity ent;
    private double amount;

    public HologramSpawnEvent(LivingEntity ent, double amount) {
        this.ent = ent;
        this.amount = amount;
        fireEvent();
    }

    public void fireEvent() {
        double offset = StorageFileManager.getConfig().getDouble("hologram-offset");
        double speed = StorageFileManager.getConfig().getDouble("hologram-speed");
        int duration = StorageFileManager.getConfig().getInt("hologram-duration");
        String format = amount > 0 ? StorageFileManager.getConfig().getNearestValue("heal-format", Math.abs(amount), RoundingMode.DOWN) : StorageFileManager.getConfig().getNearestValue("damage-format", Math.abs(amount), RoundingMode.DOWN);

        String hologram = (new DisplayBuilder()).withText(format).withValue(Math.abs(amount)).build();
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.getWorld().equals(ent.getWorld())
                    && ToggleManager.INSTANCE.isToggled(player.getName())) {
                DamageIndicator.PROTOCOL_NMS.getHologram(player, ent, hologram).spawn(offset, speed, duration);
            }
        }
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

