package com.darkmattrmaestro.tick_manipulator.interfaces;

import com.badlogic.gdx.utils.Array;
import finalforeach.cosmicreach.particles.GameParticleSystem;

public interface IMixinGameParticleMesh {
    public Array<GameParticleSystem> getParticleSystems();
}
