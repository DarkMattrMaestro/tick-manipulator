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

import static com.darkmattrmaestro.tick_manipulator.utils.ChatUtils.sendMsg;

public class CommandTargetData extends Command {
    public static final String indent = "    ";
    public static String getIndent(int level) {
        return indent.repeat(level);
    }

    public <T> String logData(T obj, int indentLevel) {
        StringBuilder msg = new StringBuilder();
        Class clazz = obj.getClass();
        while (clazz != Object.class) {
            msg.append(String.format("%s%s : {", getIndent(indentLevel), clazz.getName()));
            for (Field field: clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    msg.append(String.format("\n%s%s : %s", getIndent(indentLevel + 1), field.getName(), field.get(obj)));
                } catch (IllegalAccessException e) {
                    msg.append(String.format("\n%s%s :\\ No access to field!", getIndent(indentLevel + 1), field.getName()));
                }
            }
            for (Method method: clazz.getDeclaredMethods()) {
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }

                try {
                    method.setAccessible(true);
                    msg.append(String.format("\n%s%s -> %s", getIndent(indentLevel + 1), method.getName(), method.invoke(obj)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            msg.append(String.format("\n%s}", getIndent(indentLevel)));

            clazz = clazz.getSuperclass();
        }

        return msg.toString();
    }

    public <T> String logData(T obj, int indentLevel, String targetPropertyName) {
        StringBuilder msg = new StringBuilder();
        Class clazz = obj.getClass();
        while (clazz != Object.class) {
            msg.append(String.format("\n%s%s : {", getIndent(indentLevel), clazz.getName()));
            for (Field field: clazz.getDeclaredFields()) {
                if (targetPropertyName != null && !field.getName().equals(targetPropertyName)) { continue; }

                try {
                    field.setAccessible(true);
                    msg.append(String.format("\n%s%s : %s", getIndent(indentLevel + 1), field.getName(), field.get(obj)));
                } catch (IllegalAccessException e) {
                    msg.append(String.format("\n%s%s :\\ No access to field!", getIndent(indentLevel + 1), field.getName()));
                }
            }
            for (Method method: clazz.getDeclaredMethods()) {
                if (targetPropertyName != null && !method.getName().equals(targetPropertyName)) { continue; }
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) { continue; }

                try {
                    method.setAccessible(true);
                    msg.append(String.format("\n%s%s -> %s", getIndent(indentLevel + 1), method.getName(), method.invoke(obj)));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            msg.append(String.format("\n%s}", getIndent(indentLevel)));

            clazz = clazz.getSuperclass();
        }

        return msg.toString();
    }

    public <T> void logDataOf(T obj, String name, String targetPropertyName) {
        if (obj == null) {
            Constants.LOGGER.info("No {} found!", name);
            sendMsg("No " + name + " found!");
            return;
        }

        String logDataStr;
        if (targetPropertyName != null) { logDataStr = logData(obj, 1, targetPropertyName); }
        else { logDataStr = logData(obj, 1); }

        Constants.LOGGER.info(" \nData of {} :\n{}\n}", obj, logDataStr);
        sendMsg(String.format("\nData of %s :\n%s\n}", obj, logDataStr));
    }

    public void run(IChat chat) {
        super.run(chat);

        if (!this.hasNextArg()) {
            logDataOf(BlockSelectionUtil.getBlockLookingAtFar(100), "block", null);
        } else {
            String targetType = this.getNextArg();
            String targetPropertyName = null;
            if (this.hasNextArg()) {
                targetPropertyName = this.getNextArg().toLowerCase();
            }

            if ("block".equals(targetType)) {
                logDataOf(BlockSelectionUtil.getBlockLookingAtFar(100), "block", targetPropertyName);
            } else if ("entity".equals(targetType)) {
                logDataOf(EntitySelectionUtil.getNearestEntityToPlayer(), "entity", targetPropertyName);
            } else {
                sendMsg("Invalid command arguments.");
            }
        }
    }

    public String getShortDescription() {
        return "Get information about the block that is currently being looked at.";
    }
}
