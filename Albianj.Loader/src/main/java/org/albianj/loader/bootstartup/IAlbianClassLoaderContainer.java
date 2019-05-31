package org.albianj.loader.bootstartup;

import java.util.Hashtable;

public interface IAlbianClassLoaderContainer {
    ClassLoader getClassloader();

    Hashtable getMetadata();
}
