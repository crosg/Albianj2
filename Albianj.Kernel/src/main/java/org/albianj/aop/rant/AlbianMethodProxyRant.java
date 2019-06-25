package org.albianj.aop.rant;

import org.albianj.boot.tags.Comments;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Comments("和service.xml中的method配置项联合使用")
public @interface AlbianMethodProxyRant {
    String Id();
}
