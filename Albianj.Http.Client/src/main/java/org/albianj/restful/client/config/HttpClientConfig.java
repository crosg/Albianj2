package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigClass2NodeRant;
import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.pooling.IPoolingObjectConfig;
import org.albianj.xml.IAlbianXml2ObjectSigning;

@ConfigClass2NodeRant(XmlNodeName = "HttpClient")
public class HttpClientConfig implements IAlbianXml2ObjectSigning , IPoolingObjectConfig {

    @ConfigField2NodeRant()
    private String site;

    @ConfigField2NodeRant()
    private String host;

    @ConfigField2NodeRant(XmlNodeName = "Pooling",NewIfNoXmlNode = true)
    private HttpEntityPoolingConfig httpClientPoolingConfig;

    @ConfigField2NodeRant(XmlNodeName = "Headers")
    private HttpClientHeadersConfig httpClientHeadersConfig;

    @ConfigField2NodeRant(XmlNodeName = "SSL")
    private HttpClientSSLConfig httpClientSSLConfig;

    @ConfigField2NodeRant(XmlNodeName = "Socket",NewIfNoXmlNode = true)
    private HttpClientSocketConfig httpClientSocketConfig;

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public HttpEntityPoolingConfig getHttpClientPoolingConfig() {
        return httpClientPoolingConfig;
    }

    public void setHttpClientPoolingConfig(HttpEntityPoolingConfig httpClientPoolingConfig) {
        this.httpClientPoolingConfig = httpClientPoolingConfig;
    }

    public HttpClientHeadersConfig getHttpClientHeadersConfig() {
        return httpClientHeadersConfig;
    }

    public void setHttpClientHeadersConfig(HttpClientHeadersConfig httpClientHeadersConfig) {
        this.httpClientHeadersConfig = httpClientHeadersConfig;
    }

    public HttpClientSSLConfig getHttpClientSSLConfig() {
        return httpClientSSLConfig;
    }

    public void setHttpClientSSLConfig(HttpClientSSLConfig httpClientSSLConfig) {
        this.httpClientSSLConfig = httpClientSSLConfig;
    }

    public HttpClientSocketConfig getHttpClientSocketConfig() {
        return httpClientSocketConfig;
    }

    public void setHttpClientSocketConfig(HttpClientSocketConfig httpClientSocketConfig) {
        this.httpClientSocketConfig = httpClientSocketConfig;
    }
}
