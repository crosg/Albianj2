package org.albianj.restful.client.config;

import org.albianj.config.parser.ConfigField2NodeRant;
import org.albianj.xml.IAlbianXml2ObjectSigning;

public class HttpClientSSLConfig implements IAlbianXml2ObjectSigning {
    @ConfigField2NodeRant()
    private boolean enable = false;
    @ConfigField2NodeRant()
    private boolean byPass = true;
    @ConfigField2NodeRant()
    private String keyStorePath ;
    @ConfigField2NodeRant()
    private String keyStorePwd;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isByPass() {
        return byPass;
    }

    public void setByPass(boolean byPass) {
        this.byPass = byPass;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public void setKeyStorePath(String keyStorePath) {
        this.keyStorePath = keyStorePath;
    }

    public String getKeyStorePwd() {
        return keyStorePwd;
    }

    public void setKeyStorePwd(String keyStorePwd) {
        this.keyStorePwd = keyStorePwd;
    }
}
