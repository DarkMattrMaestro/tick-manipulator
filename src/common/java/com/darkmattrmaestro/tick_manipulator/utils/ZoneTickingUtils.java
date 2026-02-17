package com.darkmattrmaestro.tick_manipulator.utils;

import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;

public class ZoneTickingUtils {
    public static void resetTicking() {
        stepTicks(0);
        setFreezeState(false);
        setDelay(0);
    }

    public static void stepTicks(int numTicks) {
        for (Zone zone : GameSingletons.world.getZones()) {
            ((IMixinZone) (Object) zone).setAdvanceTicks(numTicks);
        }
    }

    public static void setFreezeState(boolean state) {
        for (Zone zone : GameSingletons.world.getZones()) {
            ((IMixinZone) (Object) zone).setFrozen(state);
        }
    }

    public static void setDelay(int delay) {
        for (Zone zone : GameSingletons.world.getZones()) {
            ((IMixinZone) (Object) zone).setTickDelay(delay);
        }
    }
}
