package com.zenya.damageindicator.nms;

import org.bukkit.Location;

public interface Hologram {

    Hologram spawn(double offset, double speed, long duration);

    void sendCreatePacket();

    void sendMetaPacket();

    void sendTeleportPacket(Location loc);

    void sendRemovePacket();

    void sendPacketToTracked(Object packet);

    void sendPacketToAllInWorld(Object packet);

}
