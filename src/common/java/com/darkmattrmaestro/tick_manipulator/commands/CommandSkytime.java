package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import com.darkmattrmaestro.tick_manipulator.packets.SkyPacket;
import finalforeach.cosmicreach.accounts.Account;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;

public class CommandSkytime extends Command {
    String[] validActions = new String[]{"set", "freeze", "unfreeze"};

    public void run(IChat chat) {
        super.run(chat);
        if (!this.hasNextArg()) {
            this.commandError("Missing action. Can be any of:\n\tset,\n\tfreeze,\n\tunfreeze");
        }

        String action = this.getNextArg();

        Player player = this.getCallingPlayer();
        Zone zone = null;
        if (player != null) {
            zone = player.getZone();
        } else {
            if (!this.hasNextArg()) {
                this.commandError("Missing zone id");
            }

            zone = this.parseZone(this.getNextArg());
            if (zone == null) {
                this.commandError("Invalid zone id. Can be any of: " + String.join(",\n\t", this.world.getZoneIds()));
                return;
            }
        }

        switch (action.toLowerCase()) {
            case "set" -> {
                if (!this.hasNextArg()) {
                    this.commandError("No time was given. Can be any number.");
                    return;
                }

                float parsedTick;
                try {
                    parsedTick = Float.parseFloat(this.getNextArg());
                } catch (Exception _) {
                    this.commandError("The time must be a valid tick. Can be any number.");
                    return;
                }
                ((IMixinZone) zone).setFrozenSkyTime(parsedTick * 0.05F);
                break;
            }
            case "freeze" -> {
                if (!((IMixinZone) zone).getIsSkyFrozen()) {
                    ((IMixinZone) zone).setFrozenSkyTime((float)GameSingletons.world.getCurrentWorldTick() * 0.05F);
                }
                ((IMixinZone) zone).setIsSkyFrozen(true);
                chat.addMessage((Account)null, "[Tick Manipulator] Froze the skytime for zone " + zone.zoneId);
                break;
            }
            case "unfreeze" -> {
                ((IMixinZone) zone).setIsSkyFrozen(false);
                chat.addMessage((Account)null, "[Tick Manipulator] Unfroze the skytime for zone " + zone.zoneId);
                break;
            }
            case "reset" -> {
                ((IMixinZone) zone).setIsSkyFrozen(false);
                chat.addMessage((Account)null, "[Tick Manipulator] Reset the skytime to the default for zone " + zone.zoneId);
                break;
            }
            default -> {
                this.commandError("Invalid action. Can be any of: " + String.join(",\n\t", validActions));
            }
        }

        if (GameSingletons.isHost() && ServerSingletons.SERVER != null) {
            SkyPacket skyPacket = new SkyPacket(
                    ((IMixinZone) zone).getFrozenSkyTime(),
                    ((IMixinZone) zone).getIsSkyFrozen(),
                    zone
            );

            ServerSingletons.SERVER.broadcastToAll(skyPacket);
        }
    }

    public String getShortDescription() {
        return "Sets the sky time.";
    }
}

