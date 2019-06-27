package org.albianj.boot.timer;

import org.albianj.boot.tags.BundleSharingTag;

@BundleSharingTag
public interface ITimeoutEntry {
    ITimer timer();
    ITimerTask task();
    boolean isExpired();
    boolean isCancelled();
    boolean cancel();
}
