package com.darkmattrmaestro.tick_manipulator;

import com.darkmattrmaestro.tick_manipulator.Highlight.BlockHighlight;
import com.darkmattrmaestro.tick_manipulator.utils.Vector3Int;

import java.util.ArrayList;

public class PerWorldClientSingletons {
    /**
     * Clear all stored values. Called on world change.
     */
    public static void clear() {
        blockHighlight.clear();
    }

    public static BlockHighlight blockHighlight = new BlockHighlight();
}
