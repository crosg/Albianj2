package org.albianj.restful.client.config;

import org.albianj.xml.IAlbianXml2ObjectSigning;

/**
 * Created by xuhaifeng on 17/2/10.
 */
public class HttpConnectionPool implements IAlbianXml2ObjectSigning {
    private int totalMax;
    private int maxPerRoute;

    /**
     * Getter for property 'totalMax'.
     *
     * @return Value for property 'totalMax'.
     */
    public int getTotalMax() {
        return totalMax;
    }

    /**
     * Setter for property 'totalMax'.
     *
     * @param totalMax Value to set for property 'totalMax'.
     */
    public void setTotalMax(int totalMax) {
        this.totalMax = totalMax;
    }

    /**
     * Getter for property 'maxPerRoute'.
     *
     * @return Value for property 'maxPerRoute'.
     */
    public int getMaxPerRoute() {
        return maxPerRoute;
    }

    /**
     * Setter for property 'maxPerRoute'.
     *
     * @param maxPerRoute Value to set for property 'maxPerRoute'.
     */
    public void setMaxPerRoute(int maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }
}
