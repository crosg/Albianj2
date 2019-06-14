package org.albianj.loader;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 所有class文件在jvm中的元信息
 */
public class AlbianClassFileMetadata {
    /**
     * 文件内容
     */
    private byte[] fileContentBytes;
    /**
     * 完整带有命名空间的类名,带.class后缀名
     */
    private String fullClassName;
    /**
     * 完整带有命名空间的类名,不带.class后缀名
     */
    private String fullClassNameWithoutSuffix;
    /**
     * md5签名
     */
    private String md5;
    /**
     * class归属的parent名字
     * 可能为目录名或者是jar
     */
    private String parentFileName;

    /**
     * 归属是目录还是jar
     */
    private boolean isParentJar;

    /**
     * class加载的实体
     */
    private Class<?> clzz;
    /**
     * 带有.class后缀名和命名空间的完全文件名
     */
    private String fullFileName;

    public byte[] getFileContentBytes() {
        return fileContentBytes;
    }

    public void setFileContentBytes(byte[] fileContentBytes) {
        this.fileContentBytes = fileContentBytes;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getParentFileName() {
        return parentFileName;
    }

    public void setParentFileName(String parentFileName) {
        this.parentFileName = parentFileName;
    }

    public Class<?> getClzz() {
        return clzz;
    }

    public void setClzz(Class<?> clzz) {
        this.clzz = clzz;
    }

    public boolean isParentJar() {
        return isParentJar;
    }

    public void setParentJar(boolean parentJar) {
        isParentJar = parentJar;
    }

    public String getFullFileName() {
        return fullFileName;
    }

    public void setFullFileName(String fullFileName) {
        this.fullFileName = fullFileName;
    }

    public String getFullClassNameWithoutSuffix() {
        return fullClassNameWithoutSuffix;
    }

    public void setFullClassNameWithoutSuffix(String fullClassNameWithoutSuffix) {
        this.fullClassNameWithoutSuffix = fullClassNameWithoutSuffix;
    }


    /**
     *
     * @param fullFileName 带有.class后缀名和命名空间的完全文件名,文件系统格式
     * @param fileContentBytes 文件内容
     * @param parentFileName class归属的parent名字 可能为目录名或者是jar
     * @param isParentJar 归属是目录还是jar
     * @return
     */
    public static AlbianClassFileMetadata makeClassFileMetadata(String fullFileName, byte[] fileContentBytes, String parentFileName, boolean isParentJar){
        AlbianClassFileMetadata cfm = new AlbianClassFileMetadata();
        cfm.setFileContentBytes(fileContentBytes);
        cfm.setParentJar(isParentJar);
        cfm.setParentFileName(parentFileName);
        if(fullFileName.endsWith(".class")){
            cfm.setFullFileName(fullFileName);
            String ffn = fullFileName.replace(File.separator,".");
            cfm.setFullClassName(ffn);
            cfm.setFullClassNameWithoutSuffix(ffn.substring(0,fullFileName.lastIndexOf(".class")));
        } else {
            cfm.setFullFileName(fullFileName.concat(".class"));
            String ffn = fullFileName.replace(File.separator,".");
            cfm.setFullClassName(ffn.concat(".class"));
            cfm.setFullClassNameWithoutSuffix(ffn);
        }
        String md5 = md5(fileContentBytes);
        cfm.setMd5(md5);
        return cfm;
    }

    public static String md5(byte[] bytes) {
        byte[] secretBytes = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        String padStr = "00000000000000000000000000000000";
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        int len = 32 - md5code.length();
        padStr = padStr.substring(0,len);
        md5code = padStr + md5code;
        return md5code;
    }
}
