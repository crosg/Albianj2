package org.albianj.xml;

import java.lang.reflect.Field;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class FieldMetadata {

    private String fieldName;
    private String nodeName;
    private Class<?> type;
    private Field f;


    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setFieldName(String name) {
        this.fieldName = name;
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

    public Field getField() {
        return f;
    }

    public void setField(Field f) {
        this.f = f;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }
}
