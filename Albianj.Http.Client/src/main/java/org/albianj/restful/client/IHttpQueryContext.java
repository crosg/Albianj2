package org.albianj.restful.client;

import java.util.List;

public interface IHttpQueryContext {
    public IHttpQueryContext to(String site);

    public IHttpQueryContext query(String site);

    public IHttpQueryContext exec(String site);

    public IHttpQueryContext withParas(List<KeyValuePair> paras);

    public IHttpQueryContext inTimeoutMs(long timeoutMs);

    public IHttpQueryContext addHeaders(List<KeyValuePair> headers);

    public IHttpQueryContext bySSL();

    public IHttpQueryContext bySSLButIgnoreVerify();

    public byte[] doGet(String sessionId);

    public byte[] doPost(String sessionId);
}
