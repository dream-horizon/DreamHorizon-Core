/*
 * DreamHorizonCore
 * Copyright (C) 2019 Dream Horizon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.dreamhorizon.core.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.dreamhorizon.core.commands.implementation.DHCommand;
import org.bukkit.entity.Player;

@CommandAlias("fun")
public class FunCommand extends DHCommand {
    @Subcommand("tableflip")
    @CommandAlias("tableflip")
    public static void onTableFlip(Player player) {
        player.performCommand("me (╯°□°）╯︵ ┻━┻");
    }
    
    @Subcommand("shrug")
    @CommandAlias("shrug")
    public static void onShrug(Player player) {
        player.performCommand("me ‾\\_(ツ)_/‾");
    }
    
    @Subcommand("unflip")
    @CommandAlias("unflip")
    public static void onUnFlip(Player player) {
        player.performCommand("me ┬─┬ ノ( ゜-゜ノ)");
    }
}
