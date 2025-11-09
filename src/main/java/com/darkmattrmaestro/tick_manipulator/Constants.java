package com.darkmattrmaestro.tick_manipulator;

import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.networking.packets.MessagePacket;
import finalforeach.cosmicreach.networking.server.ServerSingletons;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;

public class Constants {
    public static final String MOD_ID = "tick-manipulator";
    public static final Identifier MOD_NAME = Identifier.of(MOD_ID, "TickManipulator");
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static Consumer<String> relevantChatSender = GameSingletons.isClient ?
            (String msg) -> Chat.MAIN_CLIENT_CHAT.addMessage(null, msg) :
            (String msg) -> ServerSingletons.SERVER.broadcastToAll(new MessagePacket(msg));

    public static final HashSet<String> POSITIVES = new HashSet<String>(Arrays.asList("true", "yes", "on"));
    public static final HashSet<String> NEGATIVES = new HashSet<String>(Arrays.asList("false", "no", "off"));
}