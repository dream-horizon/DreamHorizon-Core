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
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.contexts.ContextResolver;
import com.dreamhorizon.core.DHCore;
import com.dreamhorizon.core.commands.implementation.DHCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    
    private PaperCommandManager manager;
    
    private CommandHandler() {
        completions.put("boolean", context -> Arrays.asList("true", "false"));
    }
    
    public void register() {
        manager = new PaperCommandManager(DHCore.getPlugin(DHCore.class));
        completions.forEach((key, context) -> manager.getCommandCompletions().registerCompletion(key, context));
        asyncCompletions.forEach((key, context) -> manager.getCommandCompletions().registerAsyncCompletion(key, context));
        contexts.forEach((clazz, resolver) -> manager.getCommandContexts().registerContext(clazz, resolver));
        commands.forEach(command -> manager.registerCommand(command));
    }
    
    public void registerCommand(DHCommand command) {
        commands.add(command);
    }
    
    public void registerCompletion(String key, CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext> completionHandler) {
        completions.put(key, completionHandler);
    }
    
    public void registerAsyncCompletion(String key, CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext> completionHandler) {
        asyncCompletions.put(key, completionHandler);
    }
    
    public void registerContext(Class toResolve, ContextResolver<?, BukkitCommandExecutionContext> resolver) {
        contexts.put(toResolve, resolver);
    }
    
    public static CommandHandler getInstance() {
        return instance;
    }
}
