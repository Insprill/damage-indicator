package com.zenya.damageindicator.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.zenya.damageindicator.scoreboard.HealthIndicator;
import com.zenya.damageindicator.storage.StorageFileManager;
import com.zenya.damageindicator.storage.ToggleManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("damageindicator|di")
@Description("Base command for Damage Indicator.")
public class DiCommand extends BaseCommand {

    @HelpCommand
    @Syntax("{@@cjm.command.help.syntax}")
    @CommandPermission("cjm.command.help")
    @Description("{@@cjm.command.help.description}")
    @SuppressWarnings("UNUSED_PARAMETER")
    public void onHelp(CommandSender sender, CommandHelp help) {
        help.showHelp();
    }

    @Subcommand("reload")
    @Description("Reloads the plugins configuration files")
    @CommandPermission("damageindicator.command.reload")
    public void onReload(CommandSender sender) {
        StorageFileManager.INSTANCE.reloadFiles();
        HealthIndicator.INSTANCE.reload();
        sender.sendMessage("Reloaded");
    }

    @Subcommand("toggle")
    @Description("Toggles whether you see indicators")
    @CommandPermission("damageindicator.command.toggle")
    public void onToggle(Player sender, @Optional String toggle) {
        boolean newStatus = (toggle != null)
                ? toggle.equalsIgnoreCase("on")
                : !ToggleManager.INSTANCE.isToggled(sender.getUniqueId());
        ToggleManager.INSTANCE.registerToggle(sender.getUniqueId(), newStatus);
    }

}
