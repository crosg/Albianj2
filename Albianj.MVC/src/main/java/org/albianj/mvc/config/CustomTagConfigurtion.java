package org.albianj.mvc.config;

/**
 * Created by xuhaifeng on 17/1/18.
 */
public class CustomTagConfigurtion implements ICustomTagConfigurtion {
    private String name;
    private String fullClassname;

    public  String getName(){
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getFullClassname(){
        return this.fullClassname;
    }
    public void setFullClassname(String classname){
        this.fullClassname = classname;
    }

    public CustomTagConfigurtion(String name,String fullClassname){
        this.name = name;
        this.fullClassname = fullClassname;
    }

}
