package org.albianj.mvc.config;

import java.lang.reflect.Field;

public class ViewFieldConfigurtion {

    private String name;
    private String bindingName;
    private Field field;
    private Class<?> type;
    private boolean autoBinding = false;

    public boolean isAutoBinding() {
        return autoBinding;
    }

    public void setAutoBinding(boolean autoBinding) {
        this.autoBinding = autoBinding;
    }

    /**
     * @return the type
     */
    public Class<?> getType() {
        return type;
    }


    /**
     * @param type the type to set
     */
    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the bindingName
     */
    public String getBindingName() {
        return bindingName;
    }

    /**
     * @param bindingName the bindingName to set
     */
    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }


}
