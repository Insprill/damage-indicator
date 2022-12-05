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

package com.zenya.damageindicator.scoreboard;

import com.zenya.damageindicator.storage.StorageFileManager;
import net.insprill.spigotutils.MinecraftVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class HealthIndicator {

    private static final String OBJECTIVE_NAME = "di-health";
    private static final String OBJECTIVE_CRITERIA = "health";
    private static final String OBJECTIVE_NAME_KEY = "health";

    public static final HealthIndicator INSTANCE = new HealthIndicator();

    private final Scoreboard board;
    private Objective obj;

    private HealthIndicator() {
        this.board = Bukkit.getScoreboardManager().getMainScoreboard();
        this.obj = board.getObjective(OBJECTIVE_NAME);
    }

    public void updateHealth(Player player) {
        if (obj != null)
            obj.getScore(player.getName()).setScore((int) player.getHealth());
    }

    @SuppressWarnings("deprecation")
    public void reload() {
        if (!StorageFileManager.getConfig().getBool("health-indicators")) {
            //Disable health indicators
            if (obj != null) {
                obj.unregister();
                obj = null;
            }
            return;
        }

        //Enable health indicators
        if (obj == null) {
            //Init if not exists
            if (MinecraftVersion.isNew()) {
                obj = board.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA, Lang.get(OBJECTIVE_NAME_KEY));
            } else {
                obj = board.registerNewObjective(OBJECTIVE_NAME, OBJECTIVE_CRITERIA);
            }
            obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
        }

        //Update displayname regardless
        obj.setDisplayName(Lang.get(OBJECTIVE_NAME_KEY));
        Bukkit.getOnlinePlayers().forEach(this::updateHealth);
    }

}
