package org.albianj.loader.entry;

import java.util.Map;

public class AlbianBootAttribute {
    private String appName = "AlbianDefaultApp";
    private String machineId;
    private String machineKey = "wefet45y56gd&^%&$($$fbf943sf98^&*&*%$@%$34tksdjfvh823r2=sdfssdfsdp[sfshfwwefwffwe";
    private String runtimeLevel = "DEBUG";
    private boolean isWindows = true;

    private Map<String , IAlbianBundleLoggerAttribute> loggerAttrs;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineKey() {
        return machineKey;
    }

    public void setMachineKey(String machineKey) {
        this.machineKey = machineKey;
    }

    public String getRuntimeLevel() {
        return runtimeLevel;
    }

    public void setRuntimeLevel(String runtimeLevel) {
        this.runtimeLevel = runtimeLevel;
    }

    public Map<String, IAlbianBundleLoggerAttribute> getLoggerAttrs() {
        return loggerAttrs;
    }

    public void setLoggerAttrs(Map<String, IAlbianBundleLoggerAttribute> loggerAttrs) {
        this.loggerAttrs = loggerAttrs;
    }

    public boolean isWindows() {
        return isWindows;
    }

    public void setWindows(boolean isWindows) {
        isWindows = isWindows;
    }
}
