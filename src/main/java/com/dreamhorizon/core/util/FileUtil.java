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

package com.dreamhorizon.core.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class FileUtil {
    private static final Logger LOGGER = LogManager.getLogger("com.dreamhorizon.core");
    
    /**
     * Creates the folder specified.
     *
     * @param folder {@link File} to be created.
     * @return {@link true} if the folder was created, false if otherwise.
     */
    public static boolean createFolder(File folder) {
        if (!folder.getParentFile().mkdirs() && !folder.getParentFile().isDirectory()) {
            LOGGER.log(Level.ERROR, "Folder " + folder.getPath() + "'s Parent folder was a file, not a folder.");
            return false;
        }
        if (!folder.exists() && !folder.mkdir()) {
            LOGGER.log(Level.ERROR, "Folder " + folder.getPath() + " couldn't be created.");
            return false;
        }
        return true;
    }
    
    /**
     * Creates the file specified.
     *
     * @param file {@link File} to be created.
     * @return {@link true} if the file was created, false if otherwise.
     */
    public static boolean createFile(File file) {
        if (!file.getParentFile().mkdirs() && !file.getParentFile().isDirectory()) {
            LOGGER.log(Level.ERROR, "File " + file.getPath() + "'s Parent folder was a file, not a folder.");
            return false;
        }
        try {
            if (!file.exists() && !file.createNewFile()) {
                LOGGER.log(Level.ERROR, "File " + file.getPath() + " couldn't be created.");
                return false;
            }
        } catch (IOException e) {
            LOGGER.log(Level.ERROR, "An unexpected error occured while creating file " + file.getPath());
            LOGGER.log(Level.ERROR, e);
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
