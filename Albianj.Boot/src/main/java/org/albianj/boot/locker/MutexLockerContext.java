package org.albianj.boot.locker;


import org.albianj.boot.AlbianApplicationServant;
import org.albianj.boot.BundleThread;
import org.albianj.boot.helpers.StringServant;
import org.albianj.boot.tags.BundleSharingTag;

import java.util.concurrent.atomic.AtomicLong;

/**
 * albianj的互斥锁,使用无锁编程实现
 * 该锁将会记录:locker获取的时间,locker所属的线程,locker获取时执行的class
 *
 * 注意,当使用本locker的时候,尽量使用tryEnter,当使用entry的时候,可能会引起CPU的飙升
 * 所以单核CPU并不推荐entry,多核CPU对于锁时间过长的方法也推荐tryEntry
 */
@BundleSharingTag
public class MutexLockerContext {

    private AtomicLong locker = new AtomicLong();
    private long ts = 0;
    private String owner = null;
    private Class<?> clzz;

    public boolean tryEnter(Class<?> clzz) {
        Thread t = Thread.currentThread();
        if(locker.compareAndSet(0,t.getId())){
            if(t instanceof BundleThread) {
                owner = StringServant.Instance.format("{0}-{1}",
                        ((BundleThread) t).getCurrentBundleContext().getBundleName(),t.getName());
            } else {
                owner = StringServant.Instance.format("{0}-{1}",
                        AlbianApplicationServant.Instance.BootBundleName,t.getName());
            }
            this.clzz = clzz;
            this. ts = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public boolean tryEnter(Class<?> clzz,int trytimes,int intervalMs) {
        do {
            if(tryEnter(clzz)){
                return true;
            }
            try{
                Thread.sleep(intervalMs);
            }catch (Exception e){

            }
        }while (0 != (--trytimes));
        return false;
    }

    public void Enter(Class<?> clzz){
        while(!tryEnter(clzz));
    }

    public void release(){
        this.owner = null;
        this.clzz = null;
        this.ts = 0;
        locker.set(0);

    }
}
