package org.albianj.pooling.impl;

import org.albianj.pooling.*;

public class ReusableObjectPoolMgr {

    public static synchronized IReusableObjectPool newObjectPool(IPoolingConfig cf,
                                                                 IPoolingObjectFactory objFactory,
                                                                 IPoolingObjectConfig objConfig) {
        return ReusableObjectPool.newConnectionPool(cf,objFactory,objConfig);
    }

    public static <T extends AutoCloseable> IPoolingObject newPoolingObject(T obj, long startupTimeMs, boolean isPooling){
        return new PoolingObject(obj,startupTimeMs,isPooling);
    }

}
