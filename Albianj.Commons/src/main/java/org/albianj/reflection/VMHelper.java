package org.albianj.reflection;

import org.apache.commons.lang3.ClassUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by xuhaifeng on 16/10/20.
 */
public class VMHelper {

    public static ClassLoader getCurrentClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static Object getClass(String classname) {
        try {
            Class<?> cla = ClassUtils.getClass(getCurrentClassLoader(), classname);
            return cla;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }


    public static Object newInstance(String classname) {
        try {
            Class<?> cla = ClassUtils.getClass(getCurrentClassLoader(), classname);
            return cla.newInstance();
        } catch (ClassNotFoundException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static String fileSystemToPackage(String folder, String classname) {
        String pck = folder.replace(File.pathSeparatorChar, '.');
        if (pck.endsWith(".")) {
            return pck + classname;
        } else {
            return pck + "." + classname;
        }
    }

    /**
     * Return a resource bundle using the specified base name.
     *
     * @param baseName the base name of the resource bundle, a fully
     *                 qualified class name
     * @return a resource bundle for the given base name
     * @throws MissingResourceException if no resource bundle for the specified base name can
     *                                  be found
     */
    public static ResourceBundle getBundle(String baseName) {
        return getBundle(baseName, Locale.getDefault());
    }

    /**
     * Return a resource bundle using the specified base name and locale.
     *
     * @param baseName the base name of the resource bundle, a fully
     *                 qualified class name
     * @param locale   the locale for which a resource bundle is desired
     * @return a resource bundle for the given base name and locale
     * @throws MissingResourceException if no resource bundle for the specified base name can
     *                                  be found
     */
    public static ResourceBundle getBundle(String baseName, Locale locale) {
        ClassLoader classLoader = getCurrentClassLoader();
        return ResourceBundle.getBundle(baseName, locale, classLoader);
    }

    /**
     * 给指定对象的field赋值
     *
     * @param obj
     * @param f
     * @param value
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static void fieldSet(Object obj, Field f, Object value) throws IllegalArgumentException, IllegalAccessException {
        f.set(obj, value);
    }

    /**
     * 从指定的对象中获取指定field的值
     *
     * @param obj
     * @param f
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    public static Object fieldGet(Object obj, Field f) throws IllegalArgumentException, IllegalAccessException {
        return f.get(obj);
    }

    /**
     * 在指定的对象上调用指定函数
     *
     * @param obj
     * @param m
     * @param args
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    public static Object invoke(Object obj, Method m, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        return m.invoke(obj, args);
    }
}
