package org.albianj.loader.entry;

import org.albianj.loader.IAlbianBundleLauncher;

public class AlbianBundleAttribute {
    private String name;
    private String workFolder;
    private String startupClassname;

    public AlbianBundleAttribute(String name, String workFolder, String startupClassname) {
        this.name = name;
        this.workFolder = workFolder;
        this.startupClassname = startupClassname;
    }

    public AlbianBundleAttribute(String name, String workFolder, Class<? extends IAlbianBundleLauncher> launcherClzz) {
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
