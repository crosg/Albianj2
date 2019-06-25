package org.albianj.loader.logging;

public enum AlbianLoggerLevel {
    All(0, "ALL"),
    Debug(10, "DEBUG"),
    Info(20, "INFO"),
    Warn(30, "WARN"),
    Error(40, "ERROR"),
    Fatal(50, "Fatal"),
    Mark(60, "MARK");

    private String tag;
    private int level;

    AlbianLoggerLevel(int level, String tag) {
        this.level = level;
        this.tag = tag;
    }

    public static AlbianLoggerLevel toLevel(String tag) {
        String utag = tag.trim();
        if (utag.equalsIgnoreCase(All.tag)) return All;
        if (utag.equalsIgnoreCase(Debug.tag)) return Debug;
        if (utag.equalsIgnoreCase(Info.tag)) return Info;
        if (utag.equalsIgnoreCase(Warn.tag)) return Warn;
        if (utag.equalsIgnoreCase(Error.tag)) return Error;
        if (utag.equalsIgnoreCase(Fatal.tag)) return Fatal;
        if (utag.equalsIgnoreCase(Mark.tag)) return Mark;
        return Info;
    }

    public String getTag() {
        return this.tag;
    }

    public int getLevel() {
        return this.level;
    }

}
