package org.albianj.boot.except;

import org.albianj.boot.tags.BundleSharingTag;

@BundleSharingTag
public class LocationInfo {
    private Throwable e;
    private Class<?> refType;

    public LocationInfo(Throwable e, Class<?> refType) {
        this.e = e;
        this.refType = refType;
    }

    public Throwable getThrowable(){
        return this.e;
    }
    public Class<?> getRefType(){return this.refType;}
}
