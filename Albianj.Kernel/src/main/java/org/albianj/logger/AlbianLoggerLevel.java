package org.albianj.logger;

/**
 * Created by xuhaifeng on 17/2/9.
 */
public enum AlbianLoggerLevel {
    Debug("DEBUG",0),
    Info("INFO",1) ,
    Warn("WARN",2),
    Error("ERROR",3),
    Mark("MARK",4);

    private String tag = "D";
    private int level = 0;
    AlbianLoggerLevel(String tag,int level) {
        this.level = level;
        this.tag = tag;
    }

    public String getTag(){
        return this.tag;
    }

    public int getLevel(){
        return this.level;
    }


}
