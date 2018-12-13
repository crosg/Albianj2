package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.xml.IAlbianXml2ObjectSigning;

public class HttpClientSocketConfig implements IAlbianXml2ObjectSigning {
    @ConfigField2NodeRant(XmlNodeName = "NoDelay")
    private  boolean tcpNoDelay = true;
    @ConfigField2NodeRant()
    private  long backlogSizeB = -1;
    @ConfigField2NodeRant()
    private  long sndBufSizeB = -1;
    @ConfigField2NodeRant()
    private  long rcvBufSizeB = -1;
    @ConfigField2NodeRant()
    private  boolean soKeepAlive = true;
    @ConfigField2NodeRant()
    private  int soLinger = -1;
    @ConfigField2NodeRant()
    private  boolean soReuseAddress = true;
    @ConfigField2NodeRant()
    private  long soTimeoutMs = 1000 ;
    @ConfigField2NodeRant()
    private long bufferSizeB = -1;
    @ConfigField2NodeRant()
    private String charset = "UTF-8";
    @ConfigField2NodeRant()
    private long connectionRequestTimeoutMs;
    @ConfigField2NodeRant()
    private long connectTimeoutMs;
    @ConfigField2NodeRant()
    private boolean contentCompressionEnabled = true;
    @ConfigField2NodeRant()
    private long socketTimeoutMs = 1000;
    @ConfigField2NodeRant()
    private String networkInterface = "eth1";

    private boolean redirectsEnabled = false;

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public long getBacklogSizeB() {
        return backlogSizeB;
    }

    public void setBacklogSizeB(long backlogSizeB) {
        this.backlogSizeB = backlogSizeB;
    }

    public long getSndBufSizeB() {
        return sndBufSizeB;
    }

    public void setSndBufSizeB(long sndBufSizeB) {
        this.sndBufSizeB = sndBufSizeB;
    }

    public long getRcvBufSizeB() {
        return rcvBufSizeB;
    }

    public void setRcvBufSizeB(long rcvBufSizeB) {
        this.rcvBufSizeB = rcvBufSizeB;
    }

    public boolean isSoKeepAlive() {
        return soKeepAlive;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public int getSoLinger() {
        return soLinger;
    }

    public void setSoLinger(int soLinger) {
        this.soLinger = soLinger;
    }

    public boolean isSoReuseAddress() {
        return soReuseAddress;
    }

    public void setSoReuseAddress(boolean soReuseAddress) {
        this.soReuseAddress = soReuseAddress;
    }

    public long getSoTimeoutMs() {
        return soTimeoutMs;
    }

    public void setSoTimeoutMs(long soTimeoutMs) {
        this.soTimeoutMs = soTimeoutMs;
    }

    public long getBufferSizeB() {
        return bufferSizeB;
    }

    public void setBufferSizeB(long bufferSizeB) {
        this.bufferSizeB = bufferSizeB;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public long getConnectionRequestTimeoutMs() {
        return connectionRequestTimeoutMs;
    }

    public void setConnectionRequestTimeoutMs(long connectionRequestTimeoutMs) {
        this.connectionRequestTimeoutMs = connectionRequestTimeoutMs;
    }

    public long getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(long connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public boolean isContentCompressionEnabled() {
        return contentCompressionEnabled;
    }

    public void setContentCompressionEnabled(boolean contentCompressionEnabled) {
        this.contentCompressionEnabled = contentCompressionEnabled;
    }

    public boolean isRedirectsEnabled() {
        return redirectsEnabled;
    }

    public void setRedirectsEnabled(boolean redirectsEnabled) {
        this.redirectsEnabled = redirectsEnabled;
    }

    public long getSocketTimeoutMs() {
        return socketTimeoutMs;
    }

    public void setSocketTimeoutMs(long socketTimeoutMs) {
        this.socketTimeoutMs = socketTimeoutMs;
    }

    public String getNetworkInterface() {
        return networkInterface;
    }

    public void setNetworkInterface(String networkInterface) {
        this.networkInterface = networkInterface;
    }
}
