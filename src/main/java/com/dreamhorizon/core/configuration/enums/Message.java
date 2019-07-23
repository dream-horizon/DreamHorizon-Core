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

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public enum Message implements ConfigurationNode {
    YES("true", "&aYes"),
    NO("false", "&cNo"),
    CORE_ENABLED("core_enabled", "DreamHorizonCore has successfully been enabled."),
    CORE_DISABLED("core_disabled", "DreamHorizonCore has successfully been disabled."),
    MODULE_LIST_HEADER("module_list_header", "&7----- &9Modules &7-----"),
    MODULE_LIST_ELEMENT("module_list_element", "&9Name: &6{moduleName} &9Author: &6{moduleAuthor} &9Enabled: &6{moduleEnabled}"),
    MODULE_LIST_FOOTER("module_list_footer", "&9Page: &7{page}&9/&7{maxPage}"),
    MODULE_INFO_1("module_info_1", "&9Name: &6{moduleName} &9Author: &6{moduleAuthor} &9Enabled: &6{moduleEnabled}"),
    MODULE_INFO_2("module_info_2", "&9Number of listeners: &6{moduleListenerAmount}"),
    MODULE_INFO_3("module_info_3", "&9Number of commands: &6{moduleCommandAmount}"),
    MODULE_ENABLE_ALREADY_ENABLED("module_enable_already_enabled", "&cModule 6{moduleName} &cis already enabled!"),
    MODULE_ENABLE_ENABLED("module_enable_enabled", "&cModule &6{moduleName} &cwas successfully enabled!"),
    MODULE_DISABLE_ALREADY_DISABLED("module_disable_already_disabled", "&cModule 6{moduleName} &cis already disabled!"),
    MODULE_DISABLE_DISABLED("module_disable_disabled", "&cModule &6{moduleName} &cwas successfully disabled!");
    
    private final String path;
    private final Object defaultValue;
    
    Message(String path, Object defaultValue) {
        this.path = path;
        this.defaultValue = defaultValue;
    }
    
    
    public String getPath() {
        return path;
    }
    
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public String[] getComments() {
        return new String[0];
    }
}
