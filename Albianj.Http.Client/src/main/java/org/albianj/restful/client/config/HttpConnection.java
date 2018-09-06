package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXml2ObjectSigning;

/**
 * Created by xuhaifeng on 17/2/10.
 */
public class HttpConnection implements IAlbianXml2ObjectSigning {

    private int socketTimeout = 5;
    private int connectTimeout = 5;
    private int connectionRequestTimeout = 5;
    private int retry = 0;

    /**
     * Getter for property 'socketTimeout'.
     *
     * @return Value for property 'socketTimeout'.
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Setter for property 'socketTimeout'.
     *
     * @param socketTimeout Value to set for property 'socketTimeout'.
     */
    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    /**
     * Getter for property 'connectTimeout'.
     *
     * @return Value for property 'connectTimeout'.
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Setter for property 'connectTimeout'.
     *
     * @param connectTimeout Value to set for property 'connectTimeout'.
     */
    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    /**
     * Getter for property 'connectionRequestTimeout'.
     *
     * @return Value for property 'connectionRequestTimeout'.
     */
    public int getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    /**
     * Setter for property 'connectionRequestTimeout'.
     *
     * @param connectionRequestTimeout Value to set for property 'connectionRequestTimeout'.
     */
    public void setConnectionRequestTimeout(int connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    /**
     * Getter for property 'retry'.
     *
     * @return Value for property 'retry'.
     */
    public int getRetry() {
        return retry;
    }

    /**
     * Setter for property 'retry'.
     *
     * @param retry Value to set for property 'retry'.
     */
    public void setRetry(int retry) {
        this.retry = retry;
    }
}
