package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.Highlight.Highlight;
import com.darkmattrmaestro.tick_manipulator.PerWorldClientSingletons;
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
            case "particles": {
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
            case "entities": {
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
            case "blocks": {
                if (!this.hasNextArg()) {
                    if (PerWorldClientSingletons.blockHighlight.properties.isEmpty()) {
                        sendMsg("No highlighted blocks.");
                    } else {
                        StringBuilder str = new StringBuilder();
                        for (String key: PerWorldClientSingletons.blockHighlight.properties.keySet()) {
                            str.append(key);
                            str.append("=");
                            str.append(PerWorldClientSingletons.blockHighlight.properties.get(key));
                            str.append(",");
                        }
                        str.deleteCharAt(str.length() - 1);
                        sendMsg(str.toString());
                    }
                } else {
                    String value = this.getNextArg();
                    if ("clear".equalsIgnoreCase(value)) {
                        PerWorldClientSingletons.blockHighlight.clear();
                        break;
                    }

                    String[] blockStateStrings = value.split(",");
                    for (String blockStateStr: blockStateStrings) {
                        String[] blockStateKeyValue = blockStateStr.split("=");
                        if (blockStateKeyValue.length < 2) { continue; }

                        PerWorldClientSingletons.blockHighlight.setPropertyCondition(blockStateKeyValue[0], blockStateKeyValue[1]);
                    }
                }

                break;
            }
            default: {
                sendMsg("Incorrect argument provided! The first argument must be one of `particles`, `entities` or `blocks`.");
                break;
            }
        }
    }

    public String getShortDescription() {
        return "Highlight various things by rendering them on top.";
    }
}
