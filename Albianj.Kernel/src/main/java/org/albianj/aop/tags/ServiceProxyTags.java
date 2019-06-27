package org.albianj.aop.tags;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ServiceProxyTags {
    ServiceProxyTag[] Rants() default {};
}
