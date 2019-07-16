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

package com.dreamhorizon.core.configuration.implementation.yaml;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FormatDetector;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.core.io.ConfigWriter;
import com.electronwill.nightconfig.yaml.YamlFormat;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class CustomYamlFormat implements ConfigFormat<CommentedConfig> {
    static {
        FormatDetector.registerExtension("yaml", YamlFormat::defaultInstance);
        FormatDetector.registerExtension("yml", YamlFormat::defaultInstance);
    }
    
    private final Yaml yaml;
    
    public CustomYamlFormat(Yaml yaml) {
        this.yaml = yaml;
    }
    
    @Override
    public ConfigWriter createWriter() {
        return new CustomYamlWriter(yaml);
    }
    
    @Override
    public ConfigParser<CommentedConfig> createParser() {
        return new CustomYamlParser(yaml, this);
    }
    
    @Override
    public CommentedConfig createConfig() {
        return CommentedConfig.of(this);
    }
    
    @Override
    public CommentedConfig createConcurrentConfig() {
        return CommentedConfig.ofConcurrent(this);
    }
    
    @Override
    public CommentedConfig createConfig(Supplier<Map<String, Object>> mapCreator) {
        return CommentedConfig.of(mapCreator, this);
    }
    
    @Override
    public boolean supportsComments() {
        return true;
    }
    
    @Override
    public boolean supportsType(Class<?> type) {
        return type == null
                || type.isEnum()
                || type == Boolean.class
                || type == String.class
                || type == java.util.Date.class
                || type == java.sql.Date.class
                || type == java.sql.Timestamp.class
                || type == byte[].class
                || type == Object[].class
                || Number.class.isAssignableFrom(type)
                || Set.class.isAssignableFrom(type)
                || List.class.isAssignableFrom(type)
                || Config.class.isAssignableFrom(type);
    }
    
}
