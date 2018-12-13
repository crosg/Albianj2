package org.albianj.xml;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class ClassRoom implements IAlbianXml2ObjectSigning {
    private String name;

    public ClassRoom() {
    }

    @XmlElementAttribute(Name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
