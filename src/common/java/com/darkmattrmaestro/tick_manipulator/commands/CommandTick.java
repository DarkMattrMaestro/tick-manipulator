package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.PerWorldSingletons;
import com.darkmattrmaestro.tick_manipulator.interfaces.IMixinTickRunner;
import finalforeach.cosmicreach.TickRunner;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;
import finalforeach.cosmicreach.singletons.GameSingletons;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandTick extends Command {
    private static final String HELP_MSG = """
            
            Tick Manipulation Help:
            All commands take the general form '/tick {action} {arg1} {arg2} {...}'.
            
            Help
            - '/tick help' to get this help command.
            
            GUI
            - '/tick gui' to display the encompassing GUI for the Tick Manipulator mod.
            
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
            
            Repeating:
            - '/tick repeat add {command}' adds the given command (without a slash) to the list of commands to
                run every tick. E.g. '/tick repeat add data entity velocity'.
            - '/tick repeat clear' clears the list of commands to run every tick.
            
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

    public void repeat(IChat chat) {
        if (this.hasNextArg()) {
            String action = this.getNextArg().toLowerCase();

            if ("clear".equals(action)) {
                PerWorldSingletons.repeatCalls.clear();
            } else if ("add".equals(action)) {
                String[] subcallArgs = new String[this.getNumberOfArgsLeft()];
                for (int i = 0; i < subcallArgs.length; i++) {
                    subcallArgs[i] = this.getNextArg();
                }
                PerWorldSingletons.repeatCalls.add((_) -> {
                    triggerCommand(chat, this.account, subcallArgs);
                });
            } else {
                sendMsg("Unrecognized tick repeat action! Only `clear` and `add` are valid.");
            }
        } else {
            Constants.LOGGER.warn("Repeating {} actions every tick.", PerWorldSingletons.repeatCalls.size());
            sendMsg("Repeating " + PerWorldSingletons.repeatCalls.size() + " actions every tick.");
        }
    }

    public static void reset(IChat chat) {
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTicksRemaining(0);
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setFrozen(false);
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTickRate(IMixinTickRunner.DEFAULT_TICK_RATE);
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$cancelSprint();
        PerWorldSingletons.repeatCalls.clear();
        sendMsg("Ticking reset");
    }

    public static void freeze(IChat chat) {
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setFrozen(true);
        sendMsg("Frozen");
    }

    public static void unfreeze(IChat chat) {
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setFrozen(false);
        sendMsg("Unfrozen");
    }

    public static void step(IChat chat) {
        ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTicksRemaining(1);
        sendMsg("Stepped 1 tick");
    }

    public static void step(IChat chat, String arg1) {
        try {
            int steps = Integer.parseInt(arg1);
            if (steps <= 0) {
                sendMsg("Steps must be positive (and non-zero)!");
                return;
            }
            ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTicksRemaining(steps);
            sendMsg("Stepping " + steps + " ticks");
        } catch (NumberFormatException e) {
            sendMsg("The command must be of the form `/tick step {number of ticks}`");
        }
    }

    public static void rate(IChat chat, String arg1) {
        if (arg1.toLowerCase().startsWith("reset")) {
            ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTickRate(IMixinTickRunner.DEFAULT_TICK_RATE);
            sendMsg("Reset tick rate to " + IMixinTickRunner.DEFAULT_TICK_RATE + " ticks per second.");
            return;
        }

        try {
            float rate = Float.parseFloat(arg1);
            if (rate <= 0) {
                sendMsg("The tick rate must be a positive non-zero floating-point number!");
                return;
            }
            ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setTickRate(rate);
            sendMsg("Set tick rate to " + rate + " ticks per second.");
        } catch (NumberFormatException e) {
            sendMsg("The command must be of the form `/tick rate {ticks per second}` or `/tick rate reset`");
        }
    }

    public static void sprint(IChat chat, String arg1) {
        if (arg1.toLowerCase().startsWith("cancel")) {
            ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$cancelSprint();
            sendMsg("Reset tick rate to " + IMixinTickRunner.DEFAULT_TICK_RATE + " ticks per second.");
            return;
        }

        try {
            long ticks = Long.parseLong(arg1);
            if (ticks <= 0) {
                sendMsg("The number of ticks to sprint must be a positive, non-zero, whole number!");
                return;
            }
            ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$setSprint(ticks);
            sendMsg("Sprinting " + ticks + " ticks.");
        } catch (NumberFormatException e) {
            sendMsg("The command must be of the form `/tick sprint {ticks per second}` or `/tick sprint cancel`");
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
            case "repeat": {
                repeat(chat);
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
            case "rate": {
                if (this.hasNextArg()) {
                    rate(chat, this.getNextArg());
                } else {
                    sendMsg("The current tick rate is " + ((IMixinTickRunner) TickRunner.INSTANCE).tickManipulator$getCustomTickRate() + " ticks per second.");
                }
                break;
            }
            case "sprint": {
                if (this.hasNextArg()) {
                    sprint(chat, this.getNextArg());
                } else {
                    sendMsg("The command must be of the form `/tick sprint {ticks per second}` or `/tick sprint cancel`");
                }
                break;
            }
            case "gui": {
                if (!GameSingletons.isClient()) {
                    sendMsg("This command is only valid for clients!");
                }

                Constants.clientTickGUISpawner.run();
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
