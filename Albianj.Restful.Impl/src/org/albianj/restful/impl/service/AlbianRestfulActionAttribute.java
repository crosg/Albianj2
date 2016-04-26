package org.albianj.restful.impl.service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.albianj.restful.object.AlbianRestfulActionMethod;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianRestfulActionAttribute {
	String Name() default "";
	int Method() default AlbianRestfulActionMethod.GET;
}
