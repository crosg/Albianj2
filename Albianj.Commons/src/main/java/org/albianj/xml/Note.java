package org.albianj.xml;

/**
 * Created by xuhaifeng on 17/2/3.
 */
public class Note implements IAlbianXml2ObjectSigning {
    private String name;
    private String to;
    private Student student;
    private String from;
    private String heading;
    private String body;

    public Note(){}


    public String getName() {
        return name;
    }

    @XmlElementAttribute(Name = "name")
    public void setName(String name) {
        this.name = name;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getFrom() {
        return from;
    }

    @XmlElementAttribute(Name = "from")
    public void setFrom(String from) {
        this.from = from;
    }

    @XmlElementAttribute(Name = "heading")
    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
