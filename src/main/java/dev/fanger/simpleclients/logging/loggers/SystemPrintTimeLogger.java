package dev.fanger.simpleclients.logging.loggers;

import dev.fanger.simpleclients.logging.Level;
import dev.fanger.simpleclients.logging.Logger;

import java.util.HashSet;

public class SystemPrintTimeLogger extends Logger {

    private static HashSet<Level> enabledLevels;

    static {
        enabledLevels = new HashSet<>();
        enabledLevels.add(Level.INFO);
        enabledLevels.add(Level.WARN);
        enabledLevels.add(Level.ERROR);
        enabledLevels.add(Level.DEBUG);
    }

    @Override
    public void logMessage(Level level, String message) {
        if(enabledLevels.contains(level)) {
            System.out.println("[" + level.name() + "]" + System.nanoTime() + ": " + message);
        }
    }

}
