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
import java.util.List;
import java.util.stream.Collectors;

public class HologramSpawnEvent extends Event {

    private final LivingEntity ent;
    private final double amount;

    public HologramSpawnEvent(LivingEntity ent, double amount) {
        this.ent = ent;
        this.amount = amount;
        fireEvent();
    }

    public void fireEvent() {
        int serverViewDist = Bukkit.getViewDistance() << 5;
        List<Player> players = ent.getWorld().getPlayers().stream()
                .filter(p -> p != ent)
                .filter(p -> p.getLocation().distanceSquared(ent.getLocation()) <= serverViewDist)
                .filter(p -> ToggleManager.INSTANCE.isToggled(p.getUniqueId()))
                .collect(Collectors.toList());
        if (players.isEmpty())
            return;

        double offset = StorageFileManager.getConfig().getDouble("hologram-offset");
        double speed = StorageFileManager.getConfig().getDouble("hologram-speed");
        int duration = StorageFileManager.getConfig().getInt("hologram-duration");

        String format = StorageFileManager.getConfig().getNearestValue(amount > 0 ? "heal-format" : "damage-format", Math.abs(amount), RoundingMode.DOWN);
        String hologramText = new DisplayBuilder().withText(format).withValue(Math.abs(amount)).build();

        DamageIndicator.PROTOCOL_NMS.getHologram(players, ent, hologramText).spawn(offset, speed, duration);
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

