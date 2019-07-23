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
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.dreamhorizon.core.commands.implementation.DHCommand;
import com.dreamhorizon.core.configuration.enums.Message;
import com.dreamhorizon.core.helper.MessageHelper;
import com.dreamhorizon.core.modulation.ModuleHandler;
import com.dreamhorizon.core.modulation.implementation.ModuleEntry;
import com.dreamhorizon.core.util.PaginationUtil;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.List;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
@SuppressWarnings("unused")
@CommandAlias("module|modules|dhmodule|dhmodules")
@Description("Module administration command")
@CommandPermission("dhcore.command.modules")
public class ModuleCommand extends DHCommand {
    
    @Subcommand("list")
    @Description("Gets a list of enabled modules")
    @Syntax("(page)")
    @CommandPermission("dhcore.modules.list")
    public static void onModuleList(CommandSender sender, @Default(value = "1") int page) {
        if (page == 0) page = 1;
        List<ModuleEntry> paginatedModules = PaginationUtil.getPage(ModuleHandler.getInstance().getModuleEntries(), page, 8);
        int maxPage = (int) Math.ceil(paginatedModules.size() / 8.0);
        if (maxPage == 0) maxPage = 1;
        if (page > maxPage) page = maxPage;
        
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("page", page);
        placeholders.put("maxPage", maxPage);
        
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_LIST_HEADER));
        for (ModuleEntry paginatedModule : paginatedModules) {
            placeholders.put("moduleName", paginatedModule.getName());
            placeholders.put("moduleAuthor", paginatedModule.getAuthor());
            if (paginatedModule.getModule().isEnabled()) {
                placeholders.put("moduleEnabled", Message.YES);
            } else {
                placeholders.put("moduleEnabled", Message.NO);
            }
            sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_LIST_ELEMENT));
        }
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_LIST_FOOTER));
    }
    
    @Subcommand("info")
    @Description("Shows info about a module")
    @Syntax("[module]")
    @CommandCompletion("@modules")
    @CommandPermission("dhcore.modules.info")
    public static void onModuleInfo(CommandSender sender, ModuleEntry moduleEntry) {
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("moduleName", moduleEntry.getName());
        placeholders.put("moduleAuthor", moduleEntry.getAuthor());
        placeholders.put("moduleCommandAmount", moduleEntry.getModule().getCommands().size());
        placeholders.put("moduleListenerAmount", moduleEntry.getModule().getListeners().size());
        if (moduleEntry.getModule().isEnabled()) {
            placeholders.put("moduleEnabled", Message.YES);
        } else {
            placeholders.put("moduleEnabled", Message.NO);
        }
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_INFO_1));
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_INFO_2));
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_INFO_3));
    }
    
    @Subcommand("enable")
    @Description("Disables a module")
    @Syntax("[module]")
    @CommandCompletion("@modules")
    @CommandPermission("dhcore.modules.disable")
    public static void onModuleEnable(CommandSender sender, ModuleEntry moduleEntry) {
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("moduleName", moduleEntry.getName());
        placeholders.put("moduleAuthor", moduleEntry.getAuthor());
        placeholders.put("moduleCommandAmount", moduleEntry.getModule().getCommands().size());
        placeholders.put("moduleListenerAmount", moduleEntry.getModule().getListeners().size());
        if (moduleEntry.getModule().isEnabled()) {
            sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_ENABLE_ALREADY_ENABLED));
            return;
        }
        ModuleHandler.getInstance().enableModule(moduleEntry.getModule());
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_ENABLE_ENABLED));
    }
    
    @Subcommand("disable")
    @Description("Disables a module")
    @Syntax("[module]")
    @CommandCompletion("@modules")
    @CommandPermission("dhcore.modules.disable")
    public static void onModuleDisable(CommandSender sender, ModuleEntry moduleEntry) {
        HashMap<String, Object> placeholders = new HashMap<>();
        placeholders.put("moduleName", moduleEntry.getName());
        placeholders.put("moduleAuthor", moduleEntry.getAuthor());
        placeholders.put("moduleCommandAmount", moduleEntry.getModule().getCommands().size());
        placeholders.put("moduleListenerAmount", moduleEntry.getModule().getListeners().size());
        if (!moduleEntry.getModule().isEnabled()) {
            sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_DISABLE_ALREADY_DISABLED));
            return;
        }
        ModuleHandler.getInstance().disableModule(moduleEntry.getModule());
        sender.sendMessage(MessageHelper.formatMessage(placeholders, Message.MODULE_DISABLE_DISABLED));
    }
    
    
}
