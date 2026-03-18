package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.PerWorldClientSingletons;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.gameevents.GameEventTrigger;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEvents;
import finalforeach.cosmicreach.gameevents.blockevents.actions.IBlockAction;
import finalforeach.cosmicreach.util.predicates.GamePredicateBlockEventArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(BlockEventTrigger.class)
public class BlockEventTriggerMixin extends GameEventTrigger<BlockEventArgs, IBlockAction> {
    @Shadow
    public Object rootCause(BlockEventArgs args) {
        return args.srcBlockState;
    }

    @Shadow
    public void act(BlockEventArgs args) { }

    @Inject(method = "act", at = @At(value = "TAIL"), cancellable = false)
    public void actMixin(BlockEventArgs args, CallbackInfo ci) {
        Constants.LOGGER.warn("Activated! {}", args.blockPos);
        PerWorldClientSingletons.blockHighlight.updateHighlightedBlockList((BlockPosition) args.blockPos);
    }
}
