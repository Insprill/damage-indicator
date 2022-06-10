package com.zenya.damageindicator.command;

import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import net.insprill.xenlib.localization.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DamageIndicatorCommand implements CommandExecutor {

    private void sendUsage(CommandSender sender) {
        Lang.send(sender, "commands.help");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {

        //No command arguments
        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        //No permission
        if (!sender.hasPermission("damageindicator.command." + args[0])) {
            Lang.send(sender, "commands.no-permission");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help": {
                sendUsage(sender);
                break;
            }
            case "toggle": {
                if (!(sender instanceof Player)) {
                    Lang.send(sender, "commands.player-only");
                    break;
                }
                Player player = (Player) sender;
                if (args.length == 1) {
                    if (ToggleManager.INSTANCE.isToggled(player.getUniqueId())) {
                        ToggleManager.INSTANCE.registerToggle(player.getUniqueId(), false);
                        Lang.send(sender, "commands.toggle.disable");
                    } else {
                        ToggleManager.INSTANCE.registerToggle(player.getUniqueId(), true);
                        Lang.send(sender, "commands.toggle.enable");
                    }
                } else if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("on")) {
                        ToggleManager.INSTANCE.registerToggle(player.getUniqueId(), true);
                        Lang.send(sender, "commands.toggle.enable");
                    } else if (args[1].equalsIgnoreCase("off")) {
                        ToggleManager.INSTANCE.registerToggle(player.getUniqueId(), false);
                        Lang.send(sender, "commands.toggle.disable");
                    } else {
                        //Wrong arg2 for toggle
                        sendUsage(sender);
                        return true;
                    }
                }
                break;
            }
            case "reload": {
                StorageFileManager.INSTANCE.reloadFiles();
                HealthIndicator.INSTANCE.reload();
                Lang.send(sender, "commands.reload");
                break;
            }
            default: {
                sendUsage(sender);
            }
        }
        return true;
    }

}
