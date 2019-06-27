package org.albianj.aop.tags;

import org.albianj.boot.tags.CommentsTag;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@CommentsTag("service的method的超时功能，当执行超过限定时抛出异常 单位是MS")
public @interface MethodTimeoutTag {
    long TimestampMs() default 100;
}
