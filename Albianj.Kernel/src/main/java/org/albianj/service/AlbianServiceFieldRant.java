package org.albianj.service;

import javax.swing.*;
import java.lang.annotation.*;

/**
 * service field setter tags
 * if you use this tags to field,the field must have value,
 * and not allow the field set to NULL.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AlbianServiceFieldRant {
    /*
     * field value type
     * default is string
     */
    AlbianServiceFieldType Type() default AlbianServiceFieldType.String;

    /*
     * field value
     * because annotation rule,the value cannot be object,
     * so we replace it with string.
     * default is Empty
     * when type is ref,this value is the service id
     */
    String Value() default "";

    boolean AllowNull() default false;

    AlbianServiceFieldSetterLifetime SetterLifetime() default AlbianServiceFieldSetterLifetime.AfterNew;
}
