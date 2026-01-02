/*
 *     Damage Indicator
 *     Copyright (C) 2021  Zenya
 *     Copyright (C) 2021-2022  Pierce Thompson
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        if (Lang.getLocaleConfig().isModifiable()) {
            Lang.getLocaleConfig().reload();
        }
        Lang.send(sender, "commands.reload");
        return true;
    }

    @Override
    public @Nullable List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return Collections.emptyList();
    }

}
