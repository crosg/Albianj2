package org.restful.client.impl.hget;

import org.albianj.pooling.IPoolingObject;
import org.albianj.pooling.IPoolingObjectConfig;
import org.albianj.pooling.IPoolingObjectFactory;
import org.albianj.pooling.impl.ReusableObjectPoolMgr;

public class HttpGetFactory implements IPoolingObjectFactory {
    @Override
    public IPoolingObject newPoolingObject(boolean isPooling, IPoolingObjectConfig objConfig) {

//        HttpClientConfig httpClientConfig = (HttpClientConfig) objConfig;
//        HttpClientBuilder hcb = HttpClientBuilder.create();
        CloseableHttpGet httpGet = new CloseableHttpGet();
        IPoolingObject<CloseableHttpGet> wappedHttpGet = ReusableObjectPoolMgr.newPoolingObject(httpGet, System.currentTimeMillis(), true);
        return wappedHttpGet;

//        CloseableHttpGet orgHttpGet = new CloseableHttpGet();
//        PoolingHttpGet httpGet = new PoolingHttpGet(orgHttpGet,System.currentTimeMillis(),isPooling);
//        return httpGet;
    }
}
