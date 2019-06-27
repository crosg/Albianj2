package org.albianj.kernel.tags;

import org.albianj.boot.tags.CommentsTag;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@CommentsTag("bundle的kernel级别的service，将会优先加载")
public @interface KernelServiceTag {
}
