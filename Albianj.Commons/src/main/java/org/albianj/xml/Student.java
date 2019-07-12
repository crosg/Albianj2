package org.albianj.xml;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class Student implements IAlbianXml2ObjectSigning {
    @XmlElementAttribute(Name = "name")
    private String name;
    private ClassRooms classRooms;
    @XmlElementAttribute(Name = "age")
    private int age;
    public Student() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClassRooms getClassRooms() {
        return classRooms;
    }

    public void setClassRooms(ClassRooms classRooms) {
        this.classRooms = classRooms;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
