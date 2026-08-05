package com.darkmattrmaestro.tick_manipulator.imguiStuff;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.gamestates.PauseMenu;
import imgui.ImGui;
import imgui.type.ImBoolean;
import org.tympanic.imgui_integration.imgui.ImGuiManager;
import org.tympanic.imgui_integration.imgui.ImGuiWindow;

public class LaunchpadWindow extends ImGuiWindow {
    private final ImBoolean SHOW = new ImBoolean(true);

    @Override
    public void init() {
        this.renderIn(ChatMenu.class);
        this.renderIn(InGame.class);
        this.renderIn(PauseMenu.class);
    }

    @Override
    public void render() {
        if (SHOW.get()) {
            if (ImGui.begin("Tick Manipulator", SHOW)) {
                ImGui.text("Main menu stuff...");
            }
            ImGui.end();
        } else {
            ImGuiManager.INSTANCE.closeWindow(this);
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void dispose() {
        Constants.LOGGER.info("dispose window");
    }
}