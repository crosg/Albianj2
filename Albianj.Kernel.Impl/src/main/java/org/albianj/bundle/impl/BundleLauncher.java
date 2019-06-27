package org.albianj.bundle.impl;

import org.albianj.boot.BundleContext;
import org.albianj.boot.loader.BundleClassLoader;
import org.albianj.boot.tags.BundleSharingTag;

/**
 * bundle的启动类
 */
public class BundleLauncher {

    /**
     *
     * @param bctx
     * @param isLazyStartup：是否懒惰启动。
     *                     当懒惰启动的时候，将不进行任何的xml反射，反射将在第一次使用时触发
     */
    public void startup(BundleContext bctx,boolean isLazyStartup){
        BundleClassLoader loader = (BundleClassLoader)  bctx.getClassLoader();
        if(null == loader){

        }
        loader.loadAllClass(bctx);
    }

    private void loadBundle(BundleContext bctx,boolean isLazyStartup){
        /**
         * repair bundle context first
         */
        BundleServant.Instance.repair("BundleStartupThread",bctx);
    }

    /**
     * 完全的加载bundle，适用于web等一次加载长时间运行的应用类型
     * @param bctx
     */
    private void fullLoadBundle(BundleContext bctx){
        /**
         * 0th : parser bundle.xml conf and repair it
         * 1th : find kernel service and parser their xml conf,merger their config
         * 3th : load kernel service
         * 4th : find AptObject and merger their config
         * 5th : find customer service and parser their xml conf,merger their config
         * 6th : load customer service
         * 7th : find DataRouter and merger their config
         * 8th : load DataRouterj
         */

    }

    /**
     * 懒惰的加载bundle，又叫快速的加载，适用于job等一次加载，短时间运行的应用类型
     * @param bctx
     */
    private void lazyLoadBundle(BundleContext bctx){
        /**
         * 0th : parser bundle.xml conf
         * 1th : parser service.xml conf
         * 4th : parser AptObject config
         * 5th : find customer service and parser their xml conf,merger their config
         * 6th : load customer service
         */
    }

}
