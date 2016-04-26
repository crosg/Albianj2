package org.albianj.concurrent;

import org.albianj.service.IAlbianService;

public interface IAlbianThreadPoolService extends IAlbianService {
	public static final String Name = "AlbianThreadPoolService";
	public void execute(Runnable event);
}
