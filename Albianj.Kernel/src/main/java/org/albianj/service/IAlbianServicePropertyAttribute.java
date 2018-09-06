package org.albianj.service;

/**
 * Created by xuhaifeng on 16/5/12.
 */
public interface IAlbianServicePropertyAttribute {

    /**
     * @return
     */
    String getType();

    /**
     * @param type
     */
    void setType(String type);

    /**
     * @return
     */
    String getValue();

    /**
     * @param v
     */
    void setValue(String v);

    /**
     * @return
     */
    String getName();

    /**
     * @param name
     */
    void setName(String name);
}
