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
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.entity.Player;
import org.javalite.activejdbc.DB;
import org.javalite.activejdbc.annotations.Table;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class DatabaseHandler {
    private static final Logger LOGGER = LogManager.getLogger("com.dreamhorizon.core");
    private static DatabaseHandler instance = null;
    private final EnumConfiguration coreConfig = ConfigurationHandler.getInstance().getConfigs().get("core");
    private final String dbTablePrefix = (String) coreConfig.get(CoreConfiguration.DATABASE_TABLE_PREFIX);
    private final DB db = new DB("DreamHorizonCore");
    private final String username = (String) coreConfig.get(CoreConfiguration.DATABASE_USERNAME);
    private final String password = (String) coreConfig.get(CoreConfiguration.DATABASE_PASSWORD);
    private String driver;
    private String jdbcURL;
    
    private DatabaseHandler() {
        String dbType = (String) coreConfig.get(CoreConfiguration.DATABASE_TYPE);
        // Add more DBs if necessary
        switch (dbType.toLowerCase()) {
            case "mysql": {
                driver = "com.mysql.jdbc.Driver";
                jdbcURL = "jdbc:mysql://" + coreConfig.get(CoreConfiguration.DATABASE_HOSTNAME)
                        + "/" + coreConfig.get(CoreConfiguration.DATABASE_SCHEMA_NAME)
                        + "?verifyServerCertificate=false&useSSL=false&useUnicode=true&characterEncoding=utf-8";
                break;
            }
            default: {
                LOGGER.log(Level.ERROR, "[Database] " + dbType + " is not a valid database type!");
                throw new IllegalArgumentException(dbType + " is not a valid database type!");
            }
        }
        // Use classgraph to get all @Table classes.
        // Use the setAnnotation method to update their tables to add the prefix.
        try (ScanResult scanResult = new ClassGraph()
                .addClassLoader(this.getClass().getClassLoader())
                .enableClassInfo()
                .ignoreClassVisibility()
                .enableAnnotationInfo()
                .whitelistPackages("com.dreamhorizon.core.objects")
                .disableDirScanning()
                .disableNestedJarScanning()
                .disableModuleScanning()
                .scan()) {
            scanResult.getClassesWithAnnotation("org.javalite.activejdbc.annotations.Table")
                    .forEach(classInfo -> {
                        try {
                            setTable(classInfo.loadClass());
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            LOGGER.log(Level.ERROR, "[Database] An unexpected error occured while setting object tables.");
                            LOGGER.log(Level.ERROR, e);
                            e.printStackTrace();
                        }
                    });
        }
        
        // Connect to DB.
        db.open(driver, jdbcURL, username, password);
        // Liquibase generate Schema.
        generateSchema(db.connection());
        db.close();
    }
    
    public void open() {
        db.open(driver, jdbcURL, username, password);
    }
    
    public void close() {
        db.close();
    }
    
    @SuppressWarnings("ConstantConditions")
    private void generateSchema(Connection connection) {
        try {
            File databaseChangelog = new File("plugins" + File.separator + "DHCore" + File.separator + "database" + File.separator + "database-schema.xml");
            if (!databaseChangelog.getParentFile().mkdirs() && !databaseChangelog.getParentFile().isDirectory()) {
                return;
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            this.getClass().getClassLoader().getResourceAsStream("database-schema.xml")
                    )
            );
            PrintWriter writer = new PrintWriter(new FileOutputStream(databaseChangelog));
            
            String str;
            int lineNumber = 1;
            Pattern pattern = Pattern.compile(".*/(.*?)/.*");
            while ((str = reader.readLine()) != null) {
                if (lineNumber >= 3 && lineNumber <= 5) {
                    Matcher m = pattern.matcher(str);
                    if (m.find()) {
                        str = str.replace("/" + m.group(1) + "/", dbTablePrefix + m.group(1));
                    }
                }
                writer.println(str);
                lineNumber = lineNumber + 1;
            }
            writer.close();
            reader.close();
            
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    databaseChangelog.getAbsolutePath()
                    , new FileSystemResourceAccessor(), database
            );
            liquibase.update(new Contexts(), new LabelExpression());
        } catch (LiquibaseException ignored) {
        
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @SuppressWarnings("unchecked")
    private void setTable(Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Table oldAnnotation = (Table) clazz.getAnnotations()[0];
        Annotation newAnnotation = new Table() {
            
            @Override
            public Class<? extends Annotation> annotationType() {
                return oldAnnotation.annotationType();
            }
            
            @Override
            public String value() {
                return dbTablePrefix + oldAnnotation.value();
            }
        };
        Field annotationDataField = Class.class.getDeclaredField("annotationData");
        annotationDataField.setAccessible(true);
        
        Object annotationData = annotationDataField.get(Player.class);
        
        Field annotationsField = annotationData.getClass().getDeclaredField("annotations");
        annotationsField.setAccessible(true);
        
        Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) annotationsField
                .get(annotationData);
        annotations.put(Table.class, newAnnotation);
    }
    
    public static DatabaseHandler getInstance() {
        if (instance == null) {
            instance = new DatabaseHandler();
        }
        return instance;
    }
    
}
