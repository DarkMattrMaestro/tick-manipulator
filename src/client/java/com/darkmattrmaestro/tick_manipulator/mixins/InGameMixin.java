package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.utils.Array;
import com.darkmattrmaestro.tick_manipulator.Highlight;
import com.darkmattrmaestro.tick_manipulator.PerWorldSingletons;
import com.darkmattrmaestro.tick_manipulator.utils.TickManipulatorInputProcessor;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.entities.Entity;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gamestates.GameState;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.rendering.GameParticleRenderer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGame.class)
public class InGameMixin {
    @Shadow
    private static Player localPlayer;
    @Shadow
    private static PerspectiveCamera rawWorldCamera;
    @Shadow
    public GameParticleRenderer gameParticles = new GameParticleRenderer();

    @Shadow
    InputProcessor inputMultiplexer;

    @Inject(method = "create", at = @At(value = "TAIL"))
    public void create(CallbackInfo ci) {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new TickManipulatorInputProcessor());
        multiplexer.addProcessor(this.inputMultiplexer); // Cosmic Reach's default processors
        this.inputMultiplexer = multiplexer;
        Gdx.input.setInputProcessor(this.inputMultiplexer);
    }

    /**
     * Render highlighted elements, e.g. entities, particles.
     *
     * @param playerZone the zone in which is situated the player.
     */
    protected void renderHighlight(Zone playerZone) {
//        boolean usePostProcessing = false;
//        boolean useUnderwaterPostProcessing = false;
//        boolean useUnderLavaPostProcessing = false;
//        if (localPlayer != null && localPlayer.getEntity() != null) {
//            playerHeadPosition.set(localPlayer.getPosition());
//            playerHeadPosition.add(localPlayer.getEntity().viewPositionOffset);
//            BlockState headBlock = playerZone.getBlockState(playerHeadPosition);
//            if (headBlock != null) {
//                useUnderwaterPostProcessing = headBlock.getBlockId().equals("base:water");
//                useUnderLavaPostProcessing = headBlock.getBlockId().equals("base:lava");
//            }
//        }
//
//        usePostProcessing = useUnderLavaPostProcessing || useUnderwaterPostProcessing;
//        Sky sky = Sky.getCurrentSky(playerZone);
//        sky.update();
//        if (usePostProcessing) {
//            this.postProcessing.begin();
//            if (useUnderwaterPostProcessing) {
//                sky.fogDensity *= 1.25F;
//                this.postProcessing.shader.settings = UnderwaterShader.WATER_SETTINGS;
//            }
//
//            if (useUnderLavaPostProcessing) {
//                sky.fogDensity *= 4.0F;
//                this.postProcessing.shader.settings = UnderwaterShader.LAVA_SETTINGS;
//            }
//        }
//
//        ScreenUtils.clear(sky.currentSkyColor, true);
//        this.targetFovOffset = 0.0F;
//        if (localPlayer.isSprinting) {
//            this.targetFovOffset += 7.5F;
//        }
//
//        this.fovOffset = TickRunner.INSTANCE.partTickLerp(this.fovOffset, this.targetFovOffset, 1.0F);
//        PerspectiveCamera var10000 = rawWorldCamera;
//        var10000.fieldOfView += this.fovOffset;
//        this.viewport.apply();
//        sky.drawSky(rawWorldCamera);
//        GameSingletons.zoneRenderer.render(playerZone, rawWorldCamera);
        Gdx.gl.glDepthMask(true);
//        Array<IRenderable> allRenderableBlockEntities = playerZone.allRenderableBlockEntities;
//
//        for(int i = 0; i < allRenderableBlockEntities.size; ++i) {
//            IRenderable renderable = (IRenderable)allRenderableBlockEntities.get(i);
//            if (renderable != null) {
//                renderable.onRender(rawWorldCamera);
//            }
//        }

        if (Highlight.highlightEntities) {
            Array<Entity> entities = playerZone.getAllEntities();

            for (int i = 0; i < entities.size; ++i) {
                Entity e = (Entity) entities.get(i);
                if (e != null) {
                    e.render(rawWorldCamera);
                    e.clientUpdate();
                }
            }
        }

//        this.blockSelection.render(rawWorldCamera);
        if (Highlight.highlightParticles) {
            this.gameParticles.render(rawWorldCamera, !TickRunner.INSTANCE.isRunning() && GameSingletons.isHost ? 0.0F : Gdx.graphics.getDeltaTime());
        }

//        if (usePostProcessing) {
//            this.postProcessing.end();
//            this.postProcessing.render();
//        }

    }

    @Shadow
    protected void renderWorld(Zone playerZone) {}

    @Inject(method = "switchAwayTo(Lfinalforeach/cosmicreach/gamestates/GameState;)V", at = @At(value = "INVOKE", target = "Lfinalforeach/cosmicreach/chat/Chat;clear()V"))
    public void onWorldExit(GameState gameState, CallbackInfo ci) {
        PerWorldSingletons.repeatCalls.clear();
    }

    @Inject(method = "render", at = @At(value = "TAIL"))
    public void render(CallbackInfo ci) {
        if (localPlayer != null) {
            Zone playerZone = localPlayer.getZone();
            if (playerZone != null) {
                renderHighlight(playerZone);
            }
        }
    }
}
