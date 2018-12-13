package org.restful.client.impl.hpost;

import org.apache.http.client.methods.HttpPost;

public class CloseableHttpPost extends HttpPost implements AutoCloseable{

    @Override
    public void close() throws Exception {

    }
}
