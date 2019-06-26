package org.albianj.boot.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface IAlbianTimer {
    IAlbianTimeoutEntry addTimeout(IAlbianTimerTask task, long delay, TimeUnit unit, String argv);
    Set<IAlbianTimeoutEntry> stop();
}
