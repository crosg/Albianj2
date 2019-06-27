package org.albianj.aop.tags;

import org.albianj.boot.tags.CommentsTag;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@CommentsTag("和service.xml中的method配置项联合使用")
public @interface MethodProxyTag {
    String Id();
}
