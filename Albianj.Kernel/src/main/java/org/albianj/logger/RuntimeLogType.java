package org.albianj.logger;

public enum RuntimeLogType {
    Debug("*", 0),
    Info("$", 1),
    Warn("@", 2),
    Error("!", 3),
    Mark("|", 4);

    private String tag = "*";
    private int level = 0;

    RuntimeLogType(String tag, int level) {
        this.level = level;
        this.tag = tag;
    }
}
