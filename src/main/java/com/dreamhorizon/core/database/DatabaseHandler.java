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

package com.dreamhorizon.core.database;

import com.dreamhorizon.core.configuration.ConfigurationHandler;
import com.dreamhorizon.core.configuration.enums.CoreConfiguration;
import com.dreamhorizon.core.configuration.implementation.EnumConfiguration;
import com.dreamhorizon.core.modulation.ModuleHandler;
import com.dreamhorizon.core.modulation.implementation.Module;
import com.dreamhorizon.core.util.FileUtil;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import liquibase.ContextExpression;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.InitException;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.Registry;
import org.javalite.activejdbc.annotations.Table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class DatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger("com.dreamhorizon.core");
    private static DatabaseHandler instance = null;
    private final EnumConfiguration coreConfig = ConfigurationHandler.getInstance().getConfig("core");
    private final String dbTablePrefix = (String) coreConfig.get(CoreConfiguration.DATABASE_TABLE_PREFIX);
    private final DB db;
    private final String username = (String) coreConfig.get(CoreConfiguration.DATABASE_USERNAME);
    private final String password = (String) coreConfig.get(CoreConfiguration.DATABASE_PASSWORD);
    private String driver;
    private String jdbcURL;
    
    private DatabaseHandler() {
        String dbType = (String) coreConfig.get(CoreConfiguration.DATABASE_TYPE);
        // Add more DBs if necessary, in that case also use a switch clause for slighty better performance.
        if ("mysql".equals(dbType.toLowerCase())) {
            driver = "com.mysql.jdbc.Driver";
            jdbcURL = "jdbc:mysql://" + coreConfig.get(CoreConfiguration.DATABASE_HOSTNAME)
                + ":" + coreConfig.get(CoreConfiguration.DATABASE_PORT)
                + "/" + coreConfig.get(CoreConfiguration.DATABASE_SCHEMA_NAME)
                + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf-8";
        } else {
            LOGGER.log(Level.ERROR, "[Database] " + dbType + " is not a valid database type!");
            throw new IllegalArgumentException(dbType + " is not a valid database type!");
        }
        
        File modelsFile = new File("plugins" + File.separator + "DHCore" + File.separator + "database" + File.separator + "models" + File.separator + "activejdbc_models.properties");
        if (!FileUtil.createFolder(modelsFile.getParentFile())) {
            LOGGER.log(Level.ERROR, "[Database] Failed to create ActiveJDBC models parent file.");
            db = null;
            return;
        }
        try (ScanResult scanResult = new ClassGraph()
            .addClassLoader(this.getClass().getClassLoader())
            .enableClassInfo()
            .ignoreClassVisibility()
            .enableAnnotationInfo()
            .whitelistPackages()
            .disableDirScanning()
            .disableNestedJarScanning()
            .disableModuleScanning()
            .scan()) {
            ClassInfoList classInfoList = scanResult.getSubclasses(Model.class.getCanonicalName());
            if (classInfoList == null || classInfoList.isEmpty()) {
                LOGGER.log(Level.DEBUG, "[Database] No objects were found for the database.");
                db = null;
                return;
            }
            
            try {
                FileWriter writer = new FileWriter(modelsFile, false);
                classInfoList.forEach(classInfo -> {
                    try {
                        // Use classgraph to get all @Table classes.
                        // Use the setAnnotation method to update their tables to add the prefix.
                        setTable(classInfo.loadClass());
                        // Use Commons Configuration to add them to the model configuration.
                        writer.write(classInfo.getName() + ":" + "DreamHorizonCore" + System.getProperty("line.separator"));
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        LOGGER.log(Level.ERROR, "[Database] An unexpected error occured while setting object table annotations and instrumentation.");
                        LOGGER.log(Level.ERROR, e);
                        e.printStackTrace();
                    } catch (IOException e) {
                        LOGGER.log(Level.ERROR, "[Database] An unexpected error occured while creating the ActiveJDBC models file.");
                        LOGGER.log(Level.ERROR, e);
                        e.printStackTrace();
                    }
                });
                writer.close();
            } catch (IOException e) {
                LOGGER.log(Level.ERROR, "[Database] An unexpected error occured while creating the ActiveJDBC models file.");
                LOGGER.log(Level.ERROR, e);
                e.printStackTrace();
            }
        }
        // add the now created properties file to the classpath for ActiveJDBC to see.
        try {
            // Add to classpath.
            URLClassLoader classLoader = (URLClassLoader) DatabaseHandler.class.getClassLoader();
            Method method = classLoader.getClass().getSuperclass().getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(classLoader, modelsFile.getParentFile().toURI().toURL());
            // Add to ActiveJDBC.
            Registry.INSTANCE.setModelFile(modelsFile.getName());
            
        } catch (MalformedURLException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.ERROR, "[Database] An unexpected error occured while adding/creating the ActiveJDBC models file.");
            LOGGER.log(Level.ERROR, e);
            e.printStackTrace();
        }
        try {
            db = new DB("DreamHorizonCore");
            // Connect to DB.
            db.open(driver, jdbcURL, username, password);
            // Liquibase generate Schema.
            generateSchema(db.connection());
            db.close();
        } catch (InitException e) {
            throw new RuntimeException("Failed to load ActiveJDBC");
        }
    }
    
    public void open() {
        db.open(driver, jdbcURL, username, password);
    }
    
    public void close() {
        db.close();
    }
    
    private void generateSchema(Connection connection) {
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                "db/database-master.xml",
                new ClassLoaderResourceAccessor(), database
            );
            // Add our own parameters here.
            liquibase.setChangeLogParameter("players", dbTablePrefix + "PLAYERS");
            for (Module module : ModuleHandler.getInstance().getModules()) {
                // parameters before including other changelogs (IMPORTANT)
                if (module.getSchemaProperties() != null && !module.getSchemaProperties().isEmpty()) {
                    module.getSchemaProperties().forEach(s -> liquibase.setChangeLogParameter(s, dbTablePrefix + s.toUpperCase()));
                }
                // module schemas need to be loaded into the main database changelog.
                if (module.getSchemaResourcesPath() != null && !module.getSchemaResourcesPath().isEmpty()) {
                    liquibase.getDatabaseChangeLog().include(module.getSchemaResourcesPath(), false, new ClassLoaderResourceAccessor(), new ContextExpression(), true);
                }
            }
            
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException e) {
            LOGGER.log(Level.ERROR, "An unexpected error occured while generating the database schema.");
            LOGGER.log(Level.ERROR, e);
            e.printStackTrace();
        }
    }
    
    
    @SuppressWarnings("unchecked")
    private void setTable(Class clazz) throws IllegalAccessException, NoSuchFieldException {
        Table oldAnnotation = (Table) clazz.getAnnotation(Table.class);
        
        Object handler = Proxy.getInvocationHandler(oldAnnotation);
        Field f;
        f = handler.getClass().getDeclaredField("memberValues");
        f.setAccessible(true);
        Map<String, Object> memberValues;
        memberValues = (Map<String, Object>) f.get(handler);
        
        Object oldValue = memberValues.get("value");
        if (oldValue == null || oldValue.getClass() != (dbTablePrefix + oldAnnotation.value()).getClass()) {
            throw new IllegalArgumentException();
        }
        memberValues.put("value", dbTablePrefix + oldAnnotation.value());
    }
    
    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }
    
}
