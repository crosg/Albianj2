package org.albianj.mvc;

import org.albianj.boot.tags.CommentsTag;

/**
 * Created by xuhaifeng on 16/12/11.
 */
public enum HttpActionMethod {
    Get,
    Post,
    @CommentsTag("一般只会用在查询翻页，表单保存提交禁止使用")
    All,
}
