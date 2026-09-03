package com.darkmattrmaestro.tick_manipulator.interfaces;

public interface IMixinZone {
    void tickManipulator$updatePlayerEntities(float deltaTime);

    void tickManipulator$setIsSkyFrozen(boolean isSkyFrozen);
    boolean tickManipulator$getIsSkyFrozen();
    void tickManipulator$setFrozenSkyTime(float time);
    float tickManipulator$getFrozenSkyTime();
}
