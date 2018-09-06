package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXmlListNode;
import org.albianj.xml.XmlElementGenericAttribute;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/2/10.
 */
@XmlElementGenericAttribute(Clazz = HttpHeader.class)
public class HttpHeaders extends ArrayList<HttpHeader> implements IAlbianXmlListNode<HttpHeader>{
    @Override
    public void addNode(HttpHeader obj) {
        this.add(obj);
    }

    public List<HttpHeader> getHttpHeaders(){
        return this;
    }
}
