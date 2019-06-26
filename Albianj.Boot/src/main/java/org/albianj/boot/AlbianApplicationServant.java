package org.albianj.boot;

import org.albianj.boot.helpers.*;
import org.albianj.boot.loader.AlbianBundleClassLoader;
import org.albianj.boot.logging.AlbianLogServant;
import org.albianj.boot.logging.AlbianLoggerLevel;
import org.albianj.boot.logging.IAlbianLoggerAttribute;
import org.albianj.boot.entry.AlbianBootAttribute;
import org.albianj.boot.entry.AlbianBundleAttribute;
import org.albianj.boot.except.AlbianExceptionServant;

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
    private Map<String,AlbianBundleAttribute> attAttrs = new HashMap<>();
    private Map<String,AlbianBundleContext> bundleContextMap = new HashMap<>();

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
        AlbianBundleAttribute bundleAttr = new AlbianBundleAttribute(name,workFolder,launcherClzz);
        attAttrs.put(name,bundleAttr);
        return this;
    }


    /**
     * 得到当前线程所属BundleContext
     *
     * @return
     */
    public AlbianBundleContext getCurrentBundleContext() {
        String bundleName = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader.getClass().isAssignableFrom(AlbianBundleClassLoader.class)) {
            bundleName = ((AlbianBundleClassLoader) loader).getBundleName();
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
    public AlbianBundleContext getBootBundleContext() {
        return findBundleContext(BootBundleName, false);
    }


    /**
     * 找到bundle所属的上下文
     *
     * @param bundleName
     * @param isThrowIfBundleNotExit
     * @return
     */
    public AlbianBundleContext findBundleContext(String bundleName, boolean isThrowIfBundleNotExit) {
        if (isThrowIfBundleNotExit && (!isBundleExist(bundleName))) {
            AlbianExceptionServant.Instance.throwDisplayException(AlbianExceptionServant.Code.Error,this.getClass(),null,
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
//        throw new AlbianDisplayException(AlbianExceptionServant.Code.Error,"Function not implement.",
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
        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(logsPath)){
            AlbianExceptionServant.Instance.throwDisplayException(AlbianExceptionServant.Code.Error,this.getClass(),null,
                    "Startup Argument Error.",
                    "Argument 'logsPath' is not setting and OPS(Yes,Have and only have caicai.) not allow use default value,so must setting it.");
        }

        /**
         * first init initLogger，but named Runtime and replace it when init end.
         */
        AlbianLogServant.Instance.newRuntimeLogger("Runtime",logsPath,"DEBUG",isOpenConsole);

        AlbianLogServant.Instance.addRuntimeLog("StartupThread", AlbianLoggerLevel.Info,
                this.getClass(),null,"Albianj Application Startup",null,
                "Albianj application startuping by using RuntimeLogger with folder -> {0} and {1} open ConsoleLogger.",
                logsPath, isOpenConsole ? "" : "not");

        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(workFolder)){
            if(null == startupClass){
                AlbianLogServant.Instance.addRuntimeLogAndThrow("StartupThread", AlbianLoggerLevel.Error,
                        this.getClass(),null,"Run Argument Error.",null,
                        "Application's workfolder or Class of main() function must set one.And we recommend set workfolder." );
            }

            String workFolder = AlbianClassServant.Instance.classResourcePathToFileSystemWorkFolder(startupClass);
            if(!AlbianFileServant.Instance.isFileOrPathExist(workFolder)){
                AlbianLogServant.Instance.addRuntimeLogAndThrow("StartupThread", AlbianLoggerLevel.Error,
                        this.getClass(),null,"Run Argument Error.",null,
                       "Application's workfolder is not exist,then workpath -> {0} is setting by class -> {1}.",
                                workFolder,startupClass.getName());
            }
            this.workFolder = workFolder;
            AlbianLogServant.Instance.addRuntimeLog("StartupThread",AlbianLoggerLevel.Info,
                    this.getClass(),null,"Runtime Argument Defaulter.",null,
                    "Setting workFolder to -> {0} by class -> {1} default.",workFolder,startupClass.getName());
        } else {
            AlbianLogServant.Instance.addRuntimeLog("StartupThread",AlbianLoggerLevel.Info,
                    this.getClass(),null,"Runtime Settings.",null,
                    "Application startup at workFolder -> {0}.",workFolder);
        }

        this.workFolder = AlbianFileServant.Instance.makeFolderWithSuffixSep(this.workFolder);

        /**
         * special deal boot bundle
         */
        AlbianBundleContext bootCtx = AlbianBundleContext.newInstance("StartupThread", BootBundleName,workFolder,ClassLoader.getSystemClassLoader(),null);
        bundleContextMap.put(BootBundleName,bootCtx);
        AlbianXmlParserContext bootConfCtx =  AlbianBootServant.Instance.loadBootConf("StartupThread",bootCtx);
        AlbianBootAttribute bootAttr = AlbianBootServant.Instance.parserBootBundleConf(bootConfCtx,logsPath);
        IAlbianLoggerAttribute logAttr = bootAttr.getRootLoggerAttr();
        AlbianLogServant.Instance.updateRuntimeLogger(logAttr.getLevel(),logAttr.isOpenConsole(),logAttr.getMaxFilesize());
        Map<String, AlbianBundleAttribute>  confBundlesAttr = bootAttr.getBundleAttrs();
        if(AlbianCollectServant.Instance.isNullOrEmpty(confBundlesAttr)) {
            attAttrs.putAll(confBundlesAttr);
        }

        for(Map.Entry<String,AlbianBundleAttribute> entry : attAttrs.entrySet()) {
            AlbianBundleAttribute attr = entry.getValue();
            AlbianBundleContext bundleCtx = AlbianBundleContext.newInstance("StartupThread",attr.getName() ,attr.getWorkFolder(),null,attr.getStartupClassname());
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
