package org.albianj.mvc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface HttpFieldAttribute {
    String Name() default "";
    boolean AutoBinding() default false;
}
