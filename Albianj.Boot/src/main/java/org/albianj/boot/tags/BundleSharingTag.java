package org.albianj.boot.tags;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
@BundleSharingTag
public @interface BundleSharingTag {
}
