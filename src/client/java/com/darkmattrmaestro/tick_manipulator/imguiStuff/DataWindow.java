package com.darkmattrmaestro.tick_manipulator.imguiStuff;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.Highlight.Highlight;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import finalforeach.cosmicreach.blocks.BlockPosition;
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
import java.util.Objects;

public class DataWindow extends ImGuiWindow {
    private final ImBoolean SHOW = new ImBoolean(true);

    boolean simpleViewBlock = false;

    @Override
    public void init() {
        this.renderIn(ChatMenu.class);
        this.renderIn(InGame.class);
        this.renderIn(PauseMenu.class);
    }



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
                } catch (IllegalAccessException _) {
                }
                if (res == null) {
                    ImGui.text("Field inaccessible.");
                } else if (depth > 0 && res.contains("@")) {
                    ImGui.treeNodeEx(field.getName());
                    recursiveObjectTree((field.getClass().cast(resObj)), null, depth - 1);
                    ImGui.treePop();
                } else {
                    ImGui.text(res);
                }
            }
//            for (Method method : clazz.getDeclaredMethods()) {
//                if (targetPropertyNames != null && !targetPropertyNames.contains(method.getName().toLowerCase())) { continue; }
//                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }
//
//                String res = null;
//                try {
//                    method.setAccessible(true);
//                    res = String.valueOf(method.invoke(obj));
//                } catch (IllegalAccessException | InvocationTargetException _) { }
//                appendObjProp(msg, res, method.getName(), "->", subIndentLevel, "method");
//            }

            clazz = clazz.getSuperclass();
        }
    }

    private void renderBlocksTarget() {
        if (ImGui.checkbox("Simple View", simpleViewBlock)) {
            simpleViewBlock = !simpleViewBlock;
        }

        if (ImGui.button("Update")) {
            BlockPosition blockPos = BlockSelectionUtil.getBlockLookingAtFar(100);

            if (blockPos == null) {
                ImGui.text("No block found!");
            } else {
                ImGui.beginChild("Scrolling");
                ImGui.treeNodeEx("Data of " + blockPos + " :", ImGuiTreeNodeFlags.DefaultOpen);

                recursiveObjectTree(blockPos, null, 3);

                ImGui.treePop();
                ImGui.endChild();
            }
        }
    }

    private void renderEntityTarget() {
//        if (ImGui.checkbox("Simple View", simpleViewBlock)) {
//            simpleViewBlock = !simpleViewBlock;
//        }
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