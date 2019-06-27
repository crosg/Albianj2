package org.albianj.kernel;

import org.albianj.boot.tags.CommentsTag;

/**
 * Created by xuhaifeng on 17/2/19.
 */
@CommentsTag("Albian Service的上下文")
public interface IAlbianServiceContext {
    Object getSessionId();

    void setSessionId(Object sessionId);

}
