//package com.darkmattrmaestro.tick_manipulator.imguiStuff;
//
//import imgui.ImGui;
//import imgui.ImGuiIO;
//import imgui.app.Application;
//import imgui.app.Configuration;
//import imgui.flag.ImGuiConfigFlags;
//import imgui.flag.ImGuiDockNodeFlags;
//import imgui.flag.ImGuiWindowFlags;
////import imgui.app.Configuration;
//
//public class ImguiMain extends Application {
//    boolean isOn = false;
//    @Override
//    protected void configure(Configuration config) {
//        config.setTitle("Dear ImGui is Awesome!");
//    }
//
//    @Override
//    protected void initImGui(final Configuration config) {
//        super.initImGui(config);
//
//        final ImGuiIO io = ImGui.getIO();
//        io.setIniFilename(null);                                // We don't want to save .ini file
//        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);  // Enable Keyboard Controls
//        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);      // Enable Docking
//        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);    // Enable Multi-Viewport / Platform Windows
////        io.setConfigViewportsNoTaskBarIcon(true);
//        io.setConfigWindowsResizeFromEdges(false);
//        io.addConfigFlags(ImGuiWindowFlags.NoBackground);
//        io.addConfigFlags(ImGuiDockNodeFlags.PassthruCentralNode);
//
//
//    }
//
//    @Override
//    public void process() {
//        ImGui.text("Hello, World!");
//        if (ImGui.checkbox("Hello there", isOn)) { isOn = !isOn; }
//        ImGui.arrowButton("aaaa", 1);
//    }
//
//    public static void open() {
////        launch(new ImguiMain());
//    }
//}