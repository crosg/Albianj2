package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXml2ObjectSigning;

/**
 * Created by xuhaifeng on 17/2/10.
 */
public class HttpHeader implements IAlbianXml2ObjectSigning {

    private String name;
    private String value;

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
     * Getter for property 'value'.
     *
     * @return Value for property 'value'.
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for property 'value'.
     *
     * @param value Value to set for property 'value'.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
