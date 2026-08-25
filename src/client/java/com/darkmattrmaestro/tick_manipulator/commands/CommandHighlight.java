package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.imguiStuff.HighlightWindow;
import com.darkmattrmaestro.tick_manipulator.utils.OptionalMods;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;
import org.tympanic.imgui_integration.imgui.ImGuiManager;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandHighlight extends Command {

    public void run(IChat chat) {
        super.run(chat);

        if (!OptionalMods.hasImgui()) {
            sendMsg("The Dear ImGui Integration Mod is required for this command! The mod can be found at https://crmods.org/mod/imgui-integration");
            return;
        }

        ImGuiManager.INSTANCE.windows.add(new HighlightWindow());
    }

    public String getShortDescription() {
        return "Highlight various things by rendering them on top.";
    }
}
