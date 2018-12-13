package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigGenericCollection2NodeRant;
import org.albianj.xml.IAlbianXmlPairNode;

import java.util.LinkedHashMap;

@ConfigGenericCollection2NodeRant(Clazz = HttpClientConfig.class)
public class HttpClientsConfig extends LinkedHashMap<String,HttpClientConfig> implements IAlbianXmlPairNode<HttpClientConfig> {
    @Override
    public void addNode(String key, HttpClientConfig value) {
        this.put(key,value);
    }
}
