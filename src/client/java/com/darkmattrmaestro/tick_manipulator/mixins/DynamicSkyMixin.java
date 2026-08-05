package com.darkmattrmaestro.tick_manipulator.mixins;

import finalforeach.cosmicreach.world.DynamicSky;
import finalforeach.cosmicreach.world.Sky;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(DynamicSky.class)
public class DynamicSkyMixin {
    @ModifyVariable(method = "update()V", at = @At("STORE"), ordinal = 0)
    public float update(float currentTimeSeconds) {
        return ((Sky) (Object) this).getCurrentSkyTime();
    }
}
