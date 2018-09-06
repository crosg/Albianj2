package org.albianj.mvc.config;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/12/1.
 */
public class PageActionConfigurtion {
    private String name;
    private String bindingName;
    private Method method;

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getBindingName(){
        return this.bindingName;
    }

    public void setBindingName(String bindingName){
        this.bindingName = bindingName;
    }

    public void setMethod(Method method){
        this.method = method;
    }

    public Method getMethod(){
        return this.method;
    }
}
