package org.albianj.config.parser;


import java.lang.annotation.*;

/**
 * 表示xml配置文件与根节点的关系
 * Created by xuhaifeng on 17/2/5.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface ConfigClass2NodeRant {
    String XmlNodeName() default "";
    boolean IsRoot() default false;
}
