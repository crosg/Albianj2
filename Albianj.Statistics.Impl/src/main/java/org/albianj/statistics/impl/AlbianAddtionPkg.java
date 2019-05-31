package org.albianj.statistics.impl;

public class AlbianAddtionPkg {
    private long ts;
    private String tag;
    private long figure;

    public AlbianAddtionPkg(String tag,long ts,long figure){
        this.tag = tag;
        this.ts = ts;
        this.figure = figure;
    }

    public long getTimestamp() {
        return ts;
    }

    public void setTimestamp(long ts) {
        this.ts = ts;
    }

    public String getTagName() {
        return tag;
    }

    public void setTagName(String tag) {
        this.tag = tag;
    }

    public long getFigure() {
        return figure;
    }

    public void setFigure(long figure) {
        this.figure = figure;
    }
}
