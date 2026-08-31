package com.darkmattrmaestro.tick_manipulator.interfaces;

import org.spongepowered.asm.mixin.Unique;

public interface IMixinZone {
    boolean getFrozen();
    void setFrozen(boolean state);

    int getAdvanceTicks();
    void setAdvanceTicks(int ticks);

    int getTickDelay();
    void setTickDelay(int delay);

    void setIsSkyFrozen(boolean isSkyFrozen);
    boolean getIsSkyFrozen();
    void setFrozenSkyTime(float time);
    float getFrozenSkyTime();
}
