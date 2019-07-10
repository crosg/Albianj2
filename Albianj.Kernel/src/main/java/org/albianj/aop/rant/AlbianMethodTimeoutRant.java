package org.albianj.aop.rant;

import org.albianj.comment.Comments;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Comments("service的method的超时功能，当执行超过限定时抛出异常 单位是MS")
public @interface AlbianMethodTimeoutRant {
    long TimestampMs() default 100;
}
