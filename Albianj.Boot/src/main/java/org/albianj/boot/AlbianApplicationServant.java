package org.albianj.boot;

import org.albianj.boot.except.ThrowableServant;
import org.albianj.boot.helpers.*;
import org.albianj.boot.loader.BundleClassLoader;
import org.albianj.boot.logging.LogServant;
import org.albianj.boot.logging.LoggerLevel;
import org.albianj.boot.entry.BundleAttribute;

import java.util.HashMap;
import java.util.Map;

/**
 * 所有的albianj的应用程序主入口，管理albianj的进程
 * AlbianApplicationServant 每个进程有且只能有一个。
 * 每个AlbianApplication中有一个boot的bundle，使用bootContext来管理。
 * bootBundle是一个管理程序，主要管理各个bundle以及为各个bundle进行有限的功能性托底。
 * 还可能有n个bundle，使用bundle来管理。
 *
 * <pre>{@code
 * public class StartupClass {
 *      public static void main(String[] args){
 *          AlbianApplicationServant.Instance.setAppStartupClass(StartupClass.class)
 *                  .setWorkFolder("main/project/folder/")
 *                  .setLogger("path/to/logger/",true)
 *                  .addBundle("bundleName","path/to/bundle/",BundleLauncher.class);
 *                  .run(args);
 *      }
 * }
 * }</pre>
 *
 *
 */
public class AlbianApplicationServant {
    public static AlbianApplicationServant Instance = null;
    private Thread currentThread = null;

    public  final String BootBundleName = "BootBundle";

    static {
        if(null == Instance) {
            Instance = new AlbianApplicationServant();
        }
    }

    private Class<?> startupClass = null;
    private String workFolder = null;
    private String logsPath = null;
    private boolean isOpenConsole = false;
    private Map<String, BundleAttribute> attAttrs = new HashMap<>();
    private Map<String, BundleContext> bundleContextMap = new HashMap<>();

    protected AlbianApplicationServant() {
        currentThread = Thread.currentThread();
    }

    public AlbianApplicationServant setAppStartupClass(Class<?> startupClass){
        this.startupClass = startupClass;
        return this;
    }

    public AlbianApplicationServant setWorkFolder(String workFolder) {
        this.workFolder = workFolder;
        return this;
    }

    public AlbianApplicationServant setLogger(String logsPath,boolean isOpenConsole){
        this.logsPath = logsPath;
        this.isOpenConsole = isOpenConsole;
        return this;
    }

    public AlbianApplicationServant addBundle(String name,String workFolder,Class<? extends  IAlbianBundleLauncher> launcherClzz){
        BundleAttribute bundleAttr = new BundleAttribute(name,workFolder,launcherClzz);
        attAttrs.put(name,bundleAttr);
        return this;
    }


