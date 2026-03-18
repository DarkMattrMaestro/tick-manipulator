package com.darkmattrmaestro.tick_manipulator;

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
}
