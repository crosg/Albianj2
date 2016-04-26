package org.albianj.kernel.impl;

import org.albianj.kernel.IAlbianTransmitterService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;

public class ServiceThread extends Thread {
	IAlbianTransmitterService abs = null;
	public ServiceThread(IAlbianTransmitterService abs){
		this.abs = abs;
	}
	@Override
	public void run() {
		try {
			if(null == abs) {
				
			} else {
				abs.doStart();
			}
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e, "start service is fail.");
			e.printStackTrace();

		}
	}
}
