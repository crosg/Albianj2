package org.albianj.loader;

import org.albianj.loader.entry.IAlbianBundleModuleConf;
import org.albianj.loader.entry.IAlbianBundlePooling;
import org.albianj.loader.except.AlbianExterException;
import org.albianj.loader.except.ExceptionUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * albianj bundle的上下文
 */
public class AlbianBundleContext {

    /**
     * bundle的名称
     */
    private String bundleName;

    /**
     * 该bundle的classloader
     */
    private ClassLoader classLoader;

    /**
     * 该bundle的配置文件项
     */
    private Map<String, IAlbianBundleService> bundleServiceMap;
    private Map<String,IAlbianBundleModuleConf> bundleConf;
    private Map<String, IAlbianBundlePooling> bundleDbPool;

    /**
     * 当前bundle的目录,肯定以File.separator结尾
     */
    private String workPath;

    private AlbianBundleContext(String bundleName, String workPath, ClassLoader loader){
        this.bundleName = bundleName;
        this.classLoader = loader;
        if(workPath.endsWith(File.separator)) {
            this.workPath = workPath;
        } else {
            this.workPath = workPath + File.separator;
        }
        bundleServiceMap = new HashMap<>();
        bundleConf = new HashMap<>();
        bundleDbPool = new HashMap<>();
    }

    public static AlbianBundleContext makeInstance(String bundleName, String workPath, ClassLoader loader){
        return new AlbianBundleContext(bundleName,workPath,loader);
    }

    public void addModuleConf(String moduleName, IAlbianBundleModuleConf conf){
        bundleConf.put(moduleName,conf);
    }

    public <T extends IAlbianBundleModuleConf> T getModuleConf(String moduleName) {
        if( bundleConf.containsKey(moduleName) ) {
            return (T) bundleConf.get(moduleName);
        }
        throw new AlbianExterException(ExceptionUtil.ExceptForError,
                "Module Conf not Exist.","moduleName -> ", moduleName," not exist.");
    }

    public <T extends IAlbianBundleModuleConf> T getModuleConfAndNewIfNotExist(String moduleName,Class<T> clzz) {
        if(bundleConf.containsKey(moduleName)) {
            return (T) bundleConf.get(moduleName);
        }else {
            T inst = null;
            try {
                inst = clzz.newInstance();
            } catch (Exception e) {
                throw new AlbianExterException(ExceptionUtil.ExceptForError,e,
                        "New Instance Eoor.","moduleName -> ", moduleName," new class -> ",clzz.getCanonicalName()," instance is fail.");
            }
            bundleConf.put(moduleName,inst);
            return inst;
        }
    }

    public ClassLoader getClassLoader(){
        return this.classLoader;
    }

    public IAlbianBundleService getBundleService(String serviceId){
        if (bundleServiceMap.containsKey(serviceId)) {
            return null;
        }
        return bundleServiceMap.get(serviceId);
    }

    public void addBundleService(String serviceId,IAlbianBundleService bundleService){
        bundleServiceMap.put(serviceId,bundleService);
    }

    public void cleanupBundleServices(){
        bundleServiceMap.clear();;
    }

    public boolean isExistConf(String moduleName){
        return bundleConf.containsKey(moduleName);
    }

    public String getWorkPath(){
        return this.workPath;
    }

    public String getBinPath(){
        return workPath + "bin" + File.pathSeparator ;
    }

    public String getLibPath(){
        return workPath + "lib" + File.pathSeparator ;
    }

    public String getClassesPath(){
        return workPath + "classes" + File.pathSeparator ;
    }

    public String getConfPath(){
        return workPath + "conf" + File.pathSeparator ;
    }

    public String getAppsPath(){
        return workPath + "apps" + File.pathSeparator ;
    }

    public String getBundleName(){
        return this.bundleName;
    }

    public IAlbianBundlePooling findDatabasePool(String name){
        return bundleDbPool.get(name);
    }

    public void addDatabasePoolIfNotExist(String name,IAlbianBundlePooling pool){
        synchronized (bundleDbPool) {
            if(!bundleDbPool.containsKey(name)) {
                bundleDbPool.put(name, pool);
            }
        }
    }
}
