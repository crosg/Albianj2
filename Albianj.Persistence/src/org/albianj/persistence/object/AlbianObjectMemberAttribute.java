package org.albianj.persistence.object;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Name="Id"
// FieldName="BizOfferId" AllowNull="false"
// Length="32" PrimaryKey="true"
// DbType="string" IsSave="true"/>


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface AlbianObjectMemberAttribute {
	String FieldName() default "";
	boolean IsAllowNull() default true;
	int Length() default -1;
	boolean IsPrimaryKey() default false;
	int DbType() default 0;
	boolean IsSave() default true;
}
