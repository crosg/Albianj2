package org.albianj.concurrent.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.albianj.concurrent.IAlbianThreadPoolService;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;

public class AlbianThreadPoolService extends FreeAlbianService implements
		IAlbianThreadPoolService {
	private ThreadPoolExecutor threadPool;

	public void loading() 
			throws RuntimeException,AlbianParserException {
		threadPool = new ThreadPoolExecutor(
				KernelSetting.getThreadPoolCoreSize(),
				KernelSetting.getThreadPoolMaxSize(), 300, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(KernelSetting
						.getThreadPoolMaxSize()
						- KernelSetting.getThreadPoolCoreSize()),
				new ThreadPoolExecutor.DiscardOldestPolicy());
		super.loading();
	}

	public void execute(Runnable event) {
		if (null == threadPool) {
			
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,"The thread pool is null.");
			return;
		}
		threadPool.execute(event);
	}

	public void unload() {
		if (null != threadPool)
			threadPool.shutdown();
		super.unload();
	}
}
