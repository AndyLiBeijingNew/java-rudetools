package net.sf.rudetools.common.util.logback;

import org.slf4j.Logger;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.FixedWindowRollingPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;

public class LogbackUtil {

    private static final String MAX_FILE_SIZE = "10MB";
    // /** Log pattern for common user */
    // public static final String LOG_PATTERN_USER =
    // "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n";
    /** Log pattern for Developer */
    public static final String LOG_PATTERN_NORMAL = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%.-1level] [%thread] %msg%n";

    /**
     * Logger with FixedWindowRollingPolicy and StartupTriggeringPolicy
     * which is used in Plug-in.
     * 
     * @param savingPath
     * @param logger
     * @param partName
     */
    public static void initLoggerForPlugin(String savingPath, Logger logger, String partName) {

        String loggerFileName = savingPath + partName + ".log";
        String loggerFileNamePattern = savingPath + "%i." + partName + ".log.zip";

        if (logger instanceof ch.qos.logback.classic.Logger) {

            ch.qos.logback.classic.Logger logbacker = (ch.qos.logback.classic.Logger) logger;
            LoggerContext loggerContext = logbacker.getLoggerContext();

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern(LOG_PATTERN_NORMAL);
            encoder.start();

            RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();

            // fileAppender.setName(partName);
            fileAppender.setContext(loggerContext);
            fileAppender.setFile(loggerFileName);
            fileAppender.setEncoder(encoder);

            FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
            rollingPolicy.setContext(loggerContext);
            rollingPolicy.setParent(fileAppender);
            rollingPolicy.setFileNamePattern(loggerFileNamePattern);
            rollingPolicy.setMinIndex(1);
            rollingPolicy.setMaxIndex(9);
            rollingPolicy.start();

            fileAppender.setRollingPolicy(rollingPolicy);

            StartupTriggeringPolicy<ILoggingEvent> triggerPolicy = new StartupTriggeringPolicy<ILoggingEvent>();
            triggerPolicy.setFileName(loggerFileName);
            triggerPolicy.start();
            fileAppender.setTriggeringPolicy(triggerPolicy);

            fileAppender.start();

            logbacker.setAdditive(false);
            logbacker.addAppender(fileAppender);

            // TODO to be removed start >>>>>>>>>>>>>>>>>> .
            if (!"protobuf".equals(partName)) {
                PatternLayoutEncoder consoleEncoder = new PatternLayoutEncoder();
                consoleEncoder.setContext(loggerContext);
                consoleEncoder.setPattern(LOG_PATTERN_NORMAL);
                consoleEncoder.start();

                // Console Appender
                ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
                consoleAppender.setContext(loggerContext);
                consoleAppender.setEncoder(consoleEncoder);
                consoleAppender.start();
                logbacker.addAppender(consoleAppender);
            }
            // TODO to be removed end <<<<<<<<<<<<<<<<<<<<<<<<<<
        }
    }

    /**
     * Logger with FixedWindowRollingPolicy and
     * SizeBasedTriggeringPolicy which is used in Server.
     * 
     * @param savingPath
     * @param logger
     * @param partName
     */
    public static void initLoggerForServer(String savingPath, Logger logger, String partName) {

        String loggerFileName = savingPath + partName + ".log";
        String loggerFileNamePattern = savingPath + "%i." + partName + ".log.zip";

        if (logger instanceof ch.qos.logback.classic.Logger) {

            ch.qos.logback.classic.Logger logbacker = (ch.qos.logback.classic.Logger) logger;
            LoggerContext loggerContext = logbacker.getLoggerContext();

            PatternLayoutEncoder encoder = new PatternLayoutEncoder();
            encoder.setContext(loggerContext);
            encoder.setPattern(LOG_PATTERN_NORMAL);
            encoder.start();

            RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();

            // fileAppender.setName(partName);
            fileAppender.setContext(loggerContext);
            fileAppender.setFile(loggerFileName);
            fileAppender.setEncoder(encoder);

            FixedWindowRollingPolicy rollingPolicy = new FixedWindowRollingPolicy();
            rollingPolicy.setContext(loggerContext);
            rollingPolicy.setParent(fileAppender);

            rollingPolicy.setFileNamePattern(loggerFileNamePattern);
            rollingPolicy.setMinIndex(1);
            rollingPolicy.setMaxIndex(9);
            rollingPolicy.start();
            fileAppender.setRollingPolicy(rollingPolicy);

            SizeBasedTriggeringPolicy<ILoggingEvent> triggerPolicy = new SizeBasedTriggeringPolicy<ILoggingEvent>();
            triggerPolicy.setMaxFileSize(MAX_FILE_SIZE);
            triggerPolicy.start();
            fileAppender.setTriggeringPolicy(triggerPolicy);

            fileAppender.start();

            logbacker.setAdditive(false);
            logbacker.addAppender(fileAppender);
        }
    }

    public static void setLogLevel(Logger logger, String level) {

        // will tranlate the string to level
        // (If failed, will set default Debug level).
        Level newLevel = Level.valueOf(level);

        if (logger instanceof ch.qos.logback.classic.Logger) {
            ch.qos.logback.classic.Logger logbacker = (ch.qos.logback.classic.Logger) logger;
            Level oldLevel = logbacker.getLevel();
            String oldValue = oldLevel == null ? "null" : oldLevel.toString();
            String newValue = newLevel == null ? "null" : newLevel.toString();
            logger.info("Log level has been changed from {} to {}", oldValue, newValue);
            logbacker.setLevel(newLevel);
        }
    }
}
