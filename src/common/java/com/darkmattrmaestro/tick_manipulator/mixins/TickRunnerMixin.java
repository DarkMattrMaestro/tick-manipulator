package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinTickRunner;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.FloatConsumer;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(TickRunner.class)
public class TickRunnerMixin implements IMixinTickRunner {
    @Unique private int ticksRemaining = 0;
    @Override public int getTicksRemaining() { return this.ticksRemaining; }
    @Override public void setTicksRemaining(int ticksRemaining) { this.ticksRemaining = ticksRemaining; }
    @Override public void decrementTicksRemaining() { this.ticksRemaining--; }

    @Unique private boolean frozen = false;
    @Override public boolean getFrozen() { return this.frozen; }
    @Override public void setFrozen(boolean frozen) { this.frozen = frozen; }

    @Unique private float customTickRate = 20.0F;
    @Unique private float customUpdateTimestep = 0.05F;

    @Override
    public void setTickRate(float tickRate) {
        customTickRate = tickRate;
        customUpdateTimestep = 1 / tickRate;
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
        if (!this.getFrozen()) {
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
    private <T> void sleepUpdatePlayer(long millis) throws InterruptedException{
        if (!this.isTickingStopped()) {
            Thread.sleep(Math.min(millis, 50));
            millis -= 50;
        }

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
