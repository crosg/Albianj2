package org.albianj.kernel.impl;

import java.util.Properties;

import org.albianj.io.Path;
import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.AlbianStartupMode;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.PropertiesParser;

public class AlbianKernelParserService extends FreeAlbianParserService
 {
	private String file = "kernel.properties";

	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	public void init() {
		try {
			Properties props = PropertiesParser.load(Path
					.getExtendResourcePath(KernelSetting
							.getAlbianKernelConfigFilePath() + file));
			parser(props);
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
					RuntimeException.class, e, "Kernel is error.", 
					"load the kernel properties is error.");
		}
	}

	public void parser(Properties props) {
		String id = PropertiesParser.getValue(props, "Id");
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			KernelSetting.setKernelId("001");
		} else {
			KernelSetting.setKernelId(id);
		}
		
		// KernelSetting.setKernelKey(PropertiesParser.getValue(props, "Key"));
		String appName = PropertiesParser.getValue(props, "AppName");
		if (!Validate.isNullOrEmptyOrAllSpace(appName)) {
			KernelSetting.setAppName(appName);
		}
		
		String coreSize = PropertiesParser
				.getValue(props, "ThreadPoolCoreSize");
		if (Validate.isNullOrEmptyOrAllSpace(coreSize)) {
			KernelSetting.setThreadPoolCoreSize(5);
		} else {
			KernelSetting.setThreadPoolCoreSize(new Integer(coreSize));
		}
		String maxSize = PropertiesParser.getValue(props, "ThreadPoolMaxSize");
		if (Validate.isNullOrEmptyOrAllSpace(maxSize)) {
			KernelSetting.setThreadPoolMaxSize(Runtime.getRuntime()
					.availableProcessors() * 2 + 1);
		} else {
			KernelSetting.setThreadPoolMaxSize(new Integer(maxSize));
		}

		String sLevel = PropertiesParser.getValue(props, "Level");
		if (Validate.isNullOrEmptyOrAllSpace(sLevel)
				|| sLevel.equalsIgnoreCase("debug")) {
			KernelSetting.setAlbianLevel(AlbianLevel.Debug);
		} else {
			KernelSetting.setAlbianLevel(AlbianLevel.Release);
		}

		String sMode = PropertiesParser.getValue(props, "StartupMode");
		if (Validate.isNullOrEmptyOrAllSpace(sMode)
				|| sMode.equalsIgnoreCase("normal")) {
			KernelSetting.setAlbianStartupMode(AlbianStartupMode.Normal);
		} else {
			KernelSetting.setAlbianStartupMode(AlbianStartupMode.Async);
		}
	}

	
}
