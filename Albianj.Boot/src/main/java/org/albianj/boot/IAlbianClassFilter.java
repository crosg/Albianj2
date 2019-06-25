package org.albianj.boot;

/*
 * filter class for finding your class
 *
 */
public interface IAlbianClassFilter {
    public boolean verify(Class<?> cls);
}
