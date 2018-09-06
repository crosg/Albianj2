package org.albianj.logger;

/**
 * Created by xuhaifeng on 17/2/9.
 */
public enum AlbianLoggerLevel {
    Debug("*",0),
    Info("$",1) ,
    Warn("@",2),
    Error("!",3),
    Mark("|",4);

    private String tag = "*";
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
