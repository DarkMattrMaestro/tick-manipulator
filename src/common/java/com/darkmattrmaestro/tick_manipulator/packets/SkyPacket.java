package com.darkmattrmaestro.tick_manipulator.packets;

import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.networking.GamePacket;
import finalforeach.cosmicreach.networking.NetworkIdentity;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class SkyPacket extends GamePacket {
    public boolean requesting = false;
    public boolean frozen;
    public Zone zone;
    public float time;

    public static SkyPacket generateRequestPacket(Zone zone) {
        return new SkyPacket(zone, true);
    }

    public SkyPacket() {

    }

    private SkyPacket(Zone zone, boolean requesting) {
        this.requesting = true;
        this.zone = zone;
    }

    public SkyPacket(Zone zone) {
        this.frozen = ((IMixinZone) zone).tickManipulator$getIsSkyFrozen();
        this.zone = zone;
        this.time = ((IMixinZone) zone).tickManipulator$getFrozenSkyTime();
    }

    public SkyPacket(float time, boolean frozen, Zone zone) {
        this.frozen = frozen;
        this.zone = zone;
        this.time = time;
    }

    public void receive(ByteBuf in) {
        // Check if requesting sky time data
        this.requesting = this.readBoolean(in);
        this.zone = GameSingletons.world.getZoneIfExists(this.readString(in));
        if (this.requesting) {
            return;
        }

        this.frozen = this.readBoolean(in);
        this.time = this.readFloat(in);
    }

    public void write() {
        this.writeBoolean(this.requesting);
        this.writeString(this.zone.zoneId);
        if (this.requesting) {
            return;
        }

        this.writeBoolean(this.frozen);
        if (this.frozen) {
            this.writeFloat(this.time);
        } else {
            this.writeFloat((float) GameSingletons.world.getCurrentWorldTick() * 0.05F);
        }
    }

    public void handle(NetworkIdentity identity, ChannelHandlerContext ctx) {
        if (!identity.isServer()) {
            ((IMixinZone) this.zone).tickManipulator$setIsSkyFrozen(this.frozen);
            ((IMixinZone) this.zone).tickManipulator$setFrozenSkyTime(this.time);
        } else if (this.requesting) {
            if (GameSingletons.isHost() && ServerSingletons.SERVER != null) {
                SkyPacket skyPacket = new SkyPacket(
                        ((IMixinZone) zone).tickManipulator$getFrozenSkyTime(),
                        ((IMixinZone) zone).tickManipulator$getIsSkyFrozen(),
                        zone
                );

                skyPacket.setupAndSend(ctx);
            }
        }
    }
}
