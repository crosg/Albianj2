package org.albianj.xml;

import java.lang.annotation.*;

/**
 * 标识实体bean属性类为list的泛型类
 * clazz是为了解决java的泛型擦除而需要显式标识泛型类的真实类型
 * Created by xuhaifeng on 17/2/5.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface XmlElementGenericAttribute {
    Class Clazz();
}
