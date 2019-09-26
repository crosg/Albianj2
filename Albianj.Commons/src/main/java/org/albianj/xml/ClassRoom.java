package org.albianj.xml;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class ClassRoom implements IAlbianXml2ObjectSigning {
    @XmlElementAttribute(Name = "name")
    private String name;

    public ClassRoom() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
