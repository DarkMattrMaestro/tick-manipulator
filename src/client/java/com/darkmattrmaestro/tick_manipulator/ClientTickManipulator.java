package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.commands.CommandHighlight;
import com.darkmattrmaestro.tick_manipulator.commands.CommandTargetData;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.client.ClientModInit;

import static finalforeach.cosmicreach.chat.commands.Command.registerCommand;

@SuppressWarnings("unused")
public class ClientTickManipulator implements ClientModInit {

    @Override
    public void onClientInit() {
        Constants.LOGGER.info("Initialized Tick Manipulator Client");
        registerCommand(CommandHighlight::new, "highlight", "hl");
        registerCommand(CommandTargetData::new, "target-data", "data");
    }

}