package org.albianj.persistence.object.rants;


import java.lang.annotation.*;
import java.sql.Types;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
@Documented
public @interface AlbianObjectDataFieldRant {

    String FieldName() default "";

    boolean IsAllowNull() default true;

    int Length() default -1;

    boolean IsPrimaryKey() default false;

    int DbType() default Types.OTHER;

    boolean IsSave() default true;

    /*
     * not scan by albianj persistence
     */
    boolean Ignore() default false;

    String PropertyName() default "";

    boolean IsAutoGenKey() default false;

}
