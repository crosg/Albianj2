/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
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
					
					if(!Validate.isNullOrEmptyOrAllSpace(serviceAttr.getInterface())) {
						Class<?> itf = AlbianClassLoader.getInstance().loadClass(serviceAttr.getInterface());
						if(!itf.isAssignableFrom(cla)) {
							log.info(IAlbianLoggerService.AlbianRunningLoggerName,
									"init albian service :%s the class:%s is not implements from interface:%s.",
									serviceAttr.getId(), serviceAttr.getType(),serviceAttr.getInterface());
						}
						
						if(!IAlbianService.class.isAssignableFrom(itf)) {
							log.info(IAlbianLoggerService.AlbianRunningLoggerName,
									"init albian service :%s the interface:%s must implements from IAlbianService  .",
									serviceAttr.getId(), serviceAttr.getInterface());
							throw new  TypeNotPresentException(serviceAttr.getType(),null);
						}
					}
					
					if(!IAlbianService.class.isAssignableFrom(cla)) {
						log.info(IAlbianLoggerService.AlbianRunningLoggerName,
								"init albian service :%s the class:%s must implements from IAlbianService  .",
								serviceAttr.getId(), serviceAttr.getType());
						throw new  TypeNotPresentException(serviceAttr.getType(),null);
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
