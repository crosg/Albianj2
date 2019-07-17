package org.albianj.config.parser;

import java.lang.reflect.Field;

public class ConfigFieldAttribute implements IConfigAttribute{
    private String fieldName;
    private Class<?> type;
    private Field field;
    private String xmlNodeName;
    private boolean makeDefaultIfNoNode;
    private ConfigFieldStyle fieldStyle = ConfigFieldStyle.Simple;
    private IConfigAttribute defineClassAttr;

    public ConfigFieldStyle getFieldStyle() {
        return fieldStyle;
    }

    public void setFieldStyle(ConfigFieldStyle fieldStyle) {
        this.fieldStyle = fieldStyle;
    }

    public IConfigAttribute getDefineClassAttr() {
        return defineClassAttr;
    }

    public void setDefineClassAttr(IConfigAttribute defineClassAttr) {
        this.defineClassAttr = defineClassAttr;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getXmlNodeName() {
        return xmlNodeName;
    }

    public void setXmlNodeName(String xmlNodeName) {
        this.xmlNodeName = xmlNodeName;
    }



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
     * @param fieldName Value to set for property 'name'.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public boolean isMakeDefaultIfNoNode() {
        return makeDefaultIfNoNode;
    }

    public void setMakeDefaultIfNoNode(boolean makeDefaultIfNoNode) {
        this.makeDefaultIfNoNode = makeDefaultIfNoNode;
    }
}
