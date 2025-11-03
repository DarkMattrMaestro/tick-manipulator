package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.commands.CommandTick;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.client.ClientModInit;

import static finalforeach.cosmicreach.chat.commands.Command.registerCommand;

@SuppressWarnings("unused")
public class TickManipulator implements ClientModInit {

    @Override
    public void onClientInit() {
        Constants.LOGGER.error("Initialized Tick Manipulator Client");
        registerCommand(CommandTick::new, "tick", "ti");
    }

}