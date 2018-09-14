package org.albianj.aop;

import java.lang.annotation.*;

/**
 * Aop rant for service
 * all condition will be &&
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface AlbianServiceProxyRant {

    String ProxyName();

    /*
     * aop service id
     * the same as service id in the service.xml
     * no default value,so you must set it
     */
    String ServiceName();

    /*
     * proxy same function in service by name begin with this value
     * default value is empty
     */
    String BeginWith() default "";

    /*
     * proxy same function in service by name not begin with this value
     * defaule value is empty
     */
    String NotBeginWith() default "";

    /*
 * proxy same function in service by name end with this value
 * default value is empty
 */
    String EndWith() default "";

    /*
 * proxy same function in service by name not end with this value
 * defaule value is empty
 */
    String NotEndWith() default "";


    /*
     * proxy same function in service by name contain with this value
     * defaule value is empty
     */
    String Contain() default "";

    /*
 * proxy same function in service by name not contain with this value
 * defaule value is empty
 */
    String NotContain() default "";

    /*
 * proxy all function in service
 * defaule value is false
 */
    boolean IsAll() default false;

    String FullName() default "";

    /*
 * proxy same function in service when throw exception.
 * not default value
 */
    Class<Throwable> Exception();
}
