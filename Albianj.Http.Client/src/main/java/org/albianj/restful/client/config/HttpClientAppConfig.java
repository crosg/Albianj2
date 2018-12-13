package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.config.parser.ConfigClass2NodeRant;
import org.albianj.xml.IAlbianXml2ObjectSigning;

@ConfigClass2NodeRant(XmlNodeName = "HttpClientAppConfig",IsRoot = true)
public class HttpClientAppConfig implements IAlbianXml2ObjectSigning {

    @ConfigField2NodeRant(XmlNodeName = "HttpGet",NewIfNoXmlNode = true)
    private HttpEntityPoolingConfig httpGetPoolingConfig;

    @ConfigField2NodeRant(XmlNodeName = "HttpPost",NewIfNoXmlNode = true)
    private HttpEntityPoolingConfig httpPostPoolingConfig;

    @ConfigField2NodeRant(XmlNodeName = "HttpClients")
    private HttpClientsConfig httpClientsConfig;

    public HttpEntityPoolingConfig getHttpGetPoolingConfig() {
        return httpGetPoolingConfig;
    }

    public void setHttpGetPoolingConfig(HttpEntityPoolingConfig httpGetPoolingConfig) {
        this.httpGetPoolingConfig = httpGetPoolingConfig;
    }

    public HttpEntityPoolingConfig getHttpPostPoolingConfig() {
        return httpPostPoolingConfig;
    }

    public void setHttpPostPoolingConfig(HttpEntityPoolingConfig httpPostPoolingConfig) {
        this.httpPostPoolingConfig = httpPostPoolingConfig;
    }

    public HttpClientsConfig getHttpClientsConfig() {
        return httpClientsConfig;
    }

    public void setHttpClientsConfig(HttpClientsConfig httpClientsConfig) {
        this.httpClientsConfig = httpClientsConfig;
    }
}
