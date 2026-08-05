package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.*;
import com.darkmattrmaestro.tick_manipulator.PerWorldSingletons;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.world.RandomTicks;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.gameevents.blockevents.ScheduledBlockTrigger;
import finalforeach.cosmicreach.entities.*;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.rendering.IRenderable;
import finalforeach.cosmicreach.settings.DifficultySettings;
import finalforeach.cosmicreach.util.ArrayUtils;
import finalforeach.cosmicreach.world.*;
import finalforeach.cosmicreach.worldgen.ZoneGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;
import static java.lang.Integer.max;

@Mixin(Zone.class)
public class ZoneMixin implements Json.Serializable, Disposable, IMixinZone {

    @Unique
    private static final int msPerTick = 1000 / 20;

    @Unique
    boolean frozen = false;
    @Override
    public boolean getFrozen() {
        return this.frozen;
    }

    @Override
    public void setFrozen(boolean state) {
        this.frozen = state;
    }

    @Unique
    int advanceTicks = 0;
    @Override
    public int getAdvanceTicks() {
        return this.advanceTicks;
    }

    @Override
    public void setAdvanceTicks(int ticks) {
        this.advanceTicks = max(ticks, 0);
    }

    @Unique
    int tickDelay = 0;
    @Override
    public int getTickDelay() {
        return this.tickDelay;
    }

    @Override
    public void setTickDelay(int delay) {
        this.tickDelay = max(delay, 0);
    }

    @Shadow public PriorityQueue<ScheduledBlockTrigger> eventQueue;
    @Shadow public SnapshotArray<BlockEntity> tickingBlockEntities;
    @Shadow public Array<IRenderable> allRenderableBlockEntities;
    @Shadow public int currentZoneTick;
    @Shadow private transient Region[] regionValues;
    @Shadow private Array<Player> players;
    @Shadow public Vector3 spawnPoint;
    @Shadow public String zoneId;
    @Shadow public ZoneGenerator zoneGenerator;
    @Shadow public float respawnHeight;
    @Shadow private String skyId;
    @Shadow private transient World world;
    @Shadow private transient MobSpawner hostileMobSpawner;
    @Shadow private transient MobSpawner neutralMobSpawner;
    @Shadow private RandomTicks randomTicks;

    @Shadow
    public void runScheduledTriggers() {}

    @Shadow
    public void despawnEntity(GameEntity entity) {}

    @Shadow
    public Array<GameEntity> getAllEntities() { return null; }

    @Unique void updatePlayerEntities(float deltaTime) {
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
                    Player p = (Player) this.players.get(i);
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

    @Inject(
            method = "update",
            cancellable = true,
            at = @At("HEAD")
    )
    public void update(float deltaTime, CallbackInfo ci) {
        // If not frozen, use the original update function, and reset freeze stepping vars
        if (!frozen) {
            if (this.tickDelay > 0) {
                try {
                    for (int i = 0; i <= this.tickDelay / msPerTick; i++) {
                        this.updatePlayerEntities((float) msPerTick / 1000);
                        TimeUnit.MILLISECONDS.sleep(msPerTick);
                    }
                    if (this.tickDelay % msPerTick > 0) {
                        this.updatePlayerEntities((float) (this.tickDelay % msPerTick) / 1000);
                        TimeUnit.MILLISECONDS.sleep(this.tickDelay % msPerTick);
                    }
                } catch (Exception _) {}
            }

            this.setAdvanceTicks(0);
        } else {
            if (this.getAdvanceTicks() < 1) { // Frozen
                this.updatePlayerEntities(deltaTime);

                this.setAdvanceTicks(this.getAdvanceTicks() - 1);
                ci.cancel();
                return;
            }

            // Step normally
            sendMsg("Remaining ticks: " + this.getAdvanceTicks());
            this.setAdvanceTicks(this.getAdvanceTicks() - 1);
        }

        PerWorldSingletons.repeatCalls.forEach((repeatCall) -> {
            repeatCall.accept(null);
        });
    }

    @Shadow
    public void dispose() {}

    @Shadow
    public void write(Json json) {}

    @Shadow
    public void read(Json json, JsonValue jsonValue) {}

    @Unique
    private float savedSkyTimeSeconds;
    @Unique
    private boolean isSkyFrozen = false;

    @Unique
    public void setIsSkyFrozen(boolean isSkyFrozen) {
        this.isSkyFrozen = isSkyFrozen;
    }

    @Unique
    public boolean setIsSkyFrozen() {
        return this.isSkyFrozen;
    }

    @Shadow
    public long getCurrentWorldTick() {
        return this.world.getCurrentWorldTick();
    }

    @Inject(method = "getCurrentSkyTime", at = @At(value = "HEAD"), cancellable = true)
    public void getCurrentSkyTime(CallbackInfoReturnable<Float> cir) {
        if (isSkyFrozen) {
            cir.setReturnValue(this.savedSkyTimeSeconds);
        } else {
            this.savedSkyTimeSeconds = (float)this.getCurrentWorldTick() * 0.05F;
            cir.setReturnValue(this.savedSkyTimeSeconds);
        }
    }
}

