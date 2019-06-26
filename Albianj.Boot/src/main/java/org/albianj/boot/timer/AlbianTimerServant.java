package org.albianj.boot.timer;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AlbianTimerServant {
    public static AlbianTimerServant Instance = null;
    static {
        Instance = new AlbianTimerServant();

    }
    private AlbianTimerWheel wheel = new AlbianTimerWheel();

    public IAlbianTimeoutEntry addEntry(IAlbianTimerTask task, long delay, TimeUnit unit, String argv){
        return wheel.addTimeout(task,delay,unit,argv);
    }

    public void cancelEntry(IAlbianTimeoutEntry entry){
        if(!entry.isCancelled()) {
            entry.cancel();
        }
    }

    public Set<IAlbianTimeoutEntry> stop(){
        return wheel.stop();
    }

}
