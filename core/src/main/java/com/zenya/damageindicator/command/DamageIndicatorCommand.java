package com.zenya.damageindicator.command;

import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import com.zenya.damageindicator.util.ChatBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DamageIndicatorCommand implements CommandExecutor {
    private void sendUsage(CommandSender sender) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);
        chat.sendMessages("command.help");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        ChatBuilder chat = (new ChatBuilder()).withSender(sender);

        //No command arguments
        if(args.length < 1) {
            sendUsage(sender);
            return true;
        }

        //No permission
        if(!sender.hasPermission("damageindicator.command." + args[0])) {
            chat.sendMessages("no-permission");
            return true;
        }

        //help, toggle, reload
        if(args.length == 1) {
            if(args[0].toLowerCase().equals("help")) {
                sendUsage(sender);
                return true;
            }

            if(args[0].toLowerCase().equals("toggle")) {
                if(!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;
                chat.withPlayer(player);
                if(ToggleManager.INSTANCE.isToggled(player.getName())) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                    chat.sendMessages("command.toggle.disable");
                } else {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                    chat.sendMessages("command.toggle.enable");
                }
                return true;
            }

            if(args[0].toLowerCase().equals("reload")) {
                StorageFileManager.reloadFiles();
                chat.sendMessages("command.reload");
                return true;
            }

            //Wrong arg1
            sendUsage(sender);
            return true;
        }

        //toggle
        if(args.length == 2) {
            if(args[0].toLowerCase().equals("toggle")) {
                if(!(sender instanceof Player)) {
                    chat.sendMessages("player-required");
                    return true;
                }
                Player player = (Player) sender;

                if (args[1].toLowerCase().equals("on")) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), true);
                    chat.sendMessages("command.toggle.enable");
                }

                else if (args[1].toLowerCase().equals("off")) {
                    ToggleManager.INSTANCE.registerToggle(player.getName(), false);
                    chat.sendMessages("command.toggle.disable");
                }

                else {
                    //Wrong arg2 for toggle
                    sendUsage(sender);
                    return true;
                }
                return true;
            }

            //Wrong arg1
            sendUsage(sender);
            return true;
        }
        //Incorrect number of args
        sendUsage(sender);
        return true;
    }
}