package org.albianj.persistence.object.rants;


import org.albianj.persistence.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianObjectRant {

    Class<?> Interface();

    AlbianObjectDataRoutersRant DataRouters() default @AlbianObjectDataRoutersRant(DataRouter = AlbianObjectDataRouterDefaulter.class);
}
