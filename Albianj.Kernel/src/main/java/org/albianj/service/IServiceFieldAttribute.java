package org.albianj.service;

import java.lang.reflect.Field;

/**
 * Created by xuhaifeng on 16/5/12.
 */
public interface IServiceFieldAttribute {

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

    Field getField();

    void setField(Field f);

    boolean getAllowNull();

    void setAllowNull(boolean allowNull);

    boolean isReady();

    void setReady(boolean isReady);

    ServiceFieldSetterLifetime getSetterLifetime();

    void setSetterLifetime(ServiceFieldSetterLifetime lifetime);

}
