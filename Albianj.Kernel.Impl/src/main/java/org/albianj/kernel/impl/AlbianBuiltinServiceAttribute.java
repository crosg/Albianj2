package org.albianj.kernel.impl;

public class AlbianBuiltinServiceAttribute {
    private String id;
    private String implClzz;
    private boolean required;
    private boolean loadOK;

    public AlbianBuiltinServiceAttribute(String id,String implClzz,boolean required){
        this.id = id;
        this.implClzz = implClzz;
        this.required = required;
    }

    public String getId() {
        return id;
    }

    public String getImplClzz() {
        return implClzz;
    }

    public boolean isRequired() {
        return required;
    }


    public boolean isLoadOK() {
        return loadOK;
    }

    public void setLoadOK(boolean loadOK) {
        this.loadOK = loadOK;
    }
}