    /**
     * 得到当前线程所属BundleContext
     *
     * @return
     */
    public BundleContext getCurrentBundleContext() {
        String bundleName = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader.getClass().isAssignableFrom(BundleClassLoader.class)) {
            bundleName = ((BundleClassLoader) loader).getBundleName();
        } else {
            bundleName = BootBundleName;
        }
        return findBundleContext(bundleName, false);
    }

    /**
     * 得到当前线程所属BootBundleContext
     *
     * @return
     */
    public BundleContext getBootBundleContext() {
        return findBundleContext(BootBundleName, false);
    }


    /**
     * 找到bundle所属的上下文
     *
     * @param bundleName
     * @param isThrowIfBundleNotExit
     * @return
     */
    public BundleContext findBundleContext(String bundleName, boolean isThrowIfBundleNotExit) {
        if (isThrowIfBundleNotExit && (!isBundleExist(bundleName))) {
            ThrowableServant.Instance.throwDisplayException(this.getClass(),null,
                    "Not Found Bundle","Bundle -> {0} is not found.",bundleName);
        }
        return bundleContextMap.get(bundleName);
    }

    public boolean isBundleExist(String bundleName) {
        return bundleContextMap.containsKey(bundleName);
    }

    /**
     * 注销bundle，目前该方法还未实现，会抛出AlbianExterException
     * @param name
     */
    public void detachBundle(String name){
//        throw new DisplayException(ThrowableServant.Code.Error,"Function not implement.",
//                "detach bundle is not implement.");
    }

    public void run(String[] args){
        try {
            buildApplicationRuntime(args);
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean buildApplicationRuntime(String[] args){
        if(StringServant.Instance.isNullOrEmptyOrAllSpace(logsPath)){
            ThrowableServant.Instance.throwDisplayException(this.getClass(),null,
                    "Startup Argument Error.",
                    "Argument 'logsPath' is not setting and OPS(Yes,Have and only have caicai.) not allow use default value,so must setting it.");
        }

        /**
         * first init initLogger，but named Runtime and replace it when init end.
         */
        LogServant.Instance.newRuntimeLogger("Runtime",logsPath,"DEBUG",isOpenConsole);

        LogServant.Instance.addRuntimeLog("StartupThread", LoggerLevel.Info,
                this.getClass(),null,"Albianj Application Startup",null,
                "Albianj application startuping by using RuntimeLogger with folder -> {0} and {1} open ConsoleLogger.",
                logsPath, isOpenConsole ? "" : "not");

        if(StringServant.Instance.isNullOrEmptyOrAllSpace(workFolder)){
            if(null == startupClass){
                LogServant.Instance.addRuntimeLogAndThrow("StartupThread", LoggerLevel.Error,
                        this.getClass(),null,"Run Argument Error.",null,
                        "Application's workfolder or Class of main() function must set one.And we recommend set workfolder." );
            }

            String workFolder = TypeServant.Instance.classResourcePathToFileSystemWorkFolder(startupClass);
            if(!FileServant.Instance.isFileOrPathExist(workFolder)){
                LogServant.Instance.addRuntimeLogAndThrow("StartupThread", LoggerLevel.Error,
                        this.getClass(),null,"Run Argument Error.",null,
                       "Application's workfolder is not exist,then workpath -> {0} is setting by class -> {1}.",
                                workFolder,startupClass.getName());
            }
            this.workFolder = workFolder;
            LogServant.Instance.addRuntimeLog("StartupThread", LoggerLevel.Info,
                    this.getClass(),null,"Runtime Argument Defaulter.",null,
                    "Setting workFolder to -> {0} by class -> {1} default.",workFolder,startupClass.getName());
        } else {
            LogServant.Instance.addRuntimeLog("StartupThread", LoggerLevel.Info,
                    this.getClass(),null,"Runtime Settings.",null,
                    "Application startup at workFolder -> {0}.",workFolder);
        }

        this.workFolder = FileServant.Instance.makeFolderWithSuffixSep(this.workFolder);

        /**
         * special deal boot bundle
         */
        BundleContext bootCtx = BundleContext.newInstance("StartupThread", BootBundleName,workFolder,ClassLoader.getSystemClassLoader(),null);
        bundleContextMap.put(BootBundleName,bootCtx);
        BootServant.Instance.repair("StartupThread",bootCtx,logsPath,attAttrs);

        for(Map.Entry<String, BundleAttribute> entry : attAttrs.entrySet()) {
            BundleAttribute attr = entry.getValue();
            BundleContext bundleCtx = BundleContext.newInstance("StartupThread",attr.getName() ,attr.getWorkFolder(),null,attr.getStartupClassname());
            bundleContextMap.put(BootBundleName,bundleCtx);
            bundleCtx.launchBundle(args);
        }
        return true;
    }

    private boolean isWindows = false;
    public boolean isWindows(){
        return this.isWindows;
    }

    public void exitSystem(int st){
        try {
//            Runtime.getRuntime().getRuntime
            Thread.sleep(5000); //wait io flush
            currentThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(st);
    }
}
