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

    public void run(IChat chat) {
        super.run(chat);
        switch (this.getNumberOfArgs()) {
            case 1: { // /tick freeze   /tick unfreeze   /tick step
                String action = this.getNextArg();

                switch (action.toLowerCase()) {
                    case "help": {
                        sendMsg(HELP_MSG);
                        break;
                    }
                    case "reset": {
                        ZoneTickingUtils.resetTicking();
                        sendMsg("Ticking reset");
                        break;
                    }
                    case "freeze": {
                        ZoneTickingUtils.setFreezeState(true);
                        sendMsg("Frozen");
                        break;
                    }
                    case "unfreeze": {
                        ZoneTickingUtils.setFreezeState(false);
                        sendMsg("Unfrozen");
                        break;
                    }
                    case "step": {
                        ZoneTickingUtils.stepTicks(1);
                        sendMsg("Stepped 1 tick");
                        break;
                    }
                    default: {
                        sendMsg("Unrecognized tick action! Try one of: freeze, unfreeze, step");
                        break;
                    }
                }

                break;
            }
            case 2: { // /tick step {#ticks}
                String action = this.getNextArg();

                switch (action.toLowerCase()) {
                    case "step": {
                        try {
                            int steps = Integer.parseInt(this.getNextArg());
                            if (steps <= 0) {
                                sendMsg("Steps must be positive (and non-zero)!");
                                return;
                            }
                            ZoneTickingUtils.stepTicks(steps);
                            sendMsg("Stepped " + steps + " ticks");
                        } catch (NumberFormatException e) {
                            sendMsg("The command must be of the form /tick step {number of ticks}");
                        }
                        break;
                    }
                    case "delay": {
                        try {
                            int delay = Integer.parseInt(this.getNextArg());
                            if (delay <= 0) {
                                sendMsg("The delay must be a positive non-zero integer!");
                                return;
                            }
                            ZoneTickingUtils.setDelay(delay);
                            sendMsg("Set delay to " + delay + " ms");
                        } catch (NumberFormatException e) {
                            sendMsg("The command must be of the form /tick delay {delay in milliseconds}");
                        }
                        break;
                    }
                    default: {
                        sendMsg("Unrecognized tick action! Try one of: freeze, unfreeze, step");
                        break;
                    }
                }

                break;
            }
            default: {
                sendMsg("Incorrect number of arguments! The tick command is of the form /tick {action} {arg1} {arg2} {...}");
                break;
            }
        }
    }

    public String getShortDescription() {
        return "Many utilities relating to ticks. Type '/tick help' for more information.";
    }
}
