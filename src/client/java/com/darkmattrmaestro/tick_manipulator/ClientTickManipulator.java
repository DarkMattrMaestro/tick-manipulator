package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.commands.CommandHighlight;
import com.darkmattrmaestro.tick_manipulator.commands.CommandTargetData;
import com.darkmattrmaestro.tick_manipulator.imguiStuff.HighlightWindow;
import com.darkmattrmaestro.tick_manipulator.imguiStuff.LaunchpadWindow;
import com.darkmattrmaestro.tick_manipulator.imguiStuff.TestWindow;
import dev.puzzleshq.puzzleloader.loader.mod.entrypoint.client.ClientModInit;
import org.tympanic.imgui_integration.imgui.ImGuiManager;

import static finalforeach.cosmicreach.chat.commands.Command.registerCommand;

@SuppressWarnings("unused")
public class ClientTickManipulator implements ClientModInit {

    @Override
    public void onClientInit() {
        Constants.LOGGER.info("Initialized Tick Manipulator Client");
        registerCommand(CommandHighlight::new, "highlight", "hl");
        registerCommand(CommandTargetData::new, "target-data", "data");

        Constants.clientTickGUISpawner = () -> ImGuiManager.INSTANCE.windows.add(new LaunchpadWindow());
    }

}