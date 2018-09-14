package org.albianj.loader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/*
 * class scanner
 * scan all class in the package with albian classload
 * and get the class metedata
 *
 * struct for HashMap :
 * key -> class-fullname,value -> metedata
 */
public class AlbianClassScanner {
    /**
     * 从包package中获取所有的Class
     *
     * @param pkgName
     * @return
     */
    public static HashMap<String, Object> filter(ClassLoader classLoader,
                                                       String pkgName,
                                                       IAlbianClassFilter filter,
                                                        IAlbianClassExcavator excavator)
            throws ClassNotFoundException, IOException {

        // 第一个class类的集合
        HashMap<String, Object> classes = new HashMap<String, Object>();
        // 是否循环迭代
        boolean recursive = true;
        String packageName = pkgName;
        String packageDirName = packageName.replace('.', '/');
        Enumeration<URL> dirs;
            dirs = classLoader.getResources(packageDirName);
            while (dirs.hasMoreElements()) {
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    findAndAddClassesInPackageByFile(classLoader, packageName,
                            filePath, recursive, classes, filter,excavator);
                } else if ("jar".equals(protocol)) {
                    JarFile jar;
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.charAt(0) == '/') {
                                name = name.substring(1);
                            }
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                if ((idx != -1) || recursive) {
                                    if (name.endsWith(".class") && !entry.isDirectory()) {
                                        String className = name.substring(
                                                packageName.length() + 1, name.length() - 6);
                                        loadClass(classLoader, filter, classes, packageName, className,excavator);
                                    }
                                }
                            }
                        }
                }
            }

        return classes;
    }

    private static void loadClass(ClassLoader classLoader,
                                  IAlbianClassFilter filter,
                                  HashMap<String,
                                          Object> classes,
                                  String packageName,
                                  String className,
                                  IAlbianClassExcavator excavator)
            throws ClassNotFoundException {
            String fullClassName = packageName + '.' + className;
            Class<?> cls = classLoader.loadClass(fullClassName);
            if (filter.verify(cls)) {
                Object info = excavator.finder(cls);
                classes.put(fullClassName, info);
            }
    }


    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(ClassLoader classLoader,
                                                        String packageName,
                                                        String packagePath,
                                                        final boolean recursive,
                                                        HashMap<String, Object> classes,
                                                        IAlbianClassFilter filter,
                                                        IAlbianClassExcavator excavator)
            throws ClassNotFoundException {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory()) ||
                        (file.getName().endsWith(".class"));
            }
        });
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(classLoader,
                        packageName + "." + file.getName(),
                        file.getAbsolutePath(),
                        recursive,
                        classes,
                        filter,excavator);
            } else {
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                loadClass(classLoader, filter, classes, packageName, className,excavator);
            }
        }
    }

}

