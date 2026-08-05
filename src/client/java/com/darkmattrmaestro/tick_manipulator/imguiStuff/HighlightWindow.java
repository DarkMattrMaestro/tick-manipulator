package com.darkmattrmaestro.tick_manipulator.imguiStuff;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.Highlight.Highlight;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.gamestates.PauseMenu;
import imgui.ImGui;
import imgui.type.ImBoolean;
import org.tympanic.imgui_integration.imgui.ImGuiManager;
import org.tympanic.imgui_integration.imgui.ImGuiWindow;

public class HighlightWindow extends ImGuiWindow {
    private final ImBoolean SHOW_HIGHLIGHT = new ImBoolean(true);

    @Override
    public void init() {
        Constants.LOGGER.info("init window");
        this.renderIn(ChatMenu.class);
        this.renderIn(InGame.class);
        this.renderIn(PauseMenu.class);
    }

    private void renderBlocksSection() {

    }

    @Override
    public void render() {
        if (SHOW_HIGHLIGHT.get()) {
            if (ImGui.begin("Tick Manipulator", SHOW_HIGHLIGHT)) {
                if (ImGui.beginTabBar("Commands")) {
                    if (ImGui.beginTabItem("Highlight")) {
                        if (ImGui.beginChild("Particles")) {
                            if (ImGui.checkbox("Highlight particles", Highlight.highlightParticles)) {
                                Highlight.highlightParticles = !Highlight.highlightParticles;
                            }
                            if (ImGui.checkbox("Persist particles", Highlight.persistParticles)) {
                                Highlight.persistParticles = !Highlight.persistParticles;
                            }
                            ImGui.endChild();
                        }

                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("Entities")) {
                        if (ImGui.checkbox("Enable", Highlight.highlightEntities)) {
                            Highlight.highlightEntities = !Highlight.highlightEntities;
                        }
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("Blocks")) {
                        renderBlocksSection();
                        ImGui.endTabItem();
                    }
                    ImGui.endTabBar();
                }
                ImGui.end();
            } else {
                ImGui.end();
            }
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