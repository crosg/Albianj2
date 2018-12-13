package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigClass2NodeRant;
import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.xml.IAlbianXml2ObjectSigning;

@ConfigClass2NodeRant(XmlNodeName = "Header")
public class HttpClientHeaderConfig implements IAlbianXml2ObjectSigning {

    @ConfigField2NodeRant()
    private String key;

    @ConfigField2NodeRant()
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
