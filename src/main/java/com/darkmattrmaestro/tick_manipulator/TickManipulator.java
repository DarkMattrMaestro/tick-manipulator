package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.commands.CommandTick;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.common.ModInit;


import static finalforeach.cosmicreach.chat.commands.Command.registerCommand;

@SuppressWarnings("unused")
public class TickManipulator implements ModInit {

    @Override
    public void onInit() {
        Constants.LOGGER.info("Initialized Tick Manipulator");
        registerCommand(CommandTick::new, "tick", "ti");
    }

}