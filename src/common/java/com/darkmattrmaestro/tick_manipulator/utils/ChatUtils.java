package com.darkmattrmaestro.tick_manipulator.utils;

import com.darkmattrmaestro.tick_manipulator.Constants;

public class ChatUtils {
    public static void sendMsg(String msg) {
        if (Constants.relevantChatSender == null) {
            Constants.LOGGER.error("relevantChatSender not set!");
            return;
        }
        Constants.relevantChatSender.accept("[Tick Manipulator] " + msg);
    }
}
