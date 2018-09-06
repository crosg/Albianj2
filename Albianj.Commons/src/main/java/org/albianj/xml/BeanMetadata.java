package org.albianj.xml;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class BeanMetadata {
    private String name;
    private Class<?> realClass;
    private Map<String,PropertyMetadata> propertyMetadatas;

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
    public Map<String, PropertyMetadata> getPropertyMetadatas() {
        return propertyMetadatas;
    }

    /**
     * Setter for property 'writeMethods'.
     *
     * @param propertyMetadatas Value to set for property 'property Metadatas'.
     */
    public void setPropertyMetadatas(Map<String, PropertyMetadata> propertyMetadatas) {
        this.propertyMetadatas = propertyMetadatas;
    }
}
