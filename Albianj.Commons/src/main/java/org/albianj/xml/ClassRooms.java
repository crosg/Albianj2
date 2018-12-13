package org.albianj.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuhaifeng on 17/2/3.
 */

@XmlElementGenericAttribute(Clazz = ClassRoom.class)
public class ClassRooms extends ArrayList<ClassRoom> implements IAlbianXml2ObjectSigning, IAlbianXmlListNode<ClassRoom> {

    public List<ClassRoom> getClassRooms() {

        return this;
    }

    public void setClassRooms(List<ClassRoom> list) {
        this.addAll(list);
    }

    public void addNode(ClassRoom cr) {
        this.add(cr);
    }

}
