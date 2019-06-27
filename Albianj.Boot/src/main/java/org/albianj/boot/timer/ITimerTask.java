package org.albianj.boot.timer;

import org.albianj.boot.tags.BundleSharingTag;

@BundleSharingTag
public interface ITimerTask {
    void run(ITimeoutEntry ITimeoutEntry, String argv);
}
