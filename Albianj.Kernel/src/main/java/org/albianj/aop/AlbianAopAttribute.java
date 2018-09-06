package org.albianj.aop;

import java.lang.annotation.*;

/**
 * Created by xuhaifeng on 16/5/31.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianAopAttribute {
    /**
     * give up the aop when value is true
     * default is false then execute aop proxy
     *
     * @return
     */
    boolean avoid() default false;
}
