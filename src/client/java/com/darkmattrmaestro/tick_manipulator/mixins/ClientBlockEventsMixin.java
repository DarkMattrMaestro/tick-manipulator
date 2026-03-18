package com.darkmattrmaestro.tick_manipulator.mixins;

import com.badlogic.gdx.math.Vector3;
import com.darkmattrmaestro.tick_manipulator.Highlight.BlockHighlight;
import com.darkmattrmaestro.tick_manipulator.PerWorldClientSingletons;
import finalforeach.cosmicreach.ClientBlockEvents;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.blocks.BlockState;
import finalforeach.cosmicreach.blocks.blockentities.BlockEntity;
import finalforeach.cosmicreach.entities.player.Player;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventArgs;
import finalforeach.cosmicreach.gameevents.blockevents.BlockEventTrigger;
import finalforeach.cosmicreach.gamestates.InGame;
import finalforeach.cosmicreach.items.ItemSlot;
import finalforeach.cosmicreach.items.ItemStack;
import finalforeach.cosmicreach.networking.client.ClientNetworkManager;
import finalforeach.cosmicreach.networking.packets.blocks.BreakBlockPacket;
import finalforeach.cosmicreach.networking.packets.blocks.InteractBlockPacket;
import finalforeach.cosmicreach.networking.packets.blocks.PlaceBlockPacket;
import finalforeach.cosmicreach.rendering.items.ItemRenderer;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.ui.UI;
import finalforeach.cosmicreach.util.constants.Direction;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientBlockEvents.class)
public class ClientBlockEventsMixin {

    @Inject(method = "breakBlock", at = @At(value = "TAIL"), cancellable = false)
    public void breakBlock(Zone zone, BlockPosition blockPos, double timeSinceLastInteract, CallbackInfo ci) {
        PerWorldClientSingletons.blockHighlight.updateHighlightedBlockList(blockPos);
    }

    @Inject(method = "placeBlock", at = @At(value = "RETURN"), cancellable = false)
    public void placeBlock(Zone zone, BlockState targetBlockState, BlockPosition blockPos, Vector3 intersection, int selectedSlotNum, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) {
            PerWorldClientSingletons.blockHighlight.updateHighlightedBlockList(blockPos);
        }
    }

    @Inject(method = "interactWithBlockIfBlockEntity", at = @At(value = "RETURN"), cancellable = false)
    public void interactWithBlockIfBlockEntity(Player player, Zone zone, BlockPosition blockPos, Vector3 intersection, CallbackInfoReturnable<Boolean> cir) {
        if (Boolean.TRUE.equals(cir.getReturnValue())) {
            PerWorldClientSingletons.blockHighlight.updateHighlightedBlockList(blockPos);
        }
    }

    @Inject(method = "interactWithBlock", at = @At(value = "TAIL"), cancellable = false)
    public void interactWithBlock(Player player, Zone zone, BlockPosition blockPos, ItemStack heldItemStack, BlockPosition placingBlockPos, Vector3 intersection, CallbackInfo ci) {
        PerWorldClientSingletons.blockHighlight.updateHighlightedBlockList(blockPos);
    }
}
