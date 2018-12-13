package org.albianj.mvc.config;

import org.albianj.mvc.HttpActionMethod;

import java.lang.reflect.Method;

/**
 * Created by xuhaifeng on 16/12/1.
 */
public class ViewActionConfigurtion {
    private String name;
    private String bindingName;
    private Method method;
    private HttpActionMethod ham = HttpActionMethod.Get;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBindingName() {
        return this.bindingName;
    }

    public void setBindingName(String bindingName) {
        this.bindingName = bindingName;
    }

    public Method getMethod() {
        return this.method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public HttpActionMethod getHttpActionMethod() {
        return this.ham;
    }

    public void setHttpActionMethod(HttpActionMethod ham) {
        this.ham = ham;
    }
}
