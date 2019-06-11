package org.albianj.loader;

public class AlbianBundleClassLoader extends ClassLoader{
    private String bundleName = null;
    private AlbianBundleClassLoader(String bundleName){
        this.bundleName = bundleName;
    }

    public static AlbianBundleClassLoader makeInstance(String bundleName){
        return new AlbianBundleClassLoader(bundleName);
    }

    public String getBundleName(){
        return this.bundleName;
    }

}
