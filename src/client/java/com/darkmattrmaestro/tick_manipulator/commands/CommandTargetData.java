package com.darkmattrmaestro.tick_manipulator.commands;

import com.darkmattrmaestro.tick_manipulator.Constants;
import com.darkmattrmaestro.tick_manipulator.utils.BlockSelectionUtil;
import finalforeach.cosmicreach.blocks.BlockPosition;
import finalforeach.cosmicreach.chat.IChat;
import finalforeach.cosmicreach.chat.commands.Command;

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
                if (method.getParameterCount() != 0 || method.getReturnType().equals(Void.TYPE)) {
                    continue;
                }

                try {
                    msg.append(String.format("\n%s%s -> %s", getIndent(indentLevel + 1), method.getName(), method.invoke(obj)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
            msg.append(String.format("\n%s}", getIndent(indentLevel)));

            clazz = clazz.getSuperclass();
        }

        return msg.toString();
    }

    public void run(IChat chat) {
        super.run(chat);

        if (!this.hasNextArg()) {
            BlockPosition blockPos = BlockSelectionUtil.getBlockLookingAtFar(100);
            if (blockPos == null) {
                Constants.LOGGER.info("No block found!");
                return;
            }

            String logDataStr = logData(blockPos, 1);
            Constants.LOGGER.info(" \nData of {} :\n{}\n}", blockPos, logDataStr);
            sendMsg(String.format("\nData of %s :\n%s\n}", blockPos, logDataStr));
        } else {
            sendMsg("Invalid command arguments.");
        }
    }

    public String getShortDescription() {
        return "Get information about the block that is currently being looked at.";
    }
}
