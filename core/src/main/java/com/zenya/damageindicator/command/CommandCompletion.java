package com.zenya.damageindicator.command;

import co.aikar.commands.BukkitCommandManager;

import java.util.Arrays;

public class CommandCompletion {

    public static void register(BukkitCommandManager manager) {
        manager.getCommandCompletions().registerAsyncCompletion("onOffToggle", ctx -> Arrays.asList("on", "off"));
    }

    private CommandCompletion() {
        throw new UnsupportedOperationException("Utility class");
    }

}
