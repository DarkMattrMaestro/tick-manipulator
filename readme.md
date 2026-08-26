# Tick Manipulator
This mod adds commands and keybinds that make analysing Cosmic Reach tick-by-tick easier.

## Guide

There's a video guide for Tick Manipulator 0.6.1-0.2.0 available on Youtube: https://www.youtube.com/watch?v=HW7DXemh7zw

## Keybind Information
Stepping:
- `<Page Up>` to go to step forward one tick when ticking is frozen.

## Command Information
Help:
- `/tick help` to get this help command.

Resetting:
- `/tick reset` to reset all ticking modifiers to the vanilla default (unfrozen, no delay).

Freezing:
- `/tick freeze` to freeze the game's ticking.
- `/tick unfreeze` to unfreeze the game's ticking.
- `/time freeze` to freeze the sky.
- `/time unfreeze` to unfreeze the sky.

Stepping:
- `/tick step` to step to the next tick.
- `/tick step {number of ticks}` to step the given number of ticks. Ticks are evaluated as usual,
  then paused once the given number of ticks are processed. E.g. `/tick step 5` ticks the game
  five times.

Delaying:
- `/tick delay {delay in milliseconds}` to wait the given number of milliseconds before each tick.
  E.g. `/tick delay 1000` waits one second before each tick.

> [!NOTE]
> Stepping and Delaying are mutually exclusive. Stepping can only be used when ticking is frozen,
  while delaying requires ticking to be unfrozen.

Repeating:
- `/tick repeat add {command}` to add a command to the list of commands run every tick.
  E.g. `/tick repeat add data simple entity velocity` logs the nearest entity's velocity every tick.
- `/tick repeat clear` to clear the list of commands run every tick.

Highlighting:
- `/highlight` to show a GUI with options for highlighting entities (*Defaults to false*) and particles
  (*Defaults to false*), as well as persisting particles (*Defaults to true*).

Data Querying:
- `/data` to get exhaustive data about a specific block that is in the current line of sight (up to 100
  blocks away) or the nearest entity (excluding players).
  Key-data pairs separated by a colon (e.g. `chunk : (97, 2, 34)`) represent fields.
  Key-data pairs separated by an arrow (e.g. `getCenterY() -> 32.5`) represent parameterless methods. Note that these
  methods are only run when the parent object tree node is opened. Note further that some of these methods may affect
  the world.

## Dependencies:
- Puzzle Loader
- The latest Cosmic Reach version that has been verified to work with this mod is Alpha v0.5.8.
- (Optional) [Dear ImGui Integration Mod](https://crmods.org/mod/imgui-integration)

### Build dependencies
- Java >=24 for Cosmic Reach >=v0.4.17. The version must have a decimal (ex. 24.0.1), otherwise you will get 
an IllegalStateException (specifically:
`throw new IllegalStateException("Unable to convert 'java.version' (" + jVersion + ") into a version number!");` from
quiltmc). As an example, version 21.0.0 will fail to parse and throw an error.


### Updating and Building from Source

## Updating

Run the `gradle cleanOldJigsawLocal` and `gradle cleanOldJigsawGlobal` tasks to remove outdated Jigsaw directories from the local
and global environments.

Run the `gradle transformJars` task to update the game jars.

### Testing

- Run `./gradlew runModdedClient --warning-mode all` to test the client;
- Run `./gradlew runModdedServer --warning-mode all` to test the server.

