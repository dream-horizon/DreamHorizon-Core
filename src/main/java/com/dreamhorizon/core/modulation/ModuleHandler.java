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

package com.dreamhorizon.core.modulation;

import com.dreamhorizon.core.DHCore;
import com.dreamhorizon.core.configuration.ConfigurationHandler;
import com.dreamhorizon.core.modulation.implementation.Module;
import com.dreamhorizon.core.modulation.implementation.ModuleEntry;
import com.dreamhorizon.core.modulation.implementation.ModuleInfo;
import com.dreamhorizon.core.util.FileUtil;
import io.github.classgraph.AnnotationInfo;
import io.github.classgraph.AnnotationParameterValueList;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class ModuleHandler {
    private static final Logger LOGGER = LogManager.getLogger("com.dreamhorizon.core");
    private static ModuleHandler instance;
    private final File moduleFolder = new File("plugins" + File.separator + "DHCore" + File.separator + "modules");
    private final HashMap<String, ModuleEntry> moduleEntries = new HashMap<>();
    
    private ModuleHandler() {
        // Get the list of module jars.
        if (!FileUtil.createFolder(moduleFolder)) {
            LOGGER.log(Level.ERROR, "[Module] Module folder couldn't be created.");
            LOGGER.log(Level.ERROR, "[Module] Module loading has been disabled.");
            return;
        }
        
        File[] moduleJars = moduleFolder.listFiles(((dir, name) -> name.endsWith(".jar")));
        // If the array is null or empty, no modules were found.
        if (moduleJars == null || moduleJars.length == 0) {
            LOGGER.log(Level.INFO, "[Module] No modules were found.");
            return;
        }
        // Import ever moduleJar.
        for (File moduleJar : moduleJars) {
            // Get ClassLoader
            URLClassLoader classLoader = (URLClassLoader) DHCore.class.getClassLoader();
            // Load module with plugin's classloader.
            try {
                URL url = moduleJar.toURI().toURL();
                // Spigot may have already loaded the JAR, just to make sure we have this check.
                if (Arrays.asList(classLoader.getURLs()).contains(url)) {
                    continue;
                }
                // Add JAR to ClassLoader
                Method method = classLoader.getClass().getSuperclass().getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(classLoader, url);
                
                // Use ClassGraph to get the newly loaded packages and search for the module's main class.
                ScanResult scanResult = new ClassGraph()
                        .enableAnnotationInfo()
                        .disableRuntimeInvisibleAnnotations()
                        .disableDirScanning()
                        .disableModuleScanning()
                        .disableNestedJarScanning()
                        .whitelistJars(moduleJar.getName())
                        .scan();
                ClassInfoList classInfoList = scanResult.getClassesWithAnnotation(ModuleInfo.class.getCanonicalName());
                if (classInfoList == null || classInfoList.isEmpty()) {
                    LOGGER.log(Level.ERROR, "[Module] Module's main class could not be found for Module: " + moduleJar.getName());
                    continue;
                }
                if (classInfoList.size() >= 2) {
                    LOGGER.log(Level.ERROR, "[Module] Module " + moduleJar.getName() + " had more than 1 main class.");
                    continue;
                }
                ClassInfo classInfo = classInfoList.get(0);
                if (!classInfo.extendsSuperclass(Module.class.getCanonicalName())) {
                    LOGGER.log(Level.ERROR, "[Module] " + moduleJar.getName() + "'s main class was found, however it doesn't extend the module superclass.");
                    continue;
                }
                AnnotationInfo annotationInfo = classInfo.getAnnotationInfo(ModuleInfo.class.getCanonicalName());
                AnnotationParameterValueList parameterValues = annotationInfo.getParameterValues();
                String moduleName = (String) parameterValues.getValue("name");
                String moduleAuthor = (String) parameterValues.getValue("author");
                Module module = classLoader.loadClass(classInfo.getName()).asSubclass(Module.class).newInstance();
                moduleEntries.put(moduleName, new ModuleEntry(moduleName, moduleAuthor, module));
            } catch (MalformedURLException | NoSuchMethodException |
                    IllegalAccessException | InvocationTargetException |
                    ClassNotFoundException | InstantiationException e) {
                LOGGER.log(Level.ERROR, "[Module] An unexpected error occured while importing module " + moduleJar.getName());
                e.printStackTrace();
            }
        }
        
        for (ModuleEntry moduleEntry : getModuleEntries()) {
            Module module = moduleEntry.getModule();
            // Let the module run it's own loading code
            module.onEnable();
            // add Configs
            ConfigurationHandler.getInstance().addConfig(moduleEntry.getName(), module.getModuleLanguageNodes());
            // add Listeners
            module.getListeners().forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, DHCore.getPlugin(DHCore.class)));
            // TODO: Add Commands: (A little more complicated)
        }
    }
    
    /**
     * Gets the folder in which modules should be stored.
     *
     * @return {@link File} linked to the modules folder.
     */
    public File getModuleFolder() {
        return moduleFolder;
    }
    
    /**
     * Gets a list of all enabled modules' entries.
     *
     * @return {@link List} of {@link ModuleEntry}
     */
    public List<ModuleEntry> getModuleEntries() {
        return new ArrayList<>(moduleEntries.values());
    }
    
    /**
     * Gets the entry of the module with the specified name
     *
     * @param moduleName {@link String} containing the module's name.
     * @return {@link ModuleEntry} with the module's name, null if not obtainable.
     */
    @Nullable
    public ModuleEntry getModuleEntry(String moduleName) {
        return moduleEntries.get(moduleName);
    }
    
    /**
     * Gets a list of all enabled modules.
     *
     * @return {@link List} of {@link Module}
     */
    public List<Module> getModules() {
        return moduleEntries.values().stream().map(ModuleEntry::getModule).collect(Collectors.toList());
    }
    
    /**
     * Gets the module with the specified name.
     *
     * @param moduleName {@link String} containing the module's name.
     * @return {@link Module} with the specified name, null if not obtainable.
     */
    @Nullable
    public Module getModule(String moduleName) {
        return moduleEntries.get(moduleName).getModule();
    }
    
    /**
     * Checks to see if a module the specified name is enabled.
     *
     * @param moduleName {@link String} containing the name to check.
     * @return {@link true} if a module exists with the specified name, false otherwise
     */
    public boolean isModuleEnabled(String moduleName) {
        return moduleEntries.containsKey(moduleName);
    }
    
    public static ModuleHandler getInstance() {
        if (instance == null) {
            instance = new ModuleHandler();
        }
        return instance;
    }
}
