package org.albianj.kernel;

import java.io.File;

import org.albianj.verify.Validate;

public class KernelSetting {
	public static final int Windows = 1;
	public static final int Linux = 0;
	
	private static String kernelId = null;
	private static String appName = null;
	private static int threadPoolCoreSize = 5;
	private static int threadPoolMaxSize = 20;
	private static AlbianLevel level = AlbianLevel.Release;
	private static AlbianStartupMode mode = AlbianStartupMode.Normal;
	private static String fpath = "../config/";
	private static String kernelpath = "../config/";
	private static int system = 0;

	public static String getKernelId() {
		return kernelId;
	}

	public static void setKernelId(String kernelId) {
		KernelSetting.kernelId = kernelId;
	}

	public static void setAppName(String appName) {
		KernelSetting.appName = appName;
	}

	public static String getAppName() {
		return appName;
	}

	public static int getThreadPoolMaxSize() {
		return threadPoolMaxSize;
	}

	public static void setThreadPoolMaxSize(int threadPoolMaxSize) {
		KernelSetting.threadPoolMaxSize = threadPoolMaxSize;
	}

	public static int getThreadPoolCoreSize() {
		return threadPoolCoreSize;
	}

	public static void setThreadPoolCoreSize(int threadPoolCoreSize) {
		KernelSetting.threadPoolCoreSize = threadPoolCoreSize;
	}

	public static void setAlbianLevel(AlbianLevel l) {
		KernelSetting.level = l;
	}

	public static AlbianLevel getAlbianLevel() {
		return KernelSetting.level;
	}

	public static void setAlbianStartupMode(AlbianStartupMode m) {
		mode = m;
	}

	public static AlbianStartupMode getAlbianStartupMode() {
		return mode;
	}

	public static void setAlbianConfigFilePath(String fpath) {
		if (!Validate.isNullOrEmptyOrAllSpace(fpath)) {
			if (KernelSetting.Windows == KernelSetting.getSystem())
			{
				if (fpath.endsWith(KernelSetting.getPathSep()))
					KernelSetting.fpath = fpath;
				else
					KernelSetting.fpath = fpath + KernelSetting.getPathSep();
			} else {
				if (fpath.endsWith(KernelSetting.getPathSep()))
					KernelSetting.fpath = fpath;
				else
					KernelSetting.fpath = fpath + KernelSetting.getPathSep();
			}
		}
	}

	public static String getAlbianConfigFilePath() {
		return fpath;
	}

	public static void setAlbianKernelConfigFilePath(String fpath) {
		if (!Validate.isNullOrEmptyOrAllSpace(fpath)) {
			if (KernelSetting.Windows == KernelSetting.getSystem())
			{
				if (fpath.endsWith(KernelSetting.getPathSep()))
					KernelSetting.kernelpath = fpath;
				else
					KernelSetting.kernelpath = fpath + KernelSetting.getPathSep();
			} else {
				if (fpath.endsWith(KernelSetting.getPathSep()))
					KernelSetting.kernelpath = fpath;
				else
					KernelSetting.kernelpath = fpath + KernelSetting.getPathSep();
			}

		}
	}

	public static String getAlbianKernelConfigFilePath() {
		return kernelpath;
	}
	
	public static int getSystem(){
		return system;
	}
	
	public static void setSystem(int system){
		KernelSetting.system = system;
	}
	
	public static String getPathSep(){
		return File.separator;
	}
	
	public static String getLineDep(){
		 return System.getProperty("line.separator");
	}
	
	public static String getExtDir(){
		 return System.getProperty("java.ext.dirs");
	}
}
