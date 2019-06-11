package org.albianj.loader;

import org.albianj.loader.AlbianBundleContext;

public interface IAlbianBundleService {
     AlbianBundleContext getBundleContext();
     void setBundleContext(AlbianBundleContext bundleContext);
}
