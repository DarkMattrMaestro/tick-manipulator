package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import com.darkmattrmaestro.tick_manipulator.utils.EntitySelectionUtil;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;
import finalforeach.cosmicreach.entities.Entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandTargetData extends Command {
    public static final String indent = "    ";
    public static String getIndent(int level) {
        return indent.repeat(level);
    }

    public void appendObjProp(StringBuilder msg, String resStr, String propName, int indentLevel, String type) {
        msg.append(String.format("\n%s", getIndent(indentLevel)));
        if (resStr != null) {
            String[] res = String.valueOf(resStr).split("\n");
            msg.append(String.format("%s -> ", propName));
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
                appendObjProp(msg, res, field.getName(), subIndentLevel, "field");
            }
            for (Method method : clazz.getDeclaredMethods()) {
                if (targetPropertyNames != null && !targetPropertyNames.contains(method.getName().toLowerCase())) { continue; }
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }

                String res = null;
                try {
                    method.setAccessible(true);
                    res = String.valueOf(method.invoke(obj));
                } catch (IllegalAccessException | InvocationTargetException _) { }
                appendObjProp(msg, res, method.getName(), subIndentLevel, "method");
            }
            if (detailed) {
                msg.append(String.format("\n%s}", getIndent(indentLevel)));
            }

            clazz = clazz.getSuperclass();
        }

        return msg.toString();
    }

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
