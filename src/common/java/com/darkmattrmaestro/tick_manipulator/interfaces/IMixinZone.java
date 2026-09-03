package com.darkmattrmaestro.tick_manipulator.interfaces;

import org.spongepowered.asm.mixin.Unique;

public interface IMixinZone {
    void updatePlayerEntities(float deltaTime);

    void setIsSkyFrozen(boolean isSkyFrozen);
    boolean getIsSkyFrozen();
    void setFrozenSkyTime(float time);
    float getFrozenSkyTime();
}
