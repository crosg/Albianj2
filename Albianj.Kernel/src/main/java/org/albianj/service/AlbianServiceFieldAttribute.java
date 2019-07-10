package org.albianj.service;

import java.lang.reflect.Field;

/**
 * Created by xuhaifeng on 16/5/12.
 */
public class AlbianServiceFieldAttribute implements IAlbianServiceFieldAttribute {
    String type;
    String value;
    String name;
    Field field;
    boolean allowNull = false;
    boolean isReady = false;
    AlbianServiceFieldSetterLifetime lifetime = AlbianServiceFieldSetterLifetime.AfterLoading;
//    String stn;

    /**
     * @return
     */
    @Override
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * @param v
     */
    @Override
    public void setValue(String v) {
        this.value = v;
    }

    /**
     * @return
     */
    @Override
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Field getField() {
        return field;
    }

    @Override
    public void setField(Field f) {
        field = f;
    }

    @Override
    public boolean getAllowNull() {
        return this.allowNull;
    }

    @Override
    public void setAllowNull(boolean allowNull) {
        this.allowNull = allowNull;
    }

    @Override
    public boolean isReady() {
        return this.isReady;
    }

    @Override
    public void setReady(boolean isReady) {
        this.isReady = isReady;
    }

    @Override
    public AlbianServiceFieldSetterLifetime getSetterLifetime() {
        return this.lifetime;
    }

    @Override
    public void setSetterLifetime(AlbianServiceFieldSetterLifetime lifetime) {
        this.lifetime = lifetime;
    }

//    @Override
//    public String getSetterName() {
//        return this.stn;
//    }
//
//    @Override
//    public void setSetterName(String stn) {
//        this.stn = stn;
//    }
}
