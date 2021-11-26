package com.zenya.damageindicator.util;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatBuilder {

    private String text;
    private Player player;
    private CommandSender sender;

    public ChatBuilder() {
        this(null);
    }

    public ChatBuilder(String text) {
        this.text = text;
    }

    public ChatBuilder withText(String text) {
        this.text = text;
        return this;
    }

    public ChatBuilder withPlayer(Player player) {
        this.player = player;
        return this;
    }

    public ChatBuilder withSender(CommandSender sender) {
        this.sender = sender;
        this.player = (sender instanceof Player) ? (Player) sender : null;
        return this;
    }

    public String build() {
        //Placeholders
        text = text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
        text = player == null ? text : text.replaceAll("%world%", player.getWorld().getName());
        text = player == null ? text : text.replaceAll("%player%", player.getName());

        return text;
    }

    private void sendMessage(CommandSender sender) {
        this.sender = sender;
        sender.sendMessage(build());
    }

    private void sendMessage(Player player) {
        this.player = player;
        player.sendMessage(build());
    }

    public void sendMessage() {
        if (text == null || text.isEmpty())
            return;

        if (player != null) {
            sendMessage(player);
        } else if (sender != null) {
            sendMessage(sender);
        }
    }

    public void sendMessages(String node) {
        if (StorageFileManager.getMessages().isList(node)) {
            for (String item : StorageFileManager.getMessages().getList(node)) {
                withText(item).sendMessage();
            }
        } else {
            withText(StorageFileManager.getMessages().getString(node)).sendMessage();
        }
    }

}
