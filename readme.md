> [!NOTE]
> Updated to Cosmic Reach Alpha v0.5.10

# Tick Manipulator
This mod adds commands and keybinds that make analysing Cosmic Reach tick-by-tick easier.

## Keybind Information
Stepping:
- `<Page Up>` to go to step forward one tick when ticking is frozen.

## Command Information
All tick commands take the general form `/tick {action} {arg1} {arg2} {...}`.

Help
- `/tick help` to get this help command.

Resetting:
- `/tick reset` to reset all ticking modifiers to the vanilla default (unfrozen, no delay).

Freezing:
- `/tick freeze` to freeze the game's ticking.
- `/tick unfreeze` to unfreeze the game's ticking.

Stepping:
- `/tick step` to step to the next tick.
- `/tick step {number of ticks}` to step the given number of ticks. Ticks are evaluated as usual,
  then paused once the given number of ticks are processed. E.g. `/tick step 5` ticks the game
  five times.

Delaying:
- `/tick delay {delay in milliseconds}` to wait the given number of milliseconds before each tick.
  E.g. `/tick delay 1000` waits one second before each tick.

Notes:
- Stepping and Delaying are mutually exclusive. Stepping can only be used when ticking is frozen,
  while delaying requires ticking to be unfrozen.

Highlighting:
- `/highlight {particles|entities} {true|false}` to make either particles or entities visible
  through blocks. E.g. `/highlight particles true` makes particles (such as laser projectile
  entities' particles) visible through blocks. *Defaults to false.*
- `/highlight persist-particles {true|false}` to make particles persist past their normal lifetime.
  This is especially useful when ticks are frozen since particle lifetimes are tick-independent. Note
  that particles are purely visual components (mostly used by projectile entities). *Defaults to true.*

Data Querying:
- `/data` to get data about the block that is in the current line of sight (up to 100 blocks away).
  Key-data pairs that are separated by a colon (e.g. `chunk : (97, 2, 34)`) represent fields.
  Key-data pairs separated by an arrow (e.g. `getCenterY -> 32.5`) represent parameterless methods.

## Dependencies:
- Puzzle Loader
- The latest Cosmic Reach version that has been verified to work with this mod is Alpha v0.5.8.

### Build dependencies
- Java >=24 for Cosmic Reach >=v0.4.17. The version must have a decimal (ex. 24.0.1), otherwise you will get 
an IllegalStateException (specifically:
`throw new IllegalStateException("Unable to convert 'java.version' (" + jVersion + ") into a version number!");` from
quiltmc). As an example, version 21.0.0 will fail to parse and throw an error.

## How to Test Client & Server for Puzzle
- For the Client you can use the `./gradlew :runClient` task (add `--warning-mode all` for more useful outputs)
- For the Server  you can use the `./gradlew :runServer` task

