package com.darkmattrmaestro.tick_manipulator.utils;

import com.badlogic.gdx.math.Vector3;
import com.darkmattrmaestro.tick_manipulator.Constants;
import finalforeach.cosmicreach.entities.GameEntity;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.util.ArrayUtils;

import java.util.concurrent.atomic.AtomicReference;

public class EntitySelectionUtil {
    /**
     * Get the nearest entity to the player that is not a player.
     *
     * @return the nearest entity to the player that is not a player.
     */
    public static GameEntity getNearestEntityToPlayer() {
        Vector3 playerPos = InGame.getLocalPlayer().getPosition();
        AtomicReference<GameEntity> entity = new AtomicReference<>();
        AtomicReference<Float> closest = new AtomicReference<Float>(-1f);
        ArrayUtils.forEach(InGame.getLocalPlayer().getZone().getAllEntities().toArray(GameEntity.class), (GameEntity e) -> {
            if ("base:entity_player".equals(e.entityTypeId)) { return; }

            float dist2 = e.position.dst2(playerPos);
            if (closest.get() < 0 || dist2 < closest.get()) {
                closest.set(dist2);
                entity.set(e);
            }
        });

        return entity.get();
    }
}
