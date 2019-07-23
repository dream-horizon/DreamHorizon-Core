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

package com.dreamhorizon.core.configuration;

import com.dreamhorizon.core.configuration.enums.CoreConfiguration;
import com.dreamhorizon.core.configuration.enums.Message;
import com.dreamhorizon.core.configuration.implementation.ConfigurationNode;
import com.dreamhorizon.core.configuration.implementation.EnumConfiguration;
import com.dreamhorizon.core.configuration.implementation.EnumConfigurationBuilder;

import java.io.File;
import java.util.HashMap;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class ConfigurationHandler {
    private static final ConfigurationHandler instance = new ConfigurationHandler();
    private final HashMap<String, EnumConfiguration> configs = new HashMap<>();
    private final File configFolder = new File("plugins" + File.separator + "DHCore" + File.separator + "settings");
    
    private ConfigurationHandler() {
        configs.put("core", new EnumConfigurationBuilder(new File(configFolder + File.separator + "config.yml"), CoreConfiguration.class).build());
        configs.put("messages", new EnumConfigurationBuilder(new File(configFolder + File.separator + "messages.yml"), Message.class).build());
    }
    
    public void addConfig(String name, Class<? extends ConfigurationNode> enumClass) {
        configs.put(name, new EnumConfigurationBuilder(new File(configFolder + File.separator + name + ".yml"), enumClass).build());
    }
    
    public File getConfigFolder() {
        return configFolder;
    }
    
    public EnumConfiguration getConfig(String configName) {
        return configs.get(configName);
    }
    
    public static ConfigurationHandler getInstance() {
        return instance;
    }
}
