package org.albianj.aop;

import org.albianj.comment.Comments;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Comments("和service.xml中的method配置项联合使用")
public @interface AlbianMethodProxyRant {
    String Id();
}
