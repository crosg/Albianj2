package org.albianj.loader;

import org.albianj.loader.entry.AlbianBootAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * albian boot的context
 */
public class AlbianBootContext {

    /**
     * 一个进程只有一个boot context
     */
    public static AlbianBootContext Instance = null;
    static {
        Instance = new AlbianBootContext();
    }

    private AlbianBootAttribute bootAttribute = null;
    private Map<String, AlbianBundleContext> bundleContainerMap = null;
    private AlbianBootContext(){
        bundleContainerMap = new HashMap<>();
    }

    public boolean isExistBundle(String bundleName){
        return bundleContainerMap.containsKey(bundleName);
    }

    public void addBundle(String bundleName, AlbianBundleContext bundleContext){
        bundleContainerMap.put(bundleName,bundleContext);
    }

    public void deleteBundle(String bundleName){
        bundleContainerMap.remove(bundleName);
    }

    /**
     * 找到bundle所属的上下文
     * @param bundleName
     * @param isThrowIfBundleNotExit
     * @return
     */
    public AlbianBundleContext findBundleContext(String bundleName,boolean isThrowIfBundleNotExit){
        if(isThrowIfBundleNotExit && (!isExistBundle(bundleName))){
            throw new RuntimeException("Bundle -> " + bundleName +" is not exist.");
        }
        return bundleContainerMap.get(bundleName);
    }

    /**
     * 找到bundleName所属的上下文，如果没有找到就新建一个
     * @param bundleName
     * @param workPath
     * @return
     */
    public AlbianBundleContext findBundleContextOrNewIfNotExit(String bundleName, String workPath){
        if(bundleContainerMap.containsKey(bundleName)) {
            return bundleContainerMap.get(bundleName);
        }
        AlbianBundleClassLoader classLoader = AlbianBundleClassLoader.makeInstance(bundleName);
        Thread.currentThread().setContextClassLoader(classLoader);
        AlbianBundleContext bundleContainer = AlbianBundleContext.makeInstance(bundleName,workPath,classLoader);
        addBundle(bundleName,bundleContainer);
        classLoader.loadAllClass(bundleContainer.getBinPath(),bundleContainer.getClassesPath(),bundleContainer.getLibPath());
        return bundleContainer;
    }

    /**
     * 得到当前线程所属的BundleName
     * 当当前线程没有设置的时候，返回默认root
     * @return
     */
//    public String getCurrentBundleName(){
//        ClassLoader loader =  Thread.currentThread().getContextClassLoader();
//        if(loader.getClass().isAssignableFrom(AlbianBundleClassLoader.class)) {
//            return ((AlbianBundleClassLoader) loader).getBundleName();
//        }
//        return AlbianBootService.RootBundleName;
//    }

    /**
     * 得到当前线程所属BundleContext
     * @return
     */
    public AlbianBundleContext getCurrentBundleContext(){
        String bundleName = null;
        ClassLoader loader =  Thread.currentThread().getContextClassLoader();
        if(loader.getClass().isAssignableFrom(AlbianBundleClassLoader.class)) {
            bundleName = ((AlbianBundleClassLoader) loader).getBundleName();
        }
        bundleName = AlbianBootService.RootBundleName;
       return findBundleContext(bundleName,false);
    }



    AlbianBootAttribute findBootAttrOrNewIfNotExist() {
        if(null == this.bootAttribute){
            this.bootAttribute = new AlbianBootAttribute();
        }
        return this.bootAttribute;
    }

    public AlbianBootAttribute findBootAttr() {

        return this.bootAttribute;
    }

    boolean needBootStart(){
        return null == this.bootAttribute;
    }

}
