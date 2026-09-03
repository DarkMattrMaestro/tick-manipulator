package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.PerWorldSingletons;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.util.FloatConsumer;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

@Mixin(TickRunner.class)
public class TickRunnerMixin {
    @ModifyConstant(method = "runTicks", constant = @Constant(floatValue = TickRunner.FIXED_UPDATE_TIMESTEP))
    private float runTicksTimestep(float value) { return PerWorldSingletons.getCustomUpdateTimestep(); }

    @Redirect(
            method = "runTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/badlogic/gdx/utils/Array;forEach(Ljava/util/function/Consumer;)V"
            )
    )
    private static <T> void forEachReplacement(Array<T> instance, Consumer<? super T> consumer) {
        instance.forEach((u) -> ((FloatConsumer) u).accept(0.05f)); // Make other consumers think the timestep is normal, mouahahahahaha!
    }

    @Redirect(
            method = "runTicks",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/Thread;sleep(J)V"
            )
    )
    private static <T> void sleepUpdatePlayer(long millis) throws InterruptedException{
        if (millis <= 50) {
            Thread.sleep(millis);
        } else  {
            Thread.sleep(50);
            int divisions = (int) (millis / 50);
            long substepDelay = (millis - 50) / divisions;
            for (int substep = 1; substep <= divisions; substep++) {
                GameSingletons.world.getZones().forEach((Zone zone) -> {
                    ((IMixinZone) zone).updatePlayerEntities((float) substepDelay / 1000);
                });
                Thread.sleep(substepDelay);
            }
        }
    }

//    @ModifyConstant(method = "getPartTick", constant = @Constant(doubleValue = (double) TickRunner.FIXED_UPDATE_TIMESTEP))
//    private double getPartTickTimestep(double value) { return (double) PerWorldSingletons.getCustomUpdateTimestep(); }
//
//    @ModifyConstant(method = "partTickLerp*", constant = @Constant(floatValue = TickRunner.FIXED_UPDATE_TIMESTEP))
//    private float partTickLerpTimestep(float value) { return PerWorldSingletons.getCustomUpdateTimestep(); }
//
//    @ModifyConstant(method = "partTickSlerp", constant = @Constant(floatValue = TickRunner.FIXED_UPDATE_TIMESTEP))
//    private float partTickSlerpTimestep(float value) { return PerWorldSingletons.getCustomUpdateTimestep(); }
}
