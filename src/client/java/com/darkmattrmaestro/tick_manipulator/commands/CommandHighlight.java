package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.Highlight;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandHighlight extends Command {

    public void run(IChat chat) {
        super.run(chat);

        if (!this.hasNextArg()) {
            sendMsg("No arguments provided! Try `/highlight {particles|entities} {true|false}`.");
            return;
        }

        String action = this.getNextArg().toLowerCase();
        switch (action) {
            case "particle": {
                if (!this.hasNextArg()) {
                    sendMsg("Particles highlighted: " + Highlight.highlightParticles);
                    break;
                }
                String value = this.getNextArg().toLowerCase();
                if (Constants.POSITIVES.contains(value)) {
                    Highlight.highlightParticles = true;
                    sendMsg("Set particle highlighting to true.");
                } else if (Constants.NEGATIVES.contains(value)) {
                    Highlight.highlightParticles = false;
                    sendMsg("Set particle highlighting to false.");
                } else {
                    sendMsg("Unrecognized argument!");
                }
                break;
            }
            case "entity": {
                if (!this.hasNextArg()) {
                    sendMsg("Entities highlighted: " + Highlight.highlightEntities);
                    break;
                }
                String value = this.getNextArg().toLowerCase();
                if (Constants.POSITIVES.contains(value)) {
                    Highlight.highlightEntities = true;
                    sendMsg("Set entity highlighting to true.");
                } else if (Constants.NEGATIVES.contains(value)) {
                    Highlight.highlightEntities = false;
                    sendMsg("Set entity highlighting to false.");
                } else {
                    sendMsg("Unrecognized argument!");
                }
                break;
            }
            case "persist-particles": {
                if (!this.hasNextArg()) {
                    sendMsg("persist-particles: " + Highlight.persistParticles);
                    break;
                }
                String value = this.getNextArg().toLowerCase();
                if (Constants.POSITIVES.contains(value)) {
//                    ClientSingletons.get().getLocalPlayer().getZone().forEachEntity((Entity e) -> {
//                        IEntityModelInstance modelInstance = e.modelInstance;
//                        if (modelInstance instanceof IParticleSystemContainer c) {
//                            Constants.LOGGER.warn("Found particle");
//                            Array.ArrayIterator var5 = c.getParticleSystems().iterator();
//
//                            while(var5.hasNext()) {
//                                GameParticleSystem p = (GameParticleSystem)var5.next();
//
//                                p.clearComponents();
//
////                                int l = p.particleData.size;
////                                for(int i = l - GameParticleSystem.TOTAL_IDX; i >= 0; i -= GameParticleSystem.TOTAL_IDX) {
////                                    FloatArray var10000 = p.particleData;
////                                    var10000.size += GameParticleSystem.TOTAL_IDX;
////                                    ((IMixinGameParticleSystem) p).swapParticlesPublic(i, p.particleData.size - GameParticleSystem.TOTAL_IDX);
////                                }
//
////                                ((IMixinGameParticleSystem) p).setMaxAge(Float.POSITIVE_INFINITY);
//                            }
//                        }
//                    });

                    Highlight.persistParticles = true;
                    sendMsg("Set persist-particles to true.");
                } else if (Constants.NEGATIVES.contains(value)) {
//                    ClientSingletons.get().getLocalPlayer().getZone().forEachEntity((Entity e) -> {
//                        IEntityModelInstance modelInstance = e.modelInstance;
//                        if (modelInstance instanceof IParticleSystemContainer c) {
//                            Array.ArrayIterator var5 = c.getParticleSystems().iterator();
//
//                            while(var5.hasNext()) {
//                                GameParticleSystem p = (GameParticleSystem)var5.next();
//                                ((IMixinGameParticleSystem) p).setMaxAge(5.0F);
//                            }
//                        }
//                    });

                    Highlight.persistParticles = false;
                    sendMsg("Set persist-particles to false.");
                } else {
                    sendMsg("Unrecognized argument!");
                }
                break;
            }
            default: {
                sendMsg("Incorrect argument provided! The first argument must be one of `particle` or `entity`.");
                break;
            }
        }
    }

    public String getShortDescription() {
        return "Many utilities relating to ticks. Type '/tick help' for more information.";
    }
}
