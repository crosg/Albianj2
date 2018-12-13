package org.albianj.config.parser;

import java.util.Map;

public class ConfigClassAttribute implements IConfigAttribute {
    private String name;
    private Class<?> realClass;
    private Map<String, IConfigAttribute> fieldsAttr;
    private boolean isRoot;

    /**
     * Getter for property 'name'.
     *
     * @return Value for property 'name'.
     */
    public String getXmlNodeName() {
        return name;
    }

    /**
     * Setter for property 'name'.
     *
     * @param name Value to set for property 'name'.
     */
    public void setXmlNodeName(String name) {
        this.name = name;
    }

    /**
     * Getter for property 'cls'.
     *
     * @return Value for property 'cls'.
     */
    public Class<?> getRealClass() {
        return realClass;
    }

    /**
     * Setter for property 'cls'.
     *
     * @param cls Value to set for property 'cls'.
     */
    public void setRealClass(Class<?> cls) {
        this.realClass = cls;
    }

    /**
     * Getter for property 'writeMethods'.
     *
     * @return Value for property 'writeMethods'.
     */
    public Map<String, IConfigAttribute> getFieldsAttribute() {
        return fieldsAttr;
    }

    /**
     * Setter for property 'writeMethods'.
     *
     * @param fieldsAttr Value to set for property 'property Metadatas'.
     */
    public void setFieldsAttribute(Map<String, IConfigAttribute> fieldsAttr) {
        this.fieldsAttr = fieldsAttr;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }
}
