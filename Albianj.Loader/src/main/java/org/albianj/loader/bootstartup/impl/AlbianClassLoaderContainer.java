package org.albianj.loader.bootstartup.impl;

import org.albianj.loader.bootstartup.IAlbianClassLoaderContainer;

import java.util.Hashtable;

public class AlbianClassLoaderContainer implements IAlbianClassLoaderContainer {
    private ClassLoader classloader = null;
    private Hashtable metadata = null;

    public AlbianClassLoaderContainer(ClassLoader classloader) {
        this.classloader = classloader;
        this.metadata = new Hashtable();
    }

    @Override
    public ClassLoader getClassloader() {
        return classloader;
    }

    @Override
    public Hashtable getMetadata() {
        return metadata;
    }
}
