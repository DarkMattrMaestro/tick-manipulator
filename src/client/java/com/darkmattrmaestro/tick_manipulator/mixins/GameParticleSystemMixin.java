package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.darkmattrmaestro.tick_manipulator.Highlight;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinGameParticleSystem;
import finalforeach.cosmicreach.particles.GameParticleSystem;
import finalforeach.cosmicreach.particles.components.IParticleComponent;
import finalforeach.cosmicreach.savelib.crbin.CRBSerialized;
import finalforeach.cosmicreach.savelib.crbin.CRBinDeserializer;
import finalforeach.cosmicreach.savelib.crbin.CRBinSerializer;
import finalforeach.cosmicreach.savelib.crbin.ICRBinSerializable;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameParticleSystem.class)
public class GameParticleSystemMixin implements ICRBinSerializable, IMixinGameParticleSystem {
    @Shadow public static final int IDX_POSITION;
    @Shadow public static final int IDX_LAST_POSITION;
    @Shadow public static final int IDX_ACCELERATION;
    @Shadow public static final int IDX_COLOR;
    @Shadow public static final int IDX_SIZE;
    @Shadow public static final int IDX_TEX_INDEX;
    @Shadow public static final int IDX_AGE;
    @Shadow public static final int IDX_MAX_AGE;
    @Shadow public static final int TOTAL_IDX;
    @Shadow
    public Array<IParticleComponent> particleComponents = new Array<>();

    @Shadow
    @CRBSerialized
    public float maxAge;
    @Shadow
    @CRBSerialized
    public float age;

    public FloatArray particleData = new FloatArray();
    
    @Override
    public void setMaxAge(float newMaxAge) {
        int l = this.particleData.size;
        this.maxAge = newMaxAge;

        for(int i = l - TOTAL_IDX; i >= 0; i -= TOTAL_IDX) {
            this.particleData.set(i + IDX_MAX_AGE, newMaxAge);
        }
    }

//    @Override
//    public void swapParticlesPublic(int a, int b) {
//        if (a != b) {
//            float[] items = this.particleData.items;
//
//            for(int i = 0; i < TOTAL_IDX; ++i) {
//                float ai = items[i + a];
//                float bi = items[i + b];
//                items[i + a] = bi;
//                items[i + b] = ai;
//            }
//
//        }
//    }

    @Shadow
    private void swapParticles(int a, int b) { }

//    @Inject(method = "<init>()V", at = @At(value = "TAIL"))
//    public void GameParticleSystem(CallbackInfo ci) {
//        if (Highlight.persistParticles) {
//            setMaxAge(Float.POSITIVE_INFINITY);
//            Constants.LOGGER.warn("Created modified");
//        }
//    }

//    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At(value = "TAIL"))
//    public void GameParticleSystem(String id, CallbackInfo ci) {
//        if (Highlight.persistParticles) {
//            setMaxAge(Float.POSITIVE_INFINITY);
//            Constants.LOGGER.warn("Created modified");
//        }
//    }
//
//    @Inject(method = "updateParticles", at = @At(value = "TAIL"))
//    public void updateParticles(Zone zone, float deltaTime, CallbackInfo ci) {
//        if (Highlight.persistParticles) {
//            this.age -= deltaTime;
//        }
//    }

    @Inject(method = "updateParticles", at = @At(value = "HEAD"), cancellable = true)
    public void updateParticles(Zone zone, float deltaTime, CallbackInfo ci) {
        if (deltaTime != 0.0F) {
            if (this.age == 0.0F) {
                Array.ArrayIterator var3 = this.particleComponents.iterator();

                while(var3.hasNext()) {
                    IParticleComponent p = (IParticleComponent)var3.next();
                    p.onStart(zone, (GameParticleSystem) (Object) this);
                }
            }

            Array.ArrayIterator var8 = this.particleComponents.iterator();

            while(var8.hasNext()) {
                IParticleComponent p = (IParticleComponent)var8.next();
                p.updateParticles(zone, (GameParticleSystem) (Object) this, deltaTime);
            }

            float[] items = this.particleData.items;
            int l = this.particleData.size;

            for(int i = l - TOTAL_IDX; i >= 0; i -= TOTAL_IDX) {
                int var10001 = i + IDX_AGE;
                items[var10001] += deltaTime;
                items[i + IDX_ACCELERATION] = 0.0F;
                items[i + IDX_ACCELERATION + 1] = 0.0F;
                items[i + IDX_ACCELERATION + 2] = 0.0F;
            }

            if (!Highlight.persistParticles) {
                for (int i = l - TOTAL_IDX; i >= 0; i -= TOTAL_IDX) {
                    float maxAge = items[i + IDX_MAX_AGE];
                    if (maxAge != -1.0F) {
                        float age = items[i + IDX_AGE];
                        if (age > maxAge) {
                            this.swapParticles(i, this.particleData.size - TOTAL_IDX);
                            FloatArray var10000 = this.particleData;

                            var10000.size -= TOTAL_IDX;
                        }
                    }
                }

                this.age += deltaTime;
            }
        }

        ci.cancel();
    }

    @Shadow
    public void read(CRBinDeserializer deserial) { }

    @Shadow
    public void write(CRBinSerializer serial) { }

    static {
        int i = 0;
        IDX_POSITION = i;
        i += 3;
        IDX_LAST_POSITION = i;
        i += 3;
        IDX_ACCELERATION = i;
        i += 3;
        IDX_COLOR = i++;
        IDX_SIZE = i++;
        IDX_TEX_INDEX = i++;
        IDX_AGE = i++;
        IDX_MAX_AGE = i++;
        TOTAL_IDX = i;
    }
}

