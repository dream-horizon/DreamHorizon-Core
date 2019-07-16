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

import com.dreamhorizon.core.configuration.implementation.yaml.CustomYamlFormat;
import com.dreamhorizon.core.util.FileUtil;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class EnumConfiguration {
    private static final Logger LOGGER = LogManager.getLogger("com.dreamhorizon.core");
    private final File saveFile;
    private final List<ConfigurationNode> configurationNodes;
    private CommentedConfig commentedConfig;
    
    EnumConfiguration(File loadFile, File saveFile, List<ConfigurationNode> configurationNodes) {
        this.saveFile = saveFile;
        this.configurationNodes = configurationNodes;
        if (!FileUtil.createFile(loadFile)) {
            LOGGER.log(Level.ERROR, "[Config] Config file " + loadFile.getPath() + " couldn't be created.");
            return;
        }
        if (!FileUtil.createFile(saveFile)) {
            LOGGER.log(Level.ERROR, "[Config] Config file " + saveFile.getPath() + " couldn't be created.");
            return;
        }
        FileConfig oldConfiguration = FileConfig.of(loadFile);
        if (loadFile.length() != 0) {
            oldConfiguration.load();
        }
        
        ConfigFormat<?> newConfigurationFormat;
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        dumperOptions.setIndent(4);
        dumperOptions.setWidth(10000);
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setLineBreak(DumperOptions.LineBreak.getPlatformLineBreak());
        newConfigurationFormat = new CustomYamlFormat(new Yaml(dumperOptions));
        
        CommentedConfig newConfiguration = CommentedConfig.wrap(new LinkedHashMap<>(), newConfigurationFormat);
        buildConfig(oldConfiguration, newConfiguration);
        
    }
    
    private void buildConfig(Config oldConfiguration, CommentedConfig newConfiguration) {
        for (ConfigurationNode configurationNode : configurationNodes) {
            if (configurationNode.getComments().length > 0) {
                if (configurationNode.getComments().length == 1) {
                    if (!configurationNode.getComments()[0].isEmpty()) {
                        newConfiguration.setComment(configurationNode.getPath(), getOneCommentString(configurationNode.getComments()));
                    }
                } else {
                    newConfiguration.setComment(configurationNode.getPath(), getOneCommentString(configurationNode.getComments()));
                }
            }
            if (oldConfiguration.get(configurationNode.getPath()) != null) {
                if (oldConfiguration.get(configurationNode.getPath()) instanceof Config) {
                    newConfiguration.set(configurationNode.getPath(), CommentedConfig.wrap(new LinkedHashMap<>(), newConfiguration.configFormat()));
                } else {
                    newConfiguration.set(configurationNode.getPath(), oldConfiguration.get(configurationNode.getPath()));
                }
            } else {
                if (configurationNode.getDefaultValue() instanceof ConfigurationSection) {
                    newConfiguration.set(configurationNode.getPath(), CommentedConfig.wrap(new LinkedHashMap<>(), newConfiguration.configFormat()));
                } else {
                    newConfiguration.set(configurationNode.getPath(), configurationNode.getDefaultValue());
                }
            }
        }
        commentedConfig = newConfiguration;
        save();
    }
    
    private void save() {
        commentedConfig.configFormat().createWriter().write(commentedConfig.checked(), saveFile, WritingMode.REPLACE);
    }
    
    private String getOneCommentString(String[] comments) {
        StringBuilder commentString = new StringBuilder();
        int j = 0;
        for (String comment : comments) {
            if (!comment.startsWith("#")) {
                if (comment.length() > 1 && comment.trim().length() > 1) {
                    comment = "#" + comment;
                }
            }
            commentString.append(comment);
            j = j + 1;
            if (j != (comments.length)) {
                commentString.append(System.getProperty("line.separator")).append("\uE000");
            }
        }
        return commentString.toString();
    }
    
    public Object get(ConfigurationNode node) {
        return get(node.getPath());
    }
    
    private Object get(String path) {
        return commentedConfig.get(path);
    }
    
    public void set(ConfigurationNode node, Object o) {
        set(node.getPath(), o);
    }
    
    private void set(String path, Object o) {
        commentedConfig.set(path, o);
        save();
    }
}
