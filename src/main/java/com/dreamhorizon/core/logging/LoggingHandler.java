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

package com.dreamhorizon.core.logging;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lukas Mansour
 * @since 1.0
 */
public class LoggingHandler {
    private static final LoggingHandler instance = new LoggingHandler();
    
    private LoggingHandler() {
        // Hook into Log4J2
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        
        // Create a new list of appenders and their references that have to be added
        List<Appender> appenders = new ArrayList<>();
        List<AppenderRef[]> appenderReferences = new ArrayList<>();
        
        // Get the standard layout for the new appenders
        Layout<String> layout = PatternLayout.newBuilder()
                .withPattern(PatternLayout.SIMPLE_CONVERSION_PATTERN)
                .withAlwaysWriteExceptions(false)
                .withNoConsoleNoAnsi(false)
                .withConfiguration(config)
                .build();
        
        // ActiveJDBC
        appenders.add(
                FileAppender.newBuilder()
                        .withFileName("plugins" + File.separator + "DHCore" + File.separator + "logs" + File.separator + "ActiveJDBC.log")
                        .withLocking(false)
                        .withName("ActiveJDBC")
                        .withIgnoreExceptions(false)
                        .withBufferedIo(false)
                        .withBufferSize(0)
                        .withLayout(layout)
                        .withAdvertise(false)
                        .setConfiguration(config)
                        .build()
        );
        // Liquibase
        appenders.add(
                FileAppender.newBuilder()
                        .withFileName("plugins" + File.separator + "DHCore" + File.separator + "logs" + File.separator + "Liquibase.log")
                        .withLocking(false)
                        .withName("Liquibase")
                        .withIgnoreExceptions(false)
                        .withBufferedIo(false)
                        .withBufferSize(0)
                        .withLayout(layout)
                        .withAdvertise(false)
                        .setConfiguration(config)
                        .build()
        );
        
        // Our general logger
        appenders.add(
                FileAppender.newBuilder()
                        .withFileName("plugins" + File.separator + "DHCore" + File.separator + "logs" + File.separator + "Core.log")
                        .withLocking(false)
                        .withName("DHCore")
                        .withIgnoreExceptions(false)
                        .withBufferedIo(false)
                        .withBufferSize(0)
                        .withLayout(layout)
                        .withAdvertise(false)
                        .setConfiguration(config)
                        .build()
        );
        // Start appenders, give them the LoggerConfig and add their references to the referenceList
        for (Appender appender : appenders) {
            appender.start();
            config.addAppender(appender);
            appenderReferences.add(new AppenderRef[]{AppenderRef.createAppenderRef(appender.getName(), Level.ALL, null)});
        }
        // Add the appenders to the respective LoggerConfigs.
        LoggerConfig activeJDBCConf = LoggerConfig.createLogger(false, Level.ALL, "activejdbc", null, appenderReferences.get(0), null, config, null);
        activeJDBCConf.addAppender(appenders.get(0), Level.ALL, null);
        LoggerConfig liquibaseConf = LoggerConfig.createLogger(false, Level.ALL, "liquibase", null, appenderReferences.get(1), null, config, null);
        liquibaseConf.addAppender(appenders.get(1), Level.ALL, null);
        LoggerConfig generalConf = LoggerConfig.createLogger(false, Level.ALL, "DHCore", null, appenderReferences.get(2), null, config, null);
        generalConf.addAppender(appenders.get(2), Level.ALL, null);
        
        // Add the correct packages to the LoggerConfigs, so that those packages are forwarded to the said logger.
        config.addLogger("org.javalite", activeJDBCConf);
        config.addLogger("liquibase", liquibaseConf);
        config.addLogger("com.dreamhorizon.core", generalConf);
        ctx.updateLoggers();
    }
    
    public static LoggingHandler getInstance() {
        return instance;
    }
}
