package org.albianj.loader;

/*
 * filter class for finding your class
 *
 */
public interface IAlbianClassFilter {
    public boolean verify(Class<?> cls);
}
