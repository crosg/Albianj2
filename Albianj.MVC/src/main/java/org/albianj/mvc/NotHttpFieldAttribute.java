package org.albianj.mvc;

import java.lang.annotation.*;

/**
 * Created by xuhaifeng on 16/12/9.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface NotHttpFieldAttribute {
}
