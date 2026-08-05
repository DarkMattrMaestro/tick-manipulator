package com.darkmattrmaestro.tick_manipulator.imguiStuff;

import com.darkmattrmaestro.tick_manipulator.Constants;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.gamestates.PauseMenu;

import imgui.ImGui;
import org.tympanic.imgui_integration.imgui.ImGuiWindow;

public class TestWindow extends ImGuiWindow {
    int[] test = new int[1];
    @Override
    public void init() {
        Constants.LOGGER.info("init window");
        this.renderIn(ChatMenu.class);
        this.renderIn(InGame.class);
        this.renderIn(PauseMenu.class);
    }
    @Override
    public void render() {
        ImGui.begin("testing");
        ImGui.text("test text");
        ImGui.sliderInt("test int slider", test, 0, 100);
        ImGui.end();
    }

    @Override
    public void tick() {

    }

    @Override
    public void dispose() {
        Constants.LOGGER.info("dispose window");
    }
}