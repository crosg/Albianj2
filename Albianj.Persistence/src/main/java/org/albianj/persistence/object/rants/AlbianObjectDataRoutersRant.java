package org.albianj.persistence.object.rants;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianObjectDataRoutersRant {

    Class<?> DataRouter();

    boolean ReaderRoutersEnable() default true;

    boolean WriterRoutersEnable() default true;

    AlbianObjectDataRouterRant[] ReaderRouters() default {};

    AlbianObjectDataRouterRant[] WriterRouters() default {};

}
