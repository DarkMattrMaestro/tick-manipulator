package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import com.darkmattrmaestro.tick_manipulator.packets.SkyPacket;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.entities.*;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.settings.DifficultySettings;
import finalforeach.cosmicreach.util.ArrayUtils;
import finalforeach.cosmicreach.world.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zone.class)
public class ZoneMixin implements Json.Serializable, Disposable, IMixinZone {
    @Shadow private Array<Player> players;
    @Shadow private transient World world;

    @Shadow
    public void despawnEntity(GameEntity entity) {}

    @Shadow
    public Array<GameEntity> getAllEntities() { return null; }

    @Unique public void tickManipulator$updatePlayerEntities(float deltaTime) {
        ArrayUtils.forEach(this.getAllEntities().toArray(GameEntity.class), (GameEntity e) -> {
            if (!"base:entity_player".equals(e.entityTypeId)) {
                return;
            }

            // This is the normal entity update part

            e.update((Zone) (Object) this, deltaTime);
            if (e.isMob() && !e.hasTag(CommonEntityTags.NO_DESPAWN)) {
                boolean canDespawn = true;
                float closestDistance = Float.MAX_VALUE;
                boolean isPeaceful = DifficultySettings.IsPeaceful();

                for (int i = 0; i < this.players.size; ++i) {
                    Player p = this.players.get(i);
                    if (p != null) {
                        Vector3 playerPos = p.getEntity().position;
                        closestDistance = Math.min(closestDistance, e.position.dst(playerPos));
                        if (closestDistance < 32.0F) {
                            canDespawn = false;
                            break;
                        }
                    }
                }

                boolean willDespawnFromPeaceful = isPeaceful && MobSpawner.HOSTILE_MOB_SPAWNER.hasMob(e);
                if (canDespawn || willDespawnFromPeaceful) {
                    if (closestDistance > 128.0F || isPeaceful) {
                        this.despawnEntity(e);
                        return;
                    }

                    if (e.age > 30.0F && closestDistance > 32.0F && MathUtils.randomBoolean(0.003125F)) {
                        this.despawnEntity(e);
                        return;
                    }
                }
            }
        });
    }

    @Shadow
    public void dispose() {}

    @Shadow
    public void write(Json json) {}

    @Shadow
    public void read(Json json, JsonValue jsonData) {}

    @Unique
    private float tickManipulator$savedSkyTimeSeconds;
    @Unique
    private boolean tickManipulator$isSkyFrozen = false;

    @Unique
    public void tickManipulator$setIsSkyFrozen(boolean isSkyFrozen) {
        this.tickManipulator$isSkyFrozen = isSkyFrozen;
    }

    @Unique
    public boolean tickManipulator$getIsSkyFrozen() {
        return this.tickManipulator$isSkyFrozen;
    }

    @Override
    public void tickManipulator$setFrozenSkyTime(float time) {
        this.tickManipulator$savedSkyTimeSeconds = time;
    }

    @Override
    public float tickManipulator$getFrozenSkyTime() {
        if (!tickManipulator$isSkyFrozen) {
            this.tickManipulator$savedSkyTimeSeconds = (float)this.getCurrentWorldTick() * 0.05F;
        }

        return this.tickManipulator$savedSkyTimeSeconds;
    }

    @Shadow
    public long getCurrentWorldTick() {
        return this.world.getCurrentWorldTick();
    }

    @Inject(method = "getCurrentSkyTime", at = @At(value = "HEAD"), cancellable = true)
    public void getCurrentSkyTime(CallbackInfoReturnable<Float> cir) {
        if (tickManipulator$isSkyFrozen) {
            cir.setReturnValue(this.tickManipulator$savedSkyTimeSeconds);
        } else {
            this.tickManipulator$savedSkyTimeSeconds = (float)this.getCurrentWorldTick() * 0.05F;
            cir.setReturnValue(this.tickManipulator$savedSkyTimeSeconds);
        }
    }

    @Inject(
            method = "addPlayer",
            at = @At(value = "TAIL")
    )
    public void addPlayer(Player player, CallbackInfo ci) {
        if (GameSingletons.isClient() && !GameSingletons.isHost()) {
            if (player != null && player.equals(GameSingletons.client().getLocalPlayer())) {
                GameSingletons.clientSingletons.sendAsClient(SkyPacket.generateRequestPacket((Zone) (Object) this));
            }
        }
    }
}

