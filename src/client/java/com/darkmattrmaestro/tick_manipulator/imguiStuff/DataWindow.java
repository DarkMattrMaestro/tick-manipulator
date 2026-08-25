package com.darkmattrmaestro.tick_manipulator.imguiStuff;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.Highlight.Highlight;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import com.darkmattrmaestro.tick_manipulator.utils.EntitySelectionUtil;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.entities.GameEntity;
import finalforeach.cosmicreach.gamestates.ChatMenu;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.gamestates.PauseMenu;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImBoolean;
import org.tympanic.imgui_integration.imgui.ImGuiManager;
import org.tympanic.imgui_integration.imgui.ImGuiWindow;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;

public class DataWindow extends ImGuiWindow {
    private final ImBoolean SHOW = new ImBoolean(true);

    private final ImBoolean SHOW_ERROR = new ImBoolean(false);

    private BlockPosition selectedBlockPos = null;
    private GameEntity selectedEntity = null;

    @Override
    public void init() {
        this.renderIn(ChatMenu.class);
        this.renderIn(InGame.class);
        this.renderIn(PauseMenu.class);
    }


    /**
     * Recursively renders a tree of all properties in the given object.
     *
     * @param obj The object to search.
     * @param targetPropertyNames Names of properties to selectively show. If null, shows all properties. If not null,
     *                            shows only properties listed in `targetPropertyNames`.
     * @param depth The maximal depth to search.
     * @param <T> The object type to search.
     */
    public <T> void recursiveObjectTree(T obj, HashSet<String> targetPropertyNames, int depth) {
        Class clazz = obj.getClass();
        while (clazz != Object.class) {

            for (Field field : clazz.getDeclaredFields()) {
                if (targetPropertyNames != null && !targetPropertyNames.contains(field.getName().toLowerCase())) { continue; }

                Object resObj = null;
                String res = null;
                try {
                    field.setAccessible(true);
                    resObj = field.get(obj);
                    res = String.valueOf(field.get(obj));
                } catch (Exception _) {
                }

                if (res == null) {
                    ImGui.text(field.getName() + " : Field inaccessible.");
                } else if (depth > 0 && res.contains("@")) {
                    if (ImGui.treeNodeEx(field.getName())) {
                        recursiveObjectTree((field.getType().cast(resObj)), null, depth - 1);
                        ImGui.treePop();
                    }
                } else {
                    ImGui.text(field.getName() + " : " + res);
                }
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (targetPropertyNames != null && !targetPropertyNames.contains(method.getName().toLowerCase())) { continue; }
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }

                Object resObj = null;
                String res = null;
                try {
                    method.setAccessible(true);
                    resObj = method.invoke(obj);
                    res = String.valueOf(resObj);
                } catch (Exception _) { }

                if (res == null) {
                    ImGui.text(method.getName() + "() -> Method inaccessible.");
                } else if (depth > 0 && res.contains("@")) {
                    if (ImGui.treeNodeEx(method.getName() + "()")) {
                        recursiveObjectTree((method.getReturnType().cast(resObj)), null, depth - 1);
                        ImGui.treePop();
                    }
                } else {
                    ImGui.text(method.getName() + "() -> " + res);
                }
            }

            clazz = clazz.getSuperclass();
        }
    }

    private void renderBlocksTarget() {
        // Update selected block position
        if (ImGui.button("Select block at cursor")) {
            this.selectedBlockPos = BlockSelectionUtil.getBlockLookingAtFar(100);
            if (this.selectedBlockPos == null) {
                SHOW_ERROR.set(true);
                ImGui.openPopup("Error");
            }
        }

        // Block selection error modal
        if (ImGui.beginPopupModal("Error", SHOW_ERROR)) {
            ImGui.text("Error: No block is withing range!");

            ImGui.endPopup();
        }

        // Show tree of the block's data
        if (this.selectedBlockPos == null) {
            ImGui.text("No block selected.");
        } else {
            ImGui.beginChild("Scrolling");

            if (ImGui.treeNodeEx("Data of block at " + this.selectedBlockPos + " :", ImGuiTreeNodeFlags.DefaultOpen)) {
                recursiveObjectTree(this.selectedBlockPos, null, 3);
                ImGui.treePop();
            }

            ImGui.endChild();
        }
    }

    private void renderEntityTarget() {
        // Update selected block position
        if (ImGui.button("Select nearest entity")) {
            this.selectedEntity = EntitySelectionUtil.getNearestEntityToPlayer();
            if (this.selectedEntity == null) {
                SHOW_ERROR.set(true);
                ImGui.openPopup("Error");
            }
        }

        // Block selection error modal
        if (ImGui.beginPopupModal("Error", SHOW_ERROR)) {
            ImGui.text("Error: No entity could be selected!");
            ImGui.text("If an entity was previously selected, it may have despawned.");

            ImGui.endPopup();
        }

        // Show tree of the block's data
        if (this.selectedEntity == null) {
            ImGui.text("No entity selected.");
        } else {
            ImGui.beginChild("Scrolling");

            if (ImGui.treeNodeEx("Data of entity " + this.selectedEntity.entityTypeId + " at " + this.selectedEntity.position + " :", ImGuiTreeNodeFlags.DefaultOpen)) {
                recursiveObjectTree(this.selectedEntity, null, 3);
                ImGui.treePop();
            }

            ImGui.endChild();
        }
    }

    @Override
    public void render() {
        if (SHOW.get()) {
            if (ImGui.begin("Target Data", SHOW)) {
                if (ImGui.beginTabBar("Target")) {
                    if (ImGui.beginTabItem("Block")) {
                        renderBlocksTarget();
                        ImGui.endTabItem();
                    }

                    if (ImGui.beginTabItem("Entity")) {
                        renderEntityTarget();
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