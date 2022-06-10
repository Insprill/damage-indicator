package com.zenya.damageindicator.command;

import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import net.insprill.xenlib.commands.ICommandArgument;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ReloadArg implements ICommandArgument {

    @Override
    public String getBaseArg() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reloads the plugins configuration";
    }

    @Override
    public @Nullable String getPermission() {
        return "damageindicator.command.reload";
    }

    @Override
    public boolean process(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        StorageFileManager.INSTANCE.reloadFiles();
        HealthIndicator.INSTANCE.reload();
        Lang.send(sender, "commands.reload");
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }

}
