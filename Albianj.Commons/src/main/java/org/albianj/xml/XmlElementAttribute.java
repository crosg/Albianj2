package org.albianj.xml;

import java.lang.annotation.*;

/**
 * xml解析器对于实体bean属性和xml之间映射的标签类
 * 用于在bean属性和xml节点不匹配的时候，通过name来进行转换
 * Created by xuhaifeng on 17/2/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
@Documented
public @interface XmlElementAttribute {
    String Name();
}

