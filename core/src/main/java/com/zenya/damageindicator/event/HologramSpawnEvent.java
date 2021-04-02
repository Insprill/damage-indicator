package com.zenya.damageindicator.event;

import com.zenya.damageindicator.DamageIndicator;
import com.zenya.damageindicator.file.ConfigManager;
import com.zenya.damageindicator.util.TextBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HologramSpawnEvent extends Event implements Cancellable {
    private boolean cancelled;
    private LivingEntity ent;
    private double amount;

    public HologramSpawnEvent(LivingEntity ent, double amount) {
        this.ent = ent;
        this.amount = amount;
        fireEvent();
    }

    public void fireEvent() {
        if(!cancelled) {
            double offset = ConfigManager.INSTANCE.getDouble("hologram-offset");
            double speed = ConfigManager.INSTANCE.getDouble("hologram-speed");
            int duration = ConfigManager.INSTANCE.getInt("hologram-duration");
            String format = amount < 0 ? ConfigManager.INSTANCE.getNearestValue("damage-format", Math.abs(amount), true) : ConfigManager.INSTANCE.getNearestValue("heal-format", Math.abs(amount), true);

            String hologram = (new TextBuilder()).withText(format).withValue(amount).build();
            for(Player player : Bukkit.getOnlinePlayers()) {
                DamageIndicator.PROTOCOL_NMS.getHologram(player, ent, hologram).spawn(offset, speed, duration);
            }
        }
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
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

