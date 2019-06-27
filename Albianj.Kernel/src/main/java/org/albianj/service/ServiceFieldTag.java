package org.albianj.service;

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
public @interface ServiceFieldTag {
    /*
     * field value type
     * default is string
     */
    ServiceFieldType Type() default ServiceFieldType.String;

    /*
     * field value
     * because annotation rule,the value cannot be object,
     * so we replace it with string.
     * default is Empty
     * when type is ref,this value is the service id
     */
    String Value() default "";

    boolean AllowNull() default false;

    ServiceFieldSetterLifetime SetterLifetime() default ServiceFieldSetterLifetime.AfterNew;
}
