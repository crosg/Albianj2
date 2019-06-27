package org.albianj.service;

import java.lang.annotation.*;

/**
 * albian service tags
 * the same as service section in service.xml
 * it use to class when it as albian service
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface ServiceTag {
    /*
     * service id,it must not be null or empty.
     */
    String Id();

    /*
     * if value is true load this service to albian kernel,
     *  or false not load it.
     *  default is true
     */
    boolean Enable() default true;

    /*
     * string format for service's interface
     * this interface must be IService's child class
     */
    String sInterface() default IService.FullName;

    /*
     * Class object format for service's interface
     */
    Class<? extends IService> Interface() default IService.class;
}
