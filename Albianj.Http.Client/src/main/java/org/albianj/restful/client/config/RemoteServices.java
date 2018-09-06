package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXmlPairNode;
import org.albianj.xml.XmlElementGenericAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuhaifeng on 17/2/10.
 */
@XmlElementGenericAttribute(Clazz = RemoteService.class)
public class RemoteServices extends HashMap<String,RemoteService> implements IAlbianXmlPairNode<RemoteService> {
    @Override
    public void addNode(String key, RemoteService value) {
        this.put(key,value);
    }

    public Map<String,RemoteService> getRemoteServices(){
        return this;
    }
}
