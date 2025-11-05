package com.darkmattrmaestro.tick_manipulator.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.commands.CommandTick;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.networking.packets.CommandPacket;

public class TickManipulatorInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown (int keycode) {
        String[] command = null;
        switch (keycode) {
            case Input.Keys.PAGE_UP -> {
                Constants.LOGGER.info("PAGE_UP pressed");
                command = new String[]{"tick", "step"};
            }
        }

        if (command != null) {
            CommandTick cmdTick = new CommandTick();
            if (ClientNetworkManager.isConnected()) {
                CommandPacket packet = new CommandPacket(command);
                ClientNetworkManager.sendAsClient(packet);
            } else {
                cmdTick.setup(null, command);
                cmdTick.run(Chat.MAIN_CLIENT_CHAT);
            }
        }

        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int x, int y, int pointer, int button) { return false; }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int x, int y) {
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return false;
    }
}
