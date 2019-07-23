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

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.contexts.ContextResolver;
import com.dreamhorizon.core.DHCore;
import com.dreamhorizon.core.commands.implementation.DHCommand;
import com.dreamhorizon.core.modulation.ModuleHandler;
import com.dreamhorizon.core.modulation.implementation.ModuleEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class CommandHandler {
    private static final CommandHandler instance = new CommandHandler();
    private final List<DHCommand> commands = new ArrayList<>();
    private final HashMap<String, CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext>> completions = new HashMap<>();
    private final HashMap<String, CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>> asyncCompletions = new HashMap<>();
    private final HashMap<Class, ContextResolver<?, BukkitCommandExecutionContext>> contexts = new HashMap<>();
    
    private PaperCommandManager manager = null;
    
    private CommandHandler() {
        completions.put("boolean", context -> Arrays.asList("true", "false"));
        commands.add(new ModuleCommand());
    }
    
    @SuppressWarnings("unchecked")
    public void register() {
        // Add modules here or else we would have loaded them too early above.
        completions.put("modules", context -> ModuleHandler.getInstance().getModuleEntries().stream().map(ModuleEntry::getName).collect(Collectors.toList()));
        contexts.put(ModuleEntry.class, context -> {
            String name = context.popFirstArg();
            ModuleEntry module = ModuleHandler.getInstance().getModuleEntry(name);
            if (module != null) {
                return module;
            }
            throw new InvalidCommandArgument("Could not find a module with that name!");
        });
        
        // register to PaperCommandManager.
        manager = new PaperCommandManager(DHCore.getPlugin(DHCore.class));
        completions.forEach((key, context) -> manager.getCommandCompletions().registerCompletion(key, context));
        asyncCompletions.forEach((key, context) -> manager.getCommandCompletions().registerAsyncCompletion(key, context));
        contexts.forEach((clazz, resolver) -> manager.getCommandContexts().registerContext(clazz, resolver));
        commands.forEach(command -> manager.registerCommand(command));
    }
    
    public void unregister() {
        manager.unregisterCommands();
    }
    
    public void registerCommand(DHCommand command) {
        commands.add(command);
        if (manager != null) {
            manager.registerCommand(command);
        }
    }
    
    public void registerCompletion(String key, CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> completionHandler) {
        completions.put(key, completionHandler);
        if (manager != null) {
            manager.getCommandCompletions().registerCompletion(key, completionHandler);
        }
    }
    
    public void registerAsyncCompletion(String key, CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> completionHandler) {
        asyncCompletions.put(key, completionHandler);
        if (manager != null) {
            manager.getCommandCompletions().registerAsyncCompletion(key, completionHandler);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void registerContext(Class toResolve, ContextResolver<?, BukkitCommandExecutionContext> resolver) {
        contexts.put(toResolve, resolver);
        if (manager != null) {
            manager.getCommandContexts().registerContext(toResolve, resolver);
        }
    }
    
    public void unregisterCommand(DHCommand command) {
        commands.remove(command);
        if (manager != null) {
            manager.unregisterCommand(command);
        }
    }
    
    public static CommandHandler getInstance() {
        return instance;
    }
}
