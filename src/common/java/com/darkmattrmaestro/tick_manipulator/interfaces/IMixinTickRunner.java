package com.darkmattrmaestro.tick_manipulator.interfaces;

public interface IMixinTickRunner {
    int getTicksRemaining();
    void setTicksRemaining(int ticksRemaining);
    void decrementTicksRemaining();

    boolean getFrozen();
    void setFrozen(boolean frozen);

    void setTickRate(float tickRate);

    float getCustomTickRate();

    float getCustomUpdateTimestep();

    float DEFAULT_TICK_RATE = 20.0F;

    boolean isTickingStopped();
}
