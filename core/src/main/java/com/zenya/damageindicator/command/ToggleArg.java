package com.zenya.damageindicator.command;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.zenya.damageindicator.storage.ToggleManager;
import net.insprill.xenlib.commands.ICommandArgument;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ToggleArg implements ICommandArgument {

    @Override
    public String getBaseArg() {
        return "toggle";
    }

    @Override
    public Map<String, Boolean> getSubArgs() {
        return ImmutableMap.of("status", false);
    }

    @Override
    public String getDescription() {
        return "Toggles damage and health indicators for you only.";
    }

    @Override
    public @Nullable String getPermission() {
        return "damageindicator.command.toggle";
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public boolean process(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        Player player = (Player) sender;
        boolean newStatus = (args.length == 2)
                ? args[1].equalsIgnoreCase("on")
                : !ToggleManager.INSTANCE.isToggled(player.getUniqueId());
        ToggleManager.INSTANCE.registerToggle(player.getUniqueId(), newStatus);
        Lang.send(sender, "commands.toggle." + ((newStatus) ? "enable" : "disable"));
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return args.length == 2
                ? ImmutableList.of("on", "off")
                : Collections.emptyList();
    }

}
