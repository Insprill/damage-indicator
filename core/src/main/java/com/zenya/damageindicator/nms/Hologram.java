package com.zenya.damageindicator.nms;

public interface Hologram {

    Hologram spawn(double offset, double speed, long duration);

    void sendCreatePacket();

    void sendMetaPacket();

    void sendTeleportPacket();

    void sendRemovePacket();

    void sendPacket(Object packet);

}
