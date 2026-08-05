//package com.darkmattrmaestro.tick_manipulator.imguiStuff;
//
//import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
//import finalforeach.cosmicreach.BlockGame;
//import imgui.ImGui;
//import imgui.ImGuiIO;
//import imgui.app.Configuration;
//import imgui.app.Window;
//import imgui.flag.ImGuiConfigFlags;
//import imgui.flag.ImGuiDockNodeFlags;
//import imgui.flag.ImGuiWindowFlags;
//import imgui.gl3.ImGuiImplGl3;
//import imgui.glfw.ImGuiImplGlfw;
//import org.lwjgl.glfw.GLFW;
//import org.spongepowered.asm.mixin.Shadow;
//
//public class ImguiWindow {
//    protected ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
//    protected ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
//
//    protected long handle;
//
//    private String glslVersion = null;
//
//    private void decideGlGlslVersions() {
//        final boolean isMac = System.getProperty("os.name").toLowerCase().contains("mac");
//        if (isMac) {
//            glslVersion = "#version 150";
//            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
//            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
//            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);  // 3.2+ only
//            GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);          // Required on Mac
//        } else {
//            glslVersion = "#version 130";
//            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
//            GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 0);
//        }
//    }
//
//    protected void init() {//(final Configuration config) {
//        decideGlGlslVersions();
//        handle = ((Lwjgl3Graphics) BlockGame.lwjglApp.getGraphics()).getWindow().getWindowHandle();
//        ImGui.createContext();
//        final ImGuiIO io = ImGui.getIO();
//        io.setIniFilename(null);
//        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
//        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
//        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
////        io.setConfigViewportsNoTaskBarIcon(true);
//        io.setConfigWindowsResizeFromEdges(false);
//        io.addConfigFlags(ImGuiWindowFlags.NoBackground);
//        io.addConfigFlags(ImGuiDockNodeFlags.PassthruCentralNode);
//        imGuiGlfw.init(handle, true);
//        imGuiGl3.init(glslVersion);
//    }
//
//    public void tick() {
////        for (ImGuiWindow window : windows) {
////            if (!window.hasBeenInitialized) {
////                window.init();
////                window.hasBeenInitialized = true;
////            }
////            if (window.rendersIn(GameState.currentGameState.getClass()) || window.rendersIn(GameState.class)) {
////                window.tick();
////            }
////        }
//    }
//
//    protected void render() {
//        startFrame();
//        process();
//        endFrame();
//    }
//
//    protected void startFrame() {
//        imGuiGl3.newFrame();
//        imGuiGlfw.newFrame();
//        ImGui.newFrame();
//    }
//
//    public void process() {
//
//    }
//
//    protected void endFrame() {
//        ImGui.render();
//        imGuiGl3.renderDrawData(ImGui.getDrawData());
//
//        // Update and Render additional Platform Windows
//        // (Platform functions may change the current OpenGL context, so we save/restore it to make it easier to paste this code elsewhere.
//        //  For this specific demo app we could also call glfwMakeContextCurrent(window) directly)
//        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
//            final long backupCurrentContext = GLFW.glfwGetCurrentContext();
//            ImGui.updatePlatformWindows();
//            ImGui.renderPlatformWindowsDefault();
//            GLFW.glfwMakeContextCurrent(backupCurrentContext);
//        }
//
////        renderBuffer();
//    }
//
////    private void renderBuffer() {
////        GLFW.glfwSwapBuffers(handle);
////        GLFW.glfwPollEvents();
////    }
//
//    public void dispose() {
//        imGuiGl3.shutdown();
//        imGuiGl3 = null;
//        imGuiGlfw.shutdown();
//        imGuiGlfw = null;
//        ImGui.destroyContext();
//
////        for (ImGuiWindow window : windows) {
////            if (window.hasBeenInitialized) {
////                window.dispose();
////            }
////        }
//    }
//}
