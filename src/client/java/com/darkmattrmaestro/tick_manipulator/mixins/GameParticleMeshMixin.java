package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinGameParticleMesh;
import finalforeach.cosmicreach.particles.GameParticleSystem;
import finalforeach.cosmicreach.rendering.GameParticleMesh;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(GameParticleMesh.class)
public class GameParticleMeshMixin implements IMixinGameParticleMesh {
    @Shadow
    Array<GameParticleSystem> particleSystems = new Array<>();

    @Unique
    public Array<GameParticleSystem> getParticleSystems() {
        return this.particleSystems;
    }
}
