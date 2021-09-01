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
import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;

public class HologramSpawnEvent extends Event {
    private final LivingEntity ent;
    private final double amount;

    public HologramSpawnEvent(LivingEntity ent, double amount) {
        this.ent = ent;
        this.amount = amount;
        fireEvent();
    }

    public void fireEvent() {
        double offset = StorageFileManager.getConfig().getDouble("hologram-offset");
        double speed = StorageFileManager.getConfig().getDouble("hologram-speed");
        int duration = StorageFileManager.getConfig().getInt("hologram-duration");
        String format = amount > 0
                ? StorageFileManager.getConfig().getNearestValue("heal-format", Math.abs(amount), RoundingMode.DOWN)
                : StorageFileManager.getConfig().getNearestValue("damage-format", Math.abs(amount), RoundingMode.DOWN);

        String hologramText = (new DisplayBuilder()).withText(format).withValue(Math.abs(amount)).build();
        int serverViewDist = Bukkit.getViewDistance() << 5;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld() != ent.getWorld())
                return;
            if (player.getLocation().distanceSquared(ent.getLocation()) > Math.min(player.getClientViewDistance() << 4, serverViewDist))
                return;
            if (!ToggleManager.INSTANCE.isToggled(player.getName()))
                return;
            DamageIndicator.PROTOCOL_NMS.getHologram(player, ent, hologramText).spawn(offset, speed, duration);
        }
    }

    //Default custom event methods
    private static final HandlerList handlers = new HandlerList();

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}

