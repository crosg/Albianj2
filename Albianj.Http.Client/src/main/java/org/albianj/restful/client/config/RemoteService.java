package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXmlPairObject;

/**
 * Created by xuhaifeng on 17/2/10.
 */
public class RemoteService implements IAlbianXmlPairObject {

    private String id;
    private String website;
    private int port = 80;
    private HttpHeaders httpHeaders;
    private HttpConnection httpConnection;

    /**
     * Getter for property 'port'.
     *
     * @return Value for property 'port'.
     */
    public int getPort() {
        return port;
    }

    /**
     * Setter for property 'port'.
     *
     * @param port Value to set for property 'port'.
     */
    public void setPort(int port) {
        this.port = port;
    }

    private HttpConnectionPool httpConnectionPool;

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for property 'website'.
     *
     * @return Value for property 'website'.
     */
    public String getWebsite() {
        return website;
    }

    /**
     * Setter for property 'website'.
     *
     * @param website Value to set for property 'website'.
     */
    public void setWebsite(String website) {
        this.website = website;
    }

    /**
     * Getter for property 'httpHeaders'.
     *
     * @return Value for property 'httpHeaders'.
     */
    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    /**
     * Setter for property 'httpHeaders'.
     *
     * @param httpHeaders Value to set for property 'httpHeaders'.
     */
    public void setHttpHeaders(HttpHeaders httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    /**
     * Getter for property 'httpConnection'.
     *
     * @return Value for property 'httpConnection'.
     */
    public HttpConnection getHttpConnection() {
        return httpConnection;
    }

    /**
     * Setter for property 'httpConnection'.
     *
     * @param httpConnection Value to set for property 'httpConnection'.
     */
    public void setHttpConnection(HttpConnection httpConnection) {
        this.httpConnection = httpConnection;
    }

    /**
     * Getter for property 'httpConnectionPool'.
     *
     * @return Value for property 'httpConnectionPool'.
     */
    public HttpConnectionPool getHttpConnectionPool() {
        return httpConnectionPool;
    }

    /**
     * Setter for property 'httpConnectionPool'.
     *
     * @param httpConnectionPool Value to set for property 'httpConnectionPool'.
     */
    public void setHttpConnectionPool(HttpConnectionPool httpConnectionPool) {
        this.httpConnectionPool = httpConnectionPool;
    }

    @Override
    public String getKey() {
        return this.id;
    }


}
