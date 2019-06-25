package org.albianj.aop.rant;

import org.albianj.boot.tags.Comments;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
@Comments("service的method重试的标识，注意：1.当函数抛出AlbianRetryException时，进行重试；2. 可重试的method必须为可重入函数。")
public @interface AlbianMethodRetryRant {
    int RetryTimes() default  2;
    int DelayMs() default  100;
}
