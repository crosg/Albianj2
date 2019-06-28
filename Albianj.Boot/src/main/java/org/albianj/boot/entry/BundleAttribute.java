package org.albianj.boot.entry;

import org.albianj.boot.IAlbianBundleLauncher;

public class BundleAttribute {
    private String name;
    private String workFolder;
    private String startupClassname;

    public BundleAttribute(String name, String workFolder, String startupClassname) {
        this.name = name;
        this.workFolder = workFolder;
        this.startupClassname = startupClassname;
    }

    public BundleAttribute(String name, String workFolder, Class<? extends IAlbianBundleLauncher> launcherClzz) {
        this.name = name;
        this.workFolder = workFolder;
        this.startupClassname = launcherClzz.getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkFolder() {
        return workFolder;
    }

    public void setWorkFolder(String workFolder) {
        this.workFolder = workFolder;
    }

    public String getStartupClassname() {
        return startupClassname;
    }

    public void setStartupClassname(String startupClassname) {
        this.startupClassname = startupClassname;
    }

}
