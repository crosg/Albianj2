package org.albianj.persistence.object.rants;


import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.service.AlbianObjectDataRouterDefaulter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianObjectRant {

    Class<? extends IAlbianObject> Interface();

    AlbianObjectDataRoutersRant DataRouters() default @AlbianObjectDataRoutersRant(DataRouter = AlbianObjectDataRouterDefaulter.class);
}
