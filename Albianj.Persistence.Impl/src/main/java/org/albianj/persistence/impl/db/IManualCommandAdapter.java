package org.albianj.persistence.impl.db;

import org.albianj.persistence.context.IManualContext;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualCommandAdapter {

    IManualContext createManualCommands(IManualContext mctx);
}
