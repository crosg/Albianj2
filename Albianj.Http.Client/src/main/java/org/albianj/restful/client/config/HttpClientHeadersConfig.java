package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigGenericCollection2NodeRant;
import org.albianj.xml.IAlbianXmlPairNode;

import java.util.LinkedHashMap;

@ConfigGenericCollection2NodeRant(Clazz = HttpClientHeaderConfig.class)
public class HttpClientHeadersConfig extends LinkedHashMap<String, HttpClientHeaderConfig> implements IAlbianXmlPairNode<HttpClientHeaderConfig> {
    @Override
    public void addNode(String key, HttpClientHeaderConfig value) {
        this.put(key,value);
    }
}
