package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import com.darkmattrmaestro.tick_manipulator.utils.EntitySelectionUtil;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandTargetData extends Command {
    public static final String indent = "    ";

    /**
     * Return the String that represents the given indent level.
     *
     * @param level the current level of indentation.
     * @return the String that represents the given indent level.
     */
    public static String getIndent(int level) {
        return indent.repeat(level);
    }

    /**
     *
     * @param msg the StringBuilder to which to append.
     * @param resStr the response of querying the given object, converted to a string.
     * @param propName the name of the property.
     * @param indentLevel the current indent level, where 0 is no indent.
     * @param type the type of property. E.g. `field`, `method`.
     */
    public void appendObjProp(StringBuilder msg, String resStr, String propName, String separator, int indentLevel, String type) {
        msg.append(String.format("\n%s", getIndent(indentLevel)));
        if (resStr != null) {
            String[] res = String.valueOf(resStr).split("\n");
            msg.append(String.format("%s %s ", propName, separator));
            if (res.length < 2) {
                msg.append(res[0]);
            } else {
                for (String resLine : res) {
                    msg.append(String.format("\n%s%s", getIndent(indentLevel + 1), resLine));
                }
            }
        } else {
            msg.append(String.format("%s :\\ No access to %s!", propName, type));
        }
    }

    /**
     * Return a string of what is to be logged for a given object. The first character is a carriage return.
     *
     * @param obj the object to log.
     * @param indentLevel the current indent level, where 0 is no indent.
     * @param targetPropertyNames a HashSet of the names of each property (field or method) to be logged, or null if no
     *                            filter is to be applied.
     * @param detailed `true` if the logs should be detailed, `false` otherwise.
     * @return The string of what is to be logged.
     * @param <T> the object's class.
     */
    public <T> String logData(T obj, int indentLevel, HashSet<String> targetPropertyNames, boolean detailed) {
        StringBuilder msg = new StringBuilder();
        Class clazz = obj.getClass();
        while (clazz != Object.class) {
            int subIndentLevel = indentLevel + (detailed ? 1 : 0);
            if (detailed) { msg.append(String.format("\n%s%s : {", getIndent(indentLevel), clazz.getName())); }

            for (Field field : clazz.getDeclaredFields()) {
                if (targetPropertyNames != null && !targetPropertyNames.contains(field.getName().toLowerCase())) { continue; }

                String res = null;
                try {
                    field.setAccessible(true);
                    res = String.valueOf(field.get(obj));
                } catch (IllegalAccessException _) {
                }
                appendObjProp(msg, res, field.getName(), ":", subIndentLevel, "field");
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (targetPropertyNames != null && !targetPropertyNames.contains(method.getName().toLowerCase())) { continue; }
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }

                String res = null;
                try {
                    method.setAccessible(true);
                    res = String.valueOf(method.invoke(obj));
                } catch (IllegalAccessException | InvocationTargetException _) { }
                appendObjProp(msg, res, method.getName(), "->", subIndentLevel, "method");
            }
            if (detailed) {
                msg.append(String.format("\n%s}", getIndent(indentLevel)));
            }

            clazz = clazz.getSuperclass();
        }

        return msg.toString();
    }

    /**
     * Log the data of a given object through this mod's logger and through the game's chat.
     *
     * @param obj the object to log.
     * @param name the name of the category of search. E.g. `entity`, `block`.
     * @param targetPropertyNames A HashSet of the names of each property (field or method) to be logged, or null if no
     *                            filter is to be applied.
     * @param detailed `true` if the logs should be detailed, `false` otherwise.
     * @param <T> the object's class.
     */
    public <T> void logDataOf(T obj, String name, HashSet<String> targetPropertyNames, boolean detailed) {
        if (obj == null) {
            Constants.LOGGER.info("No {} found!", name);
            sendMsg("No " + name + " found!");
            return;
        }

        String logDataStr;
        logDataStr = logData(obj, detailed ? 1 : 0, targetPropertyNames, detailed);

        if (detailed) {
            Constants.LOGGER.info("Data of {} : {{}\n}", obj, logDataStr);
            sendMsg(String.format("Data of %s : {%s\n}", obj, logDataStr));
        } else {
            String[] clazzNameFull = obj.getClass().getName().split("\\.");
            String clazzName = clazzNameFull[clazzNameFull.length - 1];
            Constants.LOGGER.info("Data of {}@{} :{}", clazzName, Integer.toHexString(obj.hashCode()), logDataStr);
            sendMsg(String.format("Data of %s@%s :%s", clazzName, Integer.toHexString(obj.hashCode()), logDataStr));
        }
    }

    public void run(IChat chat) {
        super.run(chat);

        if (!this.hasNextArg()) {
            logDataOf(BlockSelectionUtil.getBlockLookingAtFar(100), "block", null, true);
        } else {
            String targetType = this.getNextArg().toLowerCase();

            boolean detailed = true;
            if ("simple".equals(targetType)) {
                detailed = false;
                targetType = this.getNextArg().toLowerCase();
            }

            HashSet<String> targetPropertyNames = null;
            if (this.hasNextArg()) {
                targetPropertyNames = new HashSet<String>(List.of(this.getNextArg().toLowerCase().split(",")));
            }

            if ("block".equals(targetType)) {
                logDataOf(BlockSelectionUtil.getBlockLookingAtFar(100), "block", targetPropertyNames, detailed);
            } else if ("entity".equals(targetType)) {
                logDataOf(EntitySelectionUtil.getNearestEntityToPlayer(), "entity", targetPropertyNames, detailed);
            } else {
                sendMsg("Invalid command arguments.");
            }
        }
    }

    public String getShortDescription() {
        return "Get information about the block that is currently being looked at.";
    }
}
