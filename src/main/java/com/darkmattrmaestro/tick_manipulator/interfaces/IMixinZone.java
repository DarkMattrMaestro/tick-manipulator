package com.darkmattrmaestro.tick_manipulator.interfaces;

public interface IMixinZone {
    boolean getFrozen();
    void setFrozen(boolean state);

    int getAdvanceTicks();
    void setAdvanceTicks(int ticks);

    int getTickDelay();
    void setTickDelay(int delay);
}
