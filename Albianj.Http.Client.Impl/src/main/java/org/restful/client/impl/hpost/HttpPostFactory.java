package org.restful.client.impl.hpost;

import org.albianj.pooling.IPoolingObject;
import org.albianj.pooling.IPoolingObjectConfig;
import org.albianj.pooling.IPoolingObjectFactory;
import org.albianj.pooling.impl.ReusableObjectPoolMgr;

public class HttpPostFactory implements IPoolingObjectFactory {
    @Override
    public IPoolingObject newPoolingObject(boolean isPooling, IPoolingObjectConfig objConfig) {
        CloseableHttpPost httpPost = new CloseableHttpPost();
        IPoolingObject<CloseableHttpPost> wappedHttpPost = ReusableObjectPoolMgr.newPoolingObject(httpPost, System.currentTimeMillis(), true);
        return wappedHttpPost;
    }
}
