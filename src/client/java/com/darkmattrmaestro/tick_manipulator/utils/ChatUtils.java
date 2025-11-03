package com.darkmattrmaestro.tick_manipulator.utils;

import finalforeach.cosmicreach.chat.Chat;

public class ChatUtils {
    public static void sendMsg(String msg) {
        Chat.MAIN_CLIENT_CHAT.addMessage(null, "[Tick Manipulator] " + msg);
    }
}
