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

package com.dreamhorizon.core.configuration.implementation;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class EnumConfigurationBuilder {
    private final File loadFile;
    private final File saveFile;
    private final List<ConfigurationNode> configurationNodes;
    
    public EnumConfigurationBuilder(File file, Class<? extends ConfigurationNode> enumClass) {
        this.loadFile = file;
        this.saveFile = file;
        this.configurationNodes = new LinkedList<>(Arrays.asList(enumClass.getEnumConstants()));
    }

//    public <T extends Enum<T> & ConfigurationNode> EnumConfigurationBuilder withConfigurationNodes(Class<T> enumClass) {
//        this.configurationNodes = new LinkedList<>(Arrays.asList(enumClass.getEnumConstants()));
//        return this;
//    }
//
//    public EnumConfigurationBuilder withLoadFile(File file) {
//        this.loadFile = file;
//        return this;
//    }
//
//    public EnumConfigurationBuilder withSaveFile(File file) {
//        this.saveFile = file;
//        return this;
//    }
    
    public EnumConfiguration build() {
        return new EnumConfiguration(loadFile, saveFile, configurationNodes);
    }
}