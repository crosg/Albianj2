package org.albianj.persistence.object.rants;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectDataRouterRant {

    String Name();

    String StorageName();

    String TableName() default "";

    boolean Enable() default true;

    String TableOwner() default "";

}
