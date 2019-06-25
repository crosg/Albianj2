package org.albianj.boot;

import org.albianj.boot.logging.AlbianLogServant;
import org.albianj.boot.logging.AlbianLoggerLevel;

/**
 * bundle执行的thread
 */
public class AlbianBundleThread extends Thread {

    private AlbianBundleContext bundleContext;

    public AlbianBundleThread(AlbianBundleContext bundleContext,String name,Runnable func){
        super(bundleContext.getThreadGroup(),func);
        this.bundleContext = bundleContext;
        super.setContextClassLoader(bundleContext.getClassLoader());
        super.setName(name);
        super.setDaemon(true);
    }

    public AlbianBundleContext getCurrentBundleContext(){
        return this.bundleContext;
    }

    //need logging
    @Override
    public void start() {
        super.start();
    }

    // hold all exception in the thread
    // so every thread exit normal
    @Override
    public void run() {
        try {
            super.run();
        }catch (Throwable e){
            AlbianLogServant.Instance.addRuntimeLogAndThrow("BundleThread", AlbianLoggerLevel.Info,
                    this.getClass(),e,"BundleThread executer error.",null,
                    "execute bundle -> {0} thread  is error,then exit thread...",
                    bundleContext.getBundleName());
        }finally {
            return;
        }
    }
}
