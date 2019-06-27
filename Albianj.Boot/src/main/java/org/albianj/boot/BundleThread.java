package org.albianj.boot;

import org.albianj.boot.logging.LogServant;
import org.albianj.boot.logging.LoggerLevel;

/**
 * bundle执行的thread
 */
public class BundleThread extends Thread {

    private BundleContext bundleContext;

    public BundleThread(BundleContext bundleContext, String name, Runnable func){
        super(bundleContext.getThreadGroup(),func);
        this.bundleContext = bundleContext;
        super.setContextClassLoader(bundleContext.getClassLoader());
        super.setName(name);
        super.setDaemon(true);
    }

    public BundleContext getCurrentBundleContext(){
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
            LogServant.Instance.addRuntimeLogAndThrow("BundleThread", LoggerLevel.Info,
                    this.getClass(),e,"BundleThread executer error.",null,
                    "execute bundle -> {0} thread  is error,then exit thread...",
                    bundleContext.getBundleName());
        }finally {
            return;
        }
    }
}
