package com.zenya.damageindicator.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DamageIndicatorTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        List<String> matches = new ArrayList<>();

        switch (args.length) {
            case 1:
                matches.add("help");
                matches.add("toggle");
                matches.add("reload");
                return StringUtil.copyPartialMatches(args[0], matches, new ArrayList<>());

            case 2:
                switch (args[0].toLowerCase()) {
                    case "toggle":
                        matches.add("on");
                        matches.add("off");
                        return StringUtil.copyPartialMatches(args[1], matches, new ArrayList<>());
                }
        }
        return Collections.emptyList();
    }

}
