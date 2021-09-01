package com.zenya.damageindicator.command;

import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import com.zenya.damageindicator.util.ChatBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DamageIndicatorCommand implements CommandExecutor {
    private void sendUsage(CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        chat.sendMessages("command.help");
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);

        //No command arguments
        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        //No permission
        if (!sender.hasPermission("damageindicator.command." + args[0])) {
            chat.sendMessages("no-permission");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help": {
                sendUsage(sender);
                break;
            }
            case "toggle": {
                if (!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    break;
                }
                Player player = (Player) sender;
                chat.withPlayer(player);
                if (args.length == 1) {
                    if (ToggleManager.INSTANCE.isToggled(player.getName())) {
                        ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                        chat.sendMessages("command.toggle.disable");
                    } else {
                        ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                        chat.sendMessages("command.toggle.enable");
                    }
                } else if (args.length > 1) {
                    if (args[1].equalsIgnoreCase("on")) {
                        ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                        chat.sendMessages("command.toggle.enable");
                    } else if (args[1].equalsIgnoreCase("off")) {
                        ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                        chat.sendMessages("command.toggle.disable");
                    } else {
                        //Wrong arg2 for toggle
                        sendUsage(sender);
                        return true;
                    }
                }
                break;
            }
            case "reload": {
                StorageFileManager.reloadFiles();
                HealthIndicator.INSTANCE.reload();
                chat.sendMessages("command.reload");
                break;
            }
            default: {
                sendUsage(sender);
            }
        }
        return true;
    }

}
