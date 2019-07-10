package org.albianj.comment;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({
        ElementType.PARAMETER,
        ElementType.TYPE,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.LOCAL_VARIABLE,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR
})
/**
 *
 */
public @interface SpecialWarning {
    String value();
}
