package com.darkmattrmaestro.tick_manipulator;

import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PerWorldSingletons {
    /**
     * Clear all stored values. Called on world change.
     */
    public static void clear() {
        repeatCalls.clear();
    }

    public static ArrayList<Consumer<Void>> repeatCalls = new ArrayList<Consumer<Void>>();

    private static float customTickRate = 20.0F;
    private static float customUpdateTimestep = 0.05F;

    public static void setTickRate(float tickRate) {
        customTickRate = tickRate;
        customUpdateTimestep = 1 / tickRate;
    }

    public static float getCustomTickRate() {
        return customTickRate;
    }

    public static float getCustomUpdateTimestep() {
        return customUpdateTimestep;
    }
}
