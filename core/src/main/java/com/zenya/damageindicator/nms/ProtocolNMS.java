package com.zenya.damageindicator.nms;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface ProtocolNMS {

    Hologram getHologram(List<Player> players, LivingEntity ent, String text);

    class HologramImpl {
    }

}
