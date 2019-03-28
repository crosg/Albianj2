package org.albianj.mvc;

import org.albianj.comment.Comments;

/**
 * Created by xuhaifeng on 16/12/11.
 */
public enum HttpActionMethod {
    Get,
    Post,
    @Comments("一般只会用在查询翻页，表单保存提交禁止使用")
    All,
}
