package org.albianj.boot.helpers;

import org.albianj.boot.tags.BundleSharingTag;

import java.io.*;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.jar.JarInputStream;

@BundleSharingTag
public class TypeServant {
    public static TypeServant Instance = null;

    static {
        if(null == Instance) {
            Instance = new TypeServant();
        }
    }

    protected TypeServant() {

    }

    /**
     * 包括包名的class名转换成文件系统的文件名，如果后缀没有.class,会加后缀
     * @param fullClassName
     * @return
     */
    public  String fullClassNameToClassFileName(String fullClassName){
        String fsFilename =  fullClassName.replace('.', File.separatorChar) ;
        if(!fsFilename.endsWith(".class")) {
            return fsFilename +".class";
        }
        return fsFilename;
    }

    public String fullClassNameToClassFullFileName(String rootFolder,String fullClassName){
        if(rootFolder.endsWith(File.separator)) {
            return rootFolder + fullClassNameToClassFileName(fullClassName);
        }
        return  rootFolder + File.separator + fullClassNameToClassFileName(fullClassName);
    }

    public byte[] getClassFileBytes(String rootFolder,String fullClassName){
        String classFileName = fullClassNameToClassFullFileName(rootFolder,fullClassName);
        if(!FileServant.Instance.isFileOrPathExist(classFileName)) {
            return null;
        }
        return FileServant.Instance.getFileBytes(classFileName);
    }

    public String fullClassNameToSimpleClassName(String fullClassName){
        String name = fullClassName;
        if(name.endsWith(".class")){
            name = name.substring(0,name.lastIndexOf(".class") - 1);
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public String getSimpleClassName(Class<?> clzz){
        String name = clzz.getSimpleName();
        return name;
    }

    public byte[] readJarBytes(JarInputStream jis) throws IOException {
        int len = 0;
        byte[] bytes = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
        while ((len = jis.read(bytes, 0, bytes.length)) != -1) {
            baos.write(bytes, 0, len);
        }
        return baos.toByteArray();
    }

    /**
     * 从给定的类（一般为带有Main函数的类）来获取程序的运行时项目工程路径
     * @param clzz 带有main函数的启动类
     * @return
     */
    public String  classResourcePathToFileSystemWorkFolder(Class<?> clzz){
        //from jar : jar:file:/mnt/d/tmp/clTest/out/artifacts/unnamed/unnamed.jar!/org/cltest/
        //from class file:/D:/tmp/clTest/out/production/clTest/org/cltest/
        String url = clzz.getResource("").toString();
        String path = null;
        if(url.startsWith("jar:")) {
            int jarSepIdx = url.lastIndexOf("!");
            path = url.substring(9,jarSepIdx);//jar proto must with file proto
            path = path.substring(0, path.lastIndexOf("/",path.lastIndexOf(".")-1));
            return path.replace("/",File.separator);
        }
        //file proto
        String pkgName = clzz.getPackage().getName();
        pkgName = pkgName.replace(".","/");
        // maybe in the linux,substring begin index is 5
        path = url.substring(6,url.lastIndexOf(pkgName));
        return path.replace("/",File.separator);
    }

    /**
     * file协议的路径转换成文件系统的路径
     * @return
     */
    public String fileProtoUrl2FileSystemPath(Class<?> clzz){
        String simpleClassname = clzz.getSimpleName();
        URL url = clzz.getResource("");
        String filename = url.toString() + simpleClassname + ".class";
        if(filename.startsWith("file:")) {
            filename = filename.substring(filename.indexOf(":") + 2);
            filename = filename.replace("/",File.separator);
        }
        return filename;
    }

    /**
     * 是否是普通类
     * @param type
     * @return
     */
    public boolean isNormalClass(Class<?> type){
        int mod = type.getModifiers();
        return (!Modifier.isAbstract(mod)) && (!Modifier.isInterface(mod));
    }



}
