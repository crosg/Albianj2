package org.albianj.aop.tags;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface MethodIgnoreProxyTag {
    /**
     * 忽略aop的功能，设置为true，将不会有AOP的代理操作
     * @return
     */
    boolean Ignore() default true;
}
