package dev.fanger.simpleclients.logging;

import dev.fanger.simpleclients.logging.loggers.SystemPrintLogger;

public abstract class Logger {

    public static Class<? extends Logger> LOGGER_CLASS_TYPE = SystemPrintLogger.class;

    private static Logger internalLogger;

    static {
        setupInternalLogger();
    }

    /**
     * The implemented version of logging, the final point where all messages get sent to
     *
     * @param level
     * @param message
     */
    public abstract void logMessage(Level level, String message);

    /**
     * Log a given message at a specified level
     *
     * @param level
     * @param message
     */
    public static void log(Level level, String message) {
        internalLogger.logMessage(level, message);
    }

    /**
     * Log a given exception at a specific level
     *
     * @param level
     * @param e
     */
    public static void log(Level level, Exception e) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(e.getClass().getName());
        for(StackTraceElement stackTraceElement : e.getStackTrace()) {
            stringBuilder.append(System.lineSeparator());
            stringBuilder.append('\t');
            stringBuilder.append(stackTraceElement);
        }

        internalLogger.log(level, stringBuilder.toString());
    }

    /**
     * Overrides SimpleClient's default logger type. This effects where SimpleClient's messages are logged to
     *
     * @param loggerClassType
     */
    public static void overrideLoggerType(Class<? extends Logger> loggerClassType) {
        if(loggerClassType != LOGGER_CLASS_TYPE) {
            LOGGER_CLASS_TYPE = loggerClassType;
            setupInternalLogger();
        }
    }

    /**
     * Prepares the internal logger to whichever LOGGER_CLASS_TYPE is currently set
     *
     */
    private static void setupInternalLogger() {
        try {
            internalLogger = LOGGER_CLASS_TYPE.getConstructor().newInstance();
        } catch (Exception e) {
            internalLogger = new SystemPrintLogger();
            internalLogger.log(Level.ERROR, e);
        }
    }

}
