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

package com.dreamhorizon.core;

import com.dreamhorizon.core.commands.CommandHandler;
import com.dreamhorizon.core.configuration.ConfigurationHandler;
import com.dreamhorizon.core.configuration.enums.CoreConfiguration;
import com.dreamhorizon.core.configuration.enums.Message;
import com.dreamhorizon.core.database.DatabaseHandler;
import com.dreamhorizon.core.logging.LoggingHandler;
import com.dreamhorizon.core.modulation.ModuleHandler;
import com.dreamhorizon.core.tasks.GlobalPlaceHolderTask;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public final class DHCore extends JavaPlugin {
    private final LoggingHandler loggingHandler = LoggingHandler.getInstance();
    private final ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
    private CommandHandler commandHandler;
    private ModuleHandler moduleHandler;
    private DatabaseHandler databaseHandler;
    
    @Override
    public void onLoad() {
        super.onLoad();
        // Update version
        ConfigurationHandler.getInstance().getConfig("core").set(CoreConfiguration.VERSION, this.getDescription().getVersion());
        commandHandler = CommandHandler.getInstance();
        // Modules before Database becauses database needs a list of objects.
        moduleHandler = ModuleHandler.getInstance();
        // database does some nice stuff.
        databaseHandler = DatabaseHandler.getInstance();
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        // Essential Handlers.
        // Open DB Connection.
        if (databaseHandler != null) {
            databaseHandler.open();
        }
        // Commands
        commandHandler.register();
        // Listeners
        registerListeners();
        // Start the UpdateGlobalPlaceHolders runnable, which will just update our global placeholders every minute.
        new GlobalPlaceHolderTask().runTaskTimer(this, 0, 6000L);
        
        getLogger().info((String) ConfigurationHandler.getInstance().getConfig("messages").get(Message.CORE_ENABLED));
        // Finally, since everything is setup let the modules do their thing.
        moduleHandler.enableModules();
    }
    
    private void registerListeners() {
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        commandHandler.unregister();
        moduleHandler.disableModules();
        // Close DatabaseHandler
        if (databaseHandler != null) {
            databaseHandler.close();
        }
        getLogger().info((String) ConfigurationHandler.getInstance().getConfig("messages").get(Message.CORE_DISABLED));
    }
    
    @SuppressWarnings("unused")
    public LoggingHandler getLoggingHandler() {
        return loggingHandler;
    }
    
    @SuppressWarnings("unused")
    public ConfigurationHandler getConfigurationHandler() {
        return configurationHandler;
    }
    
    @SuppressWarnings("unused")
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    
    @SuppressWarnings("unused")
    public ModuleHandler getModuleHandler() {
        return moduleHandler;
    }
    
    @SuppressWarnings("unused")
    public DatabaseHandler getDatabaseHandler() {
        return databaseHandler;
    }
    
    
}
