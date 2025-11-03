package com.darkmattrmaestro.tick_manipulator.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;

public class TickManipulatorInputProcessor implements InputProcessor {
    @Override
    public boolean keyDown (int keycode) {
        switch (keycode) {
            case Input.Keys.PAGE_UP -> {
                for (Zone zone : GameSingletons.world.getZones()) {
                    ((IMixinZone)zone).setAdvanceTicks(1);
                }
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
