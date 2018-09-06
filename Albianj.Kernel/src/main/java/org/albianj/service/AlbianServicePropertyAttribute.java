package org.albianj.service;

/**
 * Created by xuhaifeng on 16/5/12.
 */
public class AlbianServicePropertyAttribute implements IAlbianServicePropertyAttribute {
    String type;
    String value;
    String name;

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
}
