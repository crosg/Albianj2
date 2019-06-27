package org.albianj.aop.tags;

import org.albianj.boot.tags.CommentsTag;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@CommentsTag("service的method计时功能,加入该标注的method自动会加入计时功能，单位是MS")
public @interface MethodMonitorTag {
    boolean Enable() default true;
    String LogTagName();
}
