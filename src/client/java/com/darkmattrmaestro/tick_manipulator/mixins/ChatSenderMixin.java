package com.darkmattrmaestro.tick_manipulator.mixins;

import com.darkmattrmaestro.tick_manipulator.ClientConstants;
import finalforeach.cosmicreach.networking.client.ChatSender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChatSender.class)
public class ChatSenderMixin {
    @Redirect(
            method = "sendMessageOrCommand",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"
            )
    )
    private static boolean equalsReplacement(String instance, Object anObject) {
        if ("panorama".equals(anObject)) {
            return ClientConstants.clientsideCommands.contains(instance);
        }

        return instance.equals(anObject);
    }
}
