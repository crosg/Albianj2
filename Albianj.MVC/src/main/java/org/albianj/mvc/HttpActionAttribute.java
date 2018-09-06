package org.albianj.mvc;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface HttpActionAttribute {
	String Name() default "";
	HttpActionMethod Method() default HttpActionMethod.Get;
}
