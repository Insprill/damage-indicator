package com.zenya.damageindicator.util;

import com.zenya.damageindicator.storage.StorageFileManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ChatBuilder {
    private String text;
    private Player player;
    private CommandSender sender;
    private List<String> args;

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

    public ChatBuilder withPlayer(String player) {
        this.player = Bukkit.getPlayer(player);
        return this;
    }

    public ChatBuilder withSender(CommandSender sender) {
        this.sender = sender;
        try {
            this.player = (Player) sender;
        } catch(ClassCastException exc) {
            player = null;
        }
        return this;
    }

    public <T extends Serializable> ChatBuilder withArgs(T... args) {
        this.args = new ArrayList<>();

        for(T arg : args) {
            this.args.add(arg.toString());
        }
        return this;
    }

    public String build() {
        //Placeholders
        text = text == null ? "" : ChatColor.translateAlternateColorCodes('&', text);
        text = player == null ? text : text.replaceAll("%world%", player.getWorld().getName());
        text = player == null ? text : text.replaceAll("%player%", player.getName());

        if(args != null && args.size() != 0) {
            for(int i=0; i<args.size(); i++) {
                text = text.replaceAll("%arg" + (i+1) + "%", args.get(i));
            }
        }

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
        if(text == null || text == "") return;

        if(player != null) {
            sendMessage(player);
        } else if(sender != null) {
            sendMessage(sender);
        }
    }

    public void sendMessages(String node) {
        if(StorageFileManager.getMessages().isList(node)) {
            for(String item : StorageFileManager.getMessages().getList(node)) {
                withText(item).sendMessage();
            }
        } else {
            withText(StorageFileManager.getMessages().getString(node)).sendMessage();
        }
    }

    public static String translateColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}

