package com.darkmattrmaestro.tick_manipulator.interfaces;

public interface IMixinTickRunner {
    int tickManipulator$getTicksRemaining();
    void tickManipulator$setTicksRemaining(int ticksRemaining);
    void tickManipulator$decrementTicksRemaining();

    boolean tickManipulator$getFrozen();
    void tickManipulator$setFrozen(boolean frozen);

    boolean tickManipulator$isSprinting();
    void tickManipulator$cancelSprint();
    void tickManipulator$setSprint(long ticks);

    void tickManipulator$setTickRate(float tickRate);
    float tickManipulator$getCustomTickRate();
    float tickManipulator$getCustomUpdateTimestep();

    float DEFAULT_TICK_RATE = 20.0F;

    boolean tickManipulator$isTickingStopped();
}
