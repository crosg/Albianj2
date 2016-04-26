package org.albianj.kernel.impl;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.albianj.kernel.AlbianKernel;
import org.albianj.kernel.AlbianStartupMode;
import org.albianj.kernel.AlbianState;
import org.albianj.kernel.IAlbianTransmitterService;
import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.kernel.KernelSetting;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianServiceParser;
import org.albianj.service.IAlbianService;
import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.service.ServiceAttributeMap;
import org.albianj.service.ServiceContainer;
import org.albianj.service.impl.AlbianServiceParser;
import org.albianj.service.parser.IAlbianParserService;
import org.albianj.verify.Validate;

/**
 * 
 * @author Seapeak
 * 
 */
@AlbianKernel
public class AlbianTransmitterService implements IAlbianTransmitterService {
	private static AlbianState state = AlbianState.Normal;
	private static Date startDateTime;
	private static String serialId;

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#getStartDateTime()
	 */
	@Override
	public Date getStartDateTime() {
		return startDateTime;
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#getSerialId()
	 */
	@Override
	public String getSerialId() {
		return serialId;
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#getLifeState()
	 */
	@Override
	public AlbianState getLifeState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#start(java.lang.String)
	 */
	@Override
	public void start(String configUrl) throws Exception {
		makeEnvironment();
		KernelSetting.setAlbianConfigFilePath(configUrl);
		start();
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#start(java.lang.String, java.lang.String)
	 */
	@Override
	public void start(String kernelpath, String configPath)
			throws Exception {
		makeEnvironment();
		KernelSetting.setAlbianConfigFilePath(configPath);
		KernelSetting.setAlbianKernelConfigFilePath(kernelpath);
		start();
	}
	
	@Override
	public void start() throws Exception {
		makeEnvironment();
		startDateTime = new Date();
		Class<?> cls = AlbianClassLoader.getInstance().loadClass("org.albianj.kernel.impl.AlbianLogicIdService");
		if(null == cls){
			state = AlbianState.Unloaded;
			System.err.println("no the logic id plugin and exit.");
			return;
		}
		IAlbianLogicIdService lid =(IAlbianLogicIdService) cls.newInstance();
		if(null == lid){
			state = AlbianState.Unloaded;
			System.err.println("can not new the logger instance and exit.");
			return;
		}
		ServiceContainer.addService(IAlbianLogicIdService.Name, lid);
		serialId = lid.generate32UUID();
		state = AlbianState.Initing;
		IAlbianParserService parser = new AlbianServiceParser();
		parser.init();

//		if (KernelSetting.getAlbianStartupMode() == AlbianStartupMode.Async) {
//			System.out.println("startup albianj with async.");
//			Thread thread = new ServiceThread(this);
//			thread.start();
//		} else {
			System.out.println("startup albianj with normal.");
			doStart();
//		}
	}

	public void doStart() throws Exception {
		//the logger is essential module so must init first
		Class<?> cls = AlbianClassLoader.getInstance().loadClass("org.albianj.logger.impl.AlbianLoggerService");
		if(null == cls){
			state = AlbianState.Unloaded;
			System.err.println("no the logger plugin and exit.");
			return;
		}
		IAlbianLoggerService log =(IAlbianLoggerService) cls.newInstance();
		if(null == log){
			state = AlbianState.Unloaded;
			System.err.println("can not new the logger instance and exit.");
			System.exit(1);
		}
		try {
			log.beforeLoad();
			log.loading();
			log.afterLoading();
		}catch (Exception e){
			state = AlbianState.Unloaded;
			System.err.println("loading logger is fail and exit.");
			return;
		}
		ServiceContainer.addService(IAlbianLoggerService.Name, log);
		
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, IAlbianServiceAttribute> map = (LinkedHashMap<String, IAlbianServiceAttribute>) ServiceAttributeMap
				.get(FreeAlbianServiceParser.ALBIANJSERVICEKEY);
		LinkedHashMap<String, IAlbianServiceAttribute> failMap = new LinkedHashMap<String, IAlbianServiceAttribute>();
		int lastFailSize = 0;
		int currentFailSize = 0;
		Exception e = null;
		while (true) {
			lastFailSize = currentFailSize;
			currentFailSize = 0;
			for (Map.Entry<String, IAlbianServiceAttribute> entry : map
					.entrySet()) {
				try {
					IAlbianServiceAttribute serviceAttr = entry.getValue();
					boolean b = AlbianClassLoader.getInstance().existClass(serviceAttr.getType());
					Class<?> cla = AlbianClassLoader.getInstance().loadClass(serviceAttr.getType());
					if(null == cla){
						log.info(IAlbianLoggerService.AlbianRunningLoggerName,
								"the class:%s is not found.",serviceAttr.getType());
						throw new  ClassNotFoundException(serviceAttr.getType());
					}	
					IAlbianService service = (IAlbianService) cla.newInstance();
					service.beforeLoad();
					service.loading();
					service.afterLoading();
					ServiceContainer.addService(entry.getKey(), service);
				} catch (Exception exc) {
					e = exc;
					currentFailSize++;
					failMap.put(entry.getKey(), entry.getValue());
				}
			}
			if (0 == currentFailSize) {
				// if open the distributed mode,
				// please contact to manager machine to logout the system.
				state = AlbianState.Running;
				log.info(IAlbianLoggerService.AlbianRunningLoggerName,"startup albianj engine is success!");
				break;// all success
			}

			if (lastFailSize == currentFailSize) {
				// startup the service fail in this times,
				// so throw the exception and stop the albianj engine
				state = AlbianState.Unloading;
				log
						.error(IAlbianLoggerService.AlbianRunningLoggerName,"startup albianj engine is fail,maybe cross refernce.");
				if(null != e){
					log.error(IAlbianLoggerService.AlbianRunningLoggerName,e, "the startup fail service:%1$s.",
							failMap.keySet().toString());
				}
				ServiceContainer.clear();
				state = AlbianState.Unloaded;
				throw e;
			} else {
				map.clear();
				map.putAll(failMap);
				failMap.clear();
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#requestHandlerContext()
	 */
	@Override
	public String requestHandlerContext() {
		if (AlbianState.Running != state) {
			return "Albian is not ready,Please wait a minute or contact administrators!";
		}
		return "";
	}

	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#unload()
	 */
	@Override
	public void unload() throws Exception {
		Set<String> keys = ServiceContainer.getAllServiceNames();
		for (String key : keys) {
			try {
				IAlbianService service = (IAlbianService) ServiceContainer.getService(key);
				service.beforeUnload();
				service.unload();
				service.afterUnload();
			} catch (Exception e) {
				IAlbianLoggerService log = AlbianServiceRouter.getService(IAlbianLoggerService.class, IAlbianLoggerService.Name);
				log.error(IAlbianLoggerService.AlbianRunningLoggerName,e, "unload the service is error.");
				e.printStackTrace();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.albianj.kernel.impl.IAlbianBootService#makeEnvironment()
	 */
	@Override
	public void makeEnvironment(){
		String system = System.getProperty("os.name");
		if (system.toLowerCase().contains("windows"))// start with '/'
		{
			KernelSetting.setSystem(KernelSetting.Windows);
		} else {
			KernelSetting.setSystem(KernelSetting.Linux);
		}
	}

}
