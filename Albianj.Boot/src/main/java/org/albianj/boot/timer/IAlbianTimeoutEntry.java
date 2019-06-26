package org.albianj.boot.timer;

public interface IAlbianTimeoutEntry {
    IAlbianTimer timer();
    IAlbianTimerTask task();
    boolean isExpired();
    boolean isCancelled();
    boolean cancel();
}
