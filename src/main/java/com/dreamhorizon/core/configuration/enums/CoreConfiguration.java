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

package com.dreamhorizon.core.configuration.enums;

import com.dreamhorizon.core.configuration.implementation.ConfigurationNode;
import com.dreamhorizon.core.configuration.implementation.ConfigurationSection;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public enum CoreConfiguration implements ConfigurationNode {
    VERSION_SECTION("version", new ConfigurationSection()),
    VERSION("version.version", "",
        "# This is the current version of SpigotPluginTemplate",
        "# Please do not edit this value!"
    ),
    LATEST_RAN_VERSION("version.latest_ran_version", "",
        "# This is the latest ran version of SpigotPluginTemplate",
        "# Please do not edit this value!"),
    DATABASE_SECTION("database", new ConfigurationSection(),
        " ",
        "##############################",
        "# +------------------------+ #",
        "# | Database Configuration | #",
        "# +------------------------+ #",
        "##############################",
        "# Valid DB types: mysql"
    ),
    DATABASE_TABLE_PREFIX("database.table_prefix", "DHCORE_",
        "# Database table prefix."),
    DATABASE_TYPE("database.type", "mysql",
        "# Database type."
    ),
    DATABASE_HOSTNAME("database.hostname", "localhost",
        "# Database hostname."
    ),
    DATABASE_PORT("database.port", "3306",
        "# Database port."
    ),
    DATABASE_SCHEMA_NAME("database.schema", "minecraft",
        "# Database schema."
    ),
    DATABASE_USERNAME("database.username", "root",
        "# Database username."
    ),
    DATABASE_PASSWORD("database.password", "password",
        "# Database password."
    );
    
    private final String path;
    private final Object defaultValue;
    private final String[] comments;
    
    CoreConfiguration(String path, Object defaultValue, String... comments) {
        this.path = path;
        this.defaultValue = defaultValue;
        this.comments = comments;
    }
    
    
    public String getPath() {
        return path;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public String[] getComments() {
        return comments;
    }
}
