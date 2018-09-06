package org.albianj.xml;

import java.util.List;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class Student implements IAlbianXml2ObjectSigning {
    public Student(){}

    private String name;
    private ClassRooms classRooms;
    private int age;

    @XmlElementAttribute(Name = "name")
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

    @XmlElementAttribute(Name = "age")
    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
