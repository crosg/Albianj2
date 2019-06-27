package org.albianj.boot;

import org.albianj.boot.except.entry.BootAttribute;
import org.albianj.boot.loader.BundleClassLoader;
import org.albianj.boot.logging.LogServant;
import org.albianj.boot.logging.LoggerLevel;
import org.albianj.boot.logging.ILogger;
import org.albianj.boot.tags.BundleSharingTag;

import java.io.File;
import java.lang.reflect.Method;

/**
 * albianj bundle的上下文
 */
@BundleSharingTag
public class BundleContext {

    /**
     * bundle的名称
     */
    private String bundleName;

    /**
     * 该bundle的classloader
     */
    private ClassLoader classLoader;

    private ThreadGroup threadGroup;

    /**
     * bundle的启动器
     *
     */
    private String launcher;

    private ILogger rtLogger;

    private BootAttribute attr;

    /**
     * 当前bundle的目录,肯定以File.separator结尾
     */
    private String workPath;

    private String binFolder;
    private String classesFolder;
    private String libFolder;
    private String confFolder;
    private String appsFolder;

    private BundleContext(String sessionId, String bundleName, String workPath, ClassLoader loader, String launcher) {
        this.bundleName = bundleName;
        if(null == loader) {
            BundleClassLoader classLoader = BundleClassLoader.makeInstance(bundleName);
            this.classLoader = classLoader;
        }
        if (workPath.endsWith(File.separator)) {
            this.workPath = workPath;
        } else {
            this.workPath = workPath + File.separator;
        }
        threadGroup = new ThreadGroup(bundleName);
        threadGroup.setDaemon(true);
        this.launcher = launcher;
        this.binFolder = this.workPath + "bin" + File.separator;
        this.libFolder = this.workPath + "lib" + File.separator;
        this.classesFolder = this.workPath + "classes" + File.separator;
        this.confFolder = this.workPath + "conf" + File.separator;
        this.appsFolder = this.workPath + "apps" + File.separator;

        LogServant.Instance.addRuntimeLog(sessionId, LoggerLevel.Info,this.getClass(),
                null,"Bundle Runtime Settings.",null,
                "Application startup at bin folder -> {0},lib folder -> {1},classes folder -> {2},conf folder -> {3},apps folder -> {4}.",
                this.binFolder,this,libFolder,this.classesFolder,this.confFolder,this.appsFolder);

    }

    public static BundleContext newInstance(String sessionid, String bundleName, String workPath, ClassLoader loader, String launcher) {
        return new BundleContext(sessionid,bundleName, workPath, loader,launcher);
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public String getWorkPath() {
        return this.workPath;
    }

    public String getBinPath() {
        return workPath + "bin" + File.pathSeparator;
    }

    public String getLibPath() {
        return workPath + "lib" + File.pathSeparator;
    }

    public String getClassesPath() {
        return workPath + "classes" + File.pathSeparator;
    }

    public String getConfPath() {
        return workPath + "conf" + File.pathSeparator;
    }

    public String getAppsPath() {
        return workPath + "apps" + File.pathSeparator;
    }

    public String getBundleName() {
        return this.bundleName;
    }

    public ThreadGroup getThreadGroup(){
        return this.threadGroup;
    }

    public BundleThread newThread(String name, Runnable func){
        return new BundleThread(this,name,func);
    }

    public ILogger getRuntimeLogger() {
        return rtLogger;
    }

    public void setRuntimeLogger(ILogger rtLogger) {
        this.rtLogger = rtLogger;
    }

    public String findConfigFile(String simpleFileName){
        return this.confFolder + simpleFileName;
    }

    public String getLauncherClass() {
        return launcher;
    }

    public void launchBundle(final String[] args){
        BundleThread thread = null;
        try {
            thread = newThread(this.bundleName, new Runnable() {
                @Override
                public void run() {
                    BundleContext ctx = AlbianApplicationServant.Instance.findBundleContext(bundleName, true);
                    String clzzName = ctx.getLauncherClass();
                    try {
                        Class<?> clzz = ctx.getClassLoader().loadClass(clzzName);
                        Object launcher =  clzz.newInstance();
                        Method startup = null;
                        startup = clzz.getMethod("startup",String[].class);
                        startup.invoke(launcher,args);
                        LogServant.Instance.addRuntimeLogAndThrow("LaunchThread", LoggerLevel.Info,
                                this.getClass(),null,"Bundle launcher.",null,
                                "Startup bundle -> {0} with class -> {1} success.",
                                bundleName, clzzName);

                    } catch (Exception e) {
                        LogServant.Instance.addRuntimeLogAndThrow("LaunchThread", LoggerLevel.Info,
                                this.getClass(),e,"Bundle launcher error.",null,
                                "Startup bundle -> {0} with class -> {1} is error.",
                                bundleName, clzzName);
                    }
                }
            });
        }catch (Exception e){
            LogServant.Instance.addRuntimeLogAndThrow("LaunchThread", LoggerLevel.Info,
                    this.getClass(),e,"Bundle launcher error.",null,
                    "Open bundle thread to startup bundle -> {0} is error.",
                    bundleName);
            return;
        }
        if(null != thread) {
            LogServant.Instance.addRuntimeLogAndThrow("LaunchThread", LoggerLevel.Info,
                    this.getClass(),null,"Bundle launcher.",null,
                    "Startup thread of bundle -> {0}....", bundleName);
            thread.start();
        }
    }

    public BootAttribute getAttr() {
        return attr;
    }

    public void setAttr(BootAttribute attr) {
        this.attr = attr;
    }
}
