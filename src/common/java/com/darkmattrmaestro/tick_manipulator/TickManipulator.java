package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.commands.CommandSkytime;
import com.darkmattrmaestro.tick_manipulator.commands.CommandTick;
import com.darkmattrmaestro.tick_manipulator.packets.SkyPacket;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;
import finalforeach.cosmicreach.networking.GamePacket;


import static finalforeach.cosmicreach.chat.commands.Command.registerCommand;

@SuppressWarnings("unused")
public class TickManipulator implements ModInit {

    @Override
    public void onInit() {
        Constants.LOGGER.info("Initialized Tick Manipulator");
        registerCommand(CommandTick::new, "tick", "ti");
        registerCommand(CommandSkytime::new, "skytime");

        GamePacket.registerPacket(SkyPacket.class);
    }

}