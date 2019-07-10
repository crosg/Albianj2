package org.albianj.kernel;

import org.albianj.comment.Comments;

/**
 * Created by xuhaifeng on 17/2/19.
 */
@Comments("Albian Service的上下文")
public interface IAlbianServiceContext {
    Object getSessionId();

    void setSessionId(Object sessionId);

}
