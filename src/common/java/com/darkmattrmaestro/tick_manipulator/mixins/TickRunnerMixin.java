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

    @Unique private int tickManipulator$ticksRemaining = 0;
    @Override public int tickManipulator$getTicksRemaining() { return this.tickManipulator$ticksRemaining; }
    @Override public void tickManipulator$setTicksRemaining(int ticksRemaining) { this.tickManipulator$ticksRemaining = ticksRemaining; }
    @Override public void tickManipulator$decrementTicksRemaining() { this.tickManipulator$ticksRemaining--; }

    @Unique private boolean tickManipulator$frozen = false;
    @Override public boolean tickManipulator$getFrozen() { return this.tickManipulator$frozen; }
    @Override public void tickManipulator$setFrozen(boolean frozen) { this.tickManipulator$frozen = frozen; }

    @Unique private long tickManipulator$sprintTicks = 0;
    @Unique private long tickManipulator$sprintStartTime = 0;
    @Unique private long tickManipulator$sprintEndTick = 0;
    @Override public boolean tickManipulator$isSprinting() {
        return this.tickManipulator$sprintEndTick > GameSingletons.world.currentWorldTick && tickManipulator$sprintStartTime != 0;
    }
    @Override
    public void tickManipulator$setSprint(long ticks) {
        this.tickManipulator$sprintEndTick = ticks + GameSingletons.world.currentWorldTick;
        this.tickManipulator$sprintStartTime = System.nanoTime();
        this.tickManipulator$sprintTicks = ticks;
        this.tickManipulator$setTickRate(Float.MAX_VALUE);
    }
    @Unique
    public void tickManipulator$updateSprinting() {
        if (this.tickManipulator$sprintEndTick < GameSingletons.world.currentWorldTick && tickManipulator$sprintStartTime != 0) {
            long deltaTimeNano = System.nanoTime() - this.tickManipulator$sprintStartTime;
            long deltaTimeMilli = deltaTimeNano / 1000000;

            long deltaTimeSeconds = deltaTimeMilli / 1000;
            deltaTimeMilli %= 1000;

            long deltaTimeMinutes = deltaTimeSeconds / 60;
            deltaTimeSeconds %= 60;

            long deltaTimeHours = deltaTimeMinutes / 60;
            deltaTimeMinutes %= 60;

            long deltaTimeDays = deltaTimeHours / 24;
            deltaTimeHours %= 24;

            sendMsg("Finished sprinting " + this.tickManipulator$sprintTicks + " ticks in " + deltaTimeDays + "d" + deltaTimeHours + "h" + deltaTimeMinutes + "min" + deltaTimeSeconds + "." + deltaTimeMilli + "s");
            this.tickManipulator$setTickRate(IMixinTickRunner.DEFAULT_TICK_RATE);
            this.tickManipulator$sprintStartTime = 0;
            this.fixedUpdateAccumulator = -1;
        }
    }

    @Unique private float tickManipulator$customTickRate = 20.0F;
    @Unique private float tickManipulator$customUpdateTimestep = 0.05F;

    @Override
    public void tickManipulator$setTickRate(float tickRate) {
        tickManipulator$customTickRate = tickRate;
        tickManipulator$customUpdateTimestep = tickRate == Float.MAX_VALUE ? 0 : 1 / tickRate;
    }

    @Override public float tickManipulator$getCustomTickRate() {
        return tickManipulator$customTickRate;
    }
    @Override public float tickManipulator$getCustomUpdateTimestep() {
        return tickManipulator$customUpdateTimestep;
    }

    public boolean tickManipulator$isTickingStopped() {
        return this.tickManipulator$getFrozen() && this.tickManipulator$getTicksRemaining() < 1;
    }



    @ModifyConstant(method = "runTicks", constant = @Constant(floatValue = TickRunner.FIXED_UPDATE_TIMESTEP))
    private float runTicksTimestep(float value) { return this.tickManipulator$getCustomUpdateTimestep(); }

    @Redirect(
            method = "runTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/badlogic/gdx/utils/Array;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private <T> void forEachReplacement(Array<T> instance, Consumer<? super T> consumer) {
        tickManipulator$updateSprinting();
        if (!this.tickManipulator$getFrozen() || this.tickManipulator$isSprinting()) {
            this.tickManipulator$setTicksRemaining(0);
        } else {
            if (this.tickManipulator$getTicksRemaining() < 1) {
                return;
            }

            tickManipulator$decrementTicksRemaining();
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
    private void sleepUpdatePlayer(long millis) throws InterruptedException {
        // No need for delay when sprinting
        if (this.tickManipulator$isSprinting()) { return; }

        // Normal delay
        if (!this.tickManipulator$isTickingStopped()) {
            Thread.sleep(Math.min(millis, 50));
            millis -= 50;
        }

        // Extra delay when the ticking system is slowed/frozen, with player updates
        if (millis > 0) {
            int divisions = (int) (millis / 50) + 1;
            long substepDelay = (millis) / divisions;
            for (int substep = 1; substep <= divisions; substep++) {
                GameSingletons.world.getZones().forEach((Zone zone) -> {
                    ((IMixinZone) zone).tickManipulator$updatePlayerEntities((float) substepDelay / 1000);
                });
                Thread.sleep(substepDelay);
            }
        }
    }
}
