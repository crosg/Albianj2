package org.albianj.mvc;

import java.lang.annotation.*;

/**
 * Created by xuhaifeng on 16/12/9.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface NotHttpActionAttribute {

}
