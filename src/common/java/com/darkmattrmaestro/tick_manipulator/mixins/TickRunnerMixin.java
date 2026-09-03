package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinTickRunner;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.FloatConsumer;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;

import java.util.function.Consumer;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

@Mixin(TickRunner.class)
public class TickRunnerMixin implements IMixinTickRunner {
    @Shadow float fixedUpdateAccumulator;

    @Unique private int ticksRemaining = 0;
    @Override public int getTicksRemaining() { return this.ticksRemaining; }
    @Override public void setTicksRemaining(int ticksRemaining) { this.ticksRemaining = ticksRemaining; }
    @Override public void decrementTicksRemaining() { this.ticksRemaining--; }

    @Unique private boolean frozen = false;
    @Override public boolean getFrozen() { return this.frozen; }
    @Override public void setFrozen(boolean frozen) { this.frozen = frozen; }

    @Unique private long sprintTicks = 0;
    @Unique private long sprintStartTime = 0;
    @Unique private long sprintEndTick = 0;
    @Override public boolean isSprinting() {
        return this.sprintEndTick > GameSingletons.world.currentWorldTick && sprintStartTime != 0;
    }
    @Override
    public void setSprint(long ticks) {
        this.sprintEndTick = ticks + GameSingletons.world.currentWorldTick;
        this.sprintStartTime = System.nanoTime();
        this.sprintTicks = ticks;
        this.setTickRate(Float.MAX_VALUE);
    }
    @Unique
    public void updateSprinting() {
        if (this.sprintEndTick < GameSingletons.world.currentWorldTick && sprintStartTime != 0) {
            long deltaTimeNano = System.nanoTime() - this.sprintStartTime;
            long deltaTimeMilli = deltaTimeNano / 1000000;

            long deltaTimeSeconds = deltaTimeMilli / 1000;
            deltaTimeMilli %= 1000;

            long deltaTimeMinutes = deltaTimeSeconds / 60;
            deltaTimeSeconds %= 60;

            long deltaTimeHours = deltaTimeMinutes / 60;
            deltaTimeMinutes %= 60;

            long deltaTimeDays = deltaTimeHours / 24;
            deltaTimeHours %= 24;

            sendMsg("Finished sprinting " + this.sprintTicks + " ticks in " + deltaTimeDays + "d" + deltaTimeHours + "h" + deltaTimeMinutes + "min" + deltaTimeSeconds + "." + deltaTimeMilli + "s");
            this.setTickRate(IMixinTickRunner.DEFAULT_TICK_RATE);
            this.sprintStartTime = 0;
            this.fixedUpdateAccumulator = 0;
        }
    }

    @Unique private float customTickRate = 20.0F;
    @Unique private float customUpdateTimestep = 0.05F;

    @Override
    public void setTickRate(float tickRate) {
        customTickRate = tickRate;
        customUpdateTimestep = tickRate == Float.MAX_VALUE ? 0 : 1 / tickRate;
    }

    @Override public float getCustomTickRate() {
        return customTickRate;
    }
    @Override public float getCustomUpdateTimestep() {
        return customUpdateTimestep;
    }

    public boolean isTickingStopped() {
        return this.getFrozen() && this.getTicksRemaining() < 1;
    }



    @ModifyConstant(method = "runTicks", constant = @Constant(floatValue = TickRunner.FIXED_UPDATE_TIMESTEP))
    private float runTicksTimestep(float value) { return this.getCustomUpdateTimestep(); }

    @Redirect(
            method = "runTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/badlogic/gdx/utils/Array;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private <T> void forEachReplacement(Array<T> instance, Consumer<? super T> consumer) {
        updateSprinting();
        if (!this.getFrozen() || this.isSprinting()) {
            this.setTicksRemaining(0);
        } else {
            if (this.getTicksRemaining() < 1) {
                return;
            }

            decrementTicksRemaining();
        }

        instance.forEach((u) -> ((FloatConsumer) u).accept(0.05f)); // Make other consumers think the timestep is normal, mouahahahahaha!
    }

    @Redirect(
            method = "runTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Thread;sleep(J)V"
            )
    )
    private <T> void sleepUpdatePlayer(long millis) throws InterruptedException {
        // No need for delay when sprinting
        if (this.isSprinting()) { return; }

        // Normal delay
        if (!this.isTickingStopped()) {
            Thread.sleep(Math.min(millis, 50));
            millis -= 50;
        }

        // Extra delay when the ticking system is slowed/frozen, with player updates
        if (millis > 0) {
            int divisions = (int) (millis / 50) + 1;
            long substepDelay = (millis) / divisions;
            for (int substep = 1; substep <= divisions; substep++) {
                GameSingletons.world.getZones().forEach((Zone zone) -> {
                    ((IMixinZone) zone).updatePlayerEntities((float) substepDelay / 1000);
                });
                Thread.sleep(substepDelay);
            }
        }
    }
}
