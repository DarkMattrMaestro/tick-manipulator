package com.darkmattrmaestro.tick_manipulator.mixins;

import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinZone;
import finalforeach.cosmicreach.accounts.Account;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.CommandTime;
import finalforeach.cosmicreach.chat.commands.parsing.ArgumentType;
import finalforeach.cosmicreach.chat.commands.parsing.CommandArgument;
import finalforeach.cosmicreach.chat.commands.parsing.CommandSignature;
import finalforeach.cosmicreach.singletons.GameSingletons;
import finalforeach.cosmicreach.world.Zone;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CommandTime.class)
public class CommandTimeMixin {
    @Inject(
            method = "run",
            cancellable = true,
            at = @At("HEAD")
    )
    public void run(IChat chat, CommandSignature signature, Object[] parsedArgs, CallbackInfo ci) {
        if (parsedArgs.length == 1) {
            String state = (String) parsedArgs[0];
            if ("freeze".equals(state)) {
                GameSingletons.world.getZones().forEach((Zone zone) -> {
                    ((IMixinZone) zone).setIsSkyFrozen(true);
                });

                chat.addMessage((Account) null, "Froze the sky");
                ci.cancel();
            } else if ("unfreeze".equals(state)) {
                GameSingletons.world.getZones().forEach((Zone zone) -> {
                    ((IMixinZone) zone).setIsSkyFrozen(false);
                });

                chat.addMessage((Account) null, "Unfroze the sky");
                ci.cancel();
            } else {
                chat.addMessage((Account) null, "Invalid arguments");
            }

            ci.cancel();
        }
    }

    @Inject(
            method = "getSignatures",
            cancellable = true,
            at = @At("HEAD")
    )
    public void getSignatures(CallbackInfoReturnable<CommandSignature[]> cir) {
        cir.setReturnValue(new CommandSignature[]{
                new CommandSignature(new CommandArgument[0]),
                new CommandSignature(new CommandArgument[]{
                        new CommandArgument(ArgumentType.STRING, "state")
                }),
                new CommandSignature(new CommandArgument[]{
                        new CommandArgument(ArgumentType.TIME_METHOD, "method"),
                        new CommandArgument(ArgumentType.INTEGER, "tick")
                })
        });
    }
}
