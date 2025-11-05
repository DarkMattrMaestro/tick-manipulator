package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.utils.ZoneTickingUtils;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandTick extends Command {
    private static final String HELP_MSG = """
            
            Tick Manipulation Help:
            All commands take the general form '/tick {action} {arg1} {arg2} {...}'.
            
            Help
            - '/tick help' to get this help command.
            
            Resetting:
            - '/tick reset' to reset all ticking modifiers to the vanilla default (unfrozen, no delay).
            
            Freezing:
            - '/tick freeze' to freeze the game's ticking.
            - '/tick unfreeze' to unfreeze the game's ticking.
            
            Stepping:
            - '/tick step' to step to the next tick.
            - '/tick step {number of ticks}' to step the given number of ticks. Ticks are evaluated as usual
                then paused once the given number of ticks are processed. Eg. '/tick step 5' ticks the game
                five times.
            
            Delaying:
            - '/tick delay {delay in milliseconds}' to wait the given number of milliseconds before each tick.
                Eg. '/tick delay 1000' waits one second before each tick.
            
            Notes:
            - Stepping and Delaying are mutually exclusive. Stepping can only be used when ticking is frozen,
                while delaying requires ticking to be unfrozen.
            """;
    /* TODO
            State Loading:
            - `/tick savestate` to save the current tick state.
            - `/tick loadstate` to load the saved tick state
     */

    public static void help(IChat chat) {
        sendMsg(HELP_MSG);
    }

    public static void reset(IChat chat) {
        ZoneTickingUtils.resetTicking();
        sendMsg("Ticking reset");
    }

    public static void freeze(IChat chat) {
        ZoneTickingUtils.setFreezeState(true);
        sendMsg("Frozen");
    }

    public static void unfreeze(IChat chat) {
        ZoneTickingUtils.setFreezeState(false);
        sendMsg("Unfrozen");
    }

    public static void step(IChat chat) {
        ZoneTickingUtils.stepTicks(1);
        sendMsg("Stepped 1 tick");
    }

    public static void step(IChat chat, String arg1) {
        try {
            int steps = Integer.parseInt(arg1);
            if (steps <= 0) {
                sendMsg("Steps must be positive (and non-zero)!");
                return;
            }
            ZoneTickingUtils.stepTicks(steps);
            sendMsg("Stepped " + steps + " ticks");
        } catch (NumberFormatException e) {
            sendMsg("The command must be of the form /tick step {number of ticks}");
        }
    }

    public static void delay(IChat chat, String arg1) {
        try {
            int delay = Integer.parseInt(arg1);
            if (delay <= 0) {
                sendMsg("The delay must be a positive non-zero integer!");
                return;
            }
            ZoneTickingUtils.setDelay(delay);
            sendMsg("Set delay to " + delay + " ms");
        } catch (NumberFormatException e) {
            sendMsg("The command must be of the form /tick delay {delay in milliseconds}");
        }
    }

    public void run(IChat chat) {
        super.run(chat);

        if (!this.hasNextArg()) {
            sendMsg("No tick action provided! Type `/tick help` for a list of valid commands.");
            return;
        }

        String action = this.getNextArg();
        switch (action.toLowerCase()) {
            case "help": {
                help(chat);
                break;
            }
            case "reset": {
                reset(chat);
                break;
            }
            case "freeze": {
                freeze(chat);
                break;
            }
            case "unfreeze": {
                unfreeze(chat);
                break;
            }
            case "step": {
                if (this.hasNextArg()) {
                    step(chat, this.getNextArg());
                } else {
                    step(chat);
                }
                break;
            }
            case "delay": {
                if (this.hasNextArg()) {
                    delay(chat, this.getNextArg());
                } else {
                    sendMsg("The command must be of the form /tick delay {delay in milliseconds}");
                }
                break;
            }
            default: {
                sendMsg("Unrecognized tick action! Type `/tick help` for a list of valid commands.");
                break;
            }
        }
    }

    public String getShortDescription() {
        return "Many utilities relating to ticks. Type '/tick help' for more information.";
    }
}
