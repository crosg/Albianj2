package org.albianj.xml;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class PropertyMetadata {

    private String name;
    private Class<?> type;
    private Method setter;
    private Method getter;


    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for property 'type'.
     *
     * @return Value for property 'type'.
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Setter for property 'type'.
     *
     * @param type Value to set for property 'type'.
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * Getter for property 'setter'.
     *
     * @return Value for property 'setter'.
     */
    public Method getSetter() {
        return setter;
    }

    /**
     * Setter for property 'setter'.
     *
     * @param setter Value to set for property 'setter'.
     */
    public void setSetter(Method setter) {
        this.setter = setter;
    }

    /**
     * Getter for property 'getter'.
     *
     * @return Value for property 'getter'.
     */
    public Method getGetter() {
        return getter;
    }

    /**
     * Setter for property 'getter'.
     *
     * @param getter Value to set for property 'getter'.
     */
    public void setGetter(Method getter) {
        this.getter = getter;
    }
}
