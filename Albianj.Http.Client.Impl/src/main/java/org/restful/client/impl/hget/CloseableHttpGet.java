package org.restful.client.impl.hget;

import org.apache.http.client.methods.HttpGet;

public class CloseableHttpGet extends HttpGet implements AutoCloseable{
    @Override
    public void close() throws Exception {

    }
}
