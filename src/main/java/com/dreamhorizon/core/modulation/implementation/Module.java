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

package com.dreamhorizon.core.modulation.implementation;

import co.aikar.commands.BukkitCommandCompletionContext;
import co.aikar.commands.BukkitCommandExecutionContext;
import co.aikar.commands.CommandCompletions;
import co.aikar.commands.contexts.ContextResolver;
import com.dreamhorizon.core.commands.implementation.DHCommand;
import com.dreamhorizon.core.configuration.implementation.ConfigurationNode;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Map;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public abstract class Module {
    public abstract List<Listener> getListeners();
    
    public abstract List<DHCommand> getCommands();
    
    public void onLoad() {
    
    }
    
    public abstract void onEnable();
    
    public abstract void onDisable();
    
    public abstract Map<String, Class<? extends ConfigurationNode>> getModuleConfigNodes();
    
    public String getSchemaResourcesPath() {
        return null;
    }
    
    public List<String> getSchemaProperties() {
        return null;
    }
    
    public Map<Class, ContextResolver<?, BukkitCommandExecutionContext>> getCommandContexts() {
        return null;
    }
    
    public Map<String, CommandCompletions.CommandCompletionHandler<BukkitCommandCompletionContext>> getSyncCommandCompletions() {
        return null;
    }
    
    public Map<String, CommandCompletions.AsyncCommandCompletionHandler<BukkitCommandCompletionContext>> getAsyncCommandCompletions() {
        return null;
    }
}
