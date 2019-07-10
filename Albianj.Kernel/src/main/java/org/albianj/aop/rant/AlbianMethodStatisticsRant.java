package org.albianj.aop.rant;

import org.albianj.comment.Comments;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Comments("service的method计时功能,加入该标注的method自动会加入计时功能，单位是MS")
public @interface AlbianMethodStatisticsRant {
    boolean Enable() default true;
    String LogTagName();
}
