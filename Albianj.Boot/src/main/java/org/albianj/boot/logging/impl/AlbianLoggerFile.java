package org.albianj.boot.logging.impl;

import org.albianj.boot.helpers.AlbianDailyServant;
import org.albianj.boot.helpers.AlbianOptConvertServant;
import org.albianj.boot.helpers.AlbianStringServant;
import org.albianj.boot.locker.AlbianMutexLockerContext;
import org.albianj.boot.timer.AlbianTimerServant;
import org.albianj.boot.timer.IAlbianTimeoutEntry;
import org.albianj.boot.timer.IAlbianTimerTask;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class AlbianLoggerFile implements Closeable {
    private long maxFilesizeB;
    private long logfileTimestampOfZero;
    private long free;

    private ByteBuffer buf;
    private FileOutputStream fos;
    private FileChannel fc;
    private AlbianMutexLockerContext mutex = null;
    private IAlbianTimeoutEntry entry = null;

    public AlbianLoggerFile(String logName, String path, String maxFilesize) {
        if (!path.endsWith(File.separator)) {
            path = path + File.separator;
        }
        this.maxFilesizeB = AlbianOptConvertServant.Instance.toFileSize(maxFilesize, 10 * 1024 * 1024);
        this.free = this.maxFilesizeB;
        buf = ByteBuffer.allocateDirect((int) this.maxFilesizeB);

        File p = new File(path);
        if (!p.exists()) {
            p.mkdirs();
        }
        logfileTimestampOfZero = AlbianDailyServant.Instance.todayTimestampOfZero();
        String filename = AlbianStringServant.Instance.join(path, logName, "_", AlbianDailyServant.Instance.dateStringWithoutSep(), ".log");
        try {
            fos = new FileOutputStream(filename,true);
        } catch (FileNotFoundException e) {

        }
        fc = fos.getChannel();
        mutex = new AlbianMutexLockerContext();
        entry =  AlbianTimerServant.Instance.addEntry(new IAlbianTimerTask() {
            @Override
            public void run(IAlbianTimeoutEntry IAlbianTimeoutEntry, String argv) {
                flushWithMutex();
            }
        },3,TimeUnit.SECONDS,null);
    }

    public boolean write(String src) {
        mutex.Enter(this.getClass());
        try {
            byte[] bytes = src.getBytes(Charset.forName("utf-8"));
            long len = bytes.length;
            long now = System.currentTimeMillis();
            if ((len > free) || (now >= AlbianDailyServant.DailyTimestampMS + logfileTimestampOfZero)) {
                return false;
            }
            buf.put(bytes);
            free -= len;
            return true;
        }finally {
            mutex.release();
        }
    }

    public void flushWithMutex() {
        mutex.Enter(this.getClass());
        try {
            flushWithoutMutex();
        }finally {
            mutex.release();
        }
    }

    public void flushWithoutMutex() {
        try {
            if (0 != buf.position()){
                buf.flip();
                fc.write(buf);
            }
        } catch (IOException e) {
        }
        try {
            fc.force(true);
        } catch (IOException e) {
        }

        buf.clear();
        entry =  AlbianTimerServant.Instance.addEntry(new IAlbianTimerTask() {
            @Override
            public void run(IAlbianTimeoutEntry IAlbianTimeoutEntry, String argv) {
                flushWithMutex();
            }
        },3,TimeUnit.SECONDS,null);
    }

    public void close() {
        /**
         * first delete the timeout entry to giveup flush
         */
        AlbianTimerServant.Instance.cancelEntry(entry);
        /**
         * must with mutex,if maybe in the timeout entry flushing
         */
        flushWithMutex();
        try {
            fos.close();
        } catch (IOException e) {
        }
        fos = null;
        try {
            fc.close();
        } catch (IOException e) {
        }
        closeBuffer();
    }

    private void closeBuffer() {
        if (buf == null || !buf.isDirect()) return;
        // we could use this type cast and call functions without reflection code,
        // but static import from sun.* package is risky for non-SUN virtual machine.
        //try { ((sun.nio.ch.DirectBuffer)cb).cleaner().clean(); } catch (Exception ex) { }
        try {
            Method cleaner = buf.getClass().getMethod("cleaner");
            cleaner.setAccessible(true);
            Method clean = Class.forName("sun.misc.Cleaner").getMethod("clean");
            clean.setAccessible(true);
            clean.invoke(cleaner.invoke(buf));
        } catch (Exception ex) {
        }
        buf = null;
    }

}
