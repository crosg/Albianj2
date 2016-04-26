package org.albianj.configurtion.impl;

import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.configurtion.IAlbianConfigurtionService;
import org.albianj.service.AlbianServiceRouter;

public class AlbianConfigurtionCachedThread implements Runnable {
	private String key = null;
	private Object value = null;
	private boolean isDelete = false;
	
	public AlbianConfigurtionCachedThread(boolean isDelete,String key,Object value) {
		this.key = key;
		this.value = value;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class, IAlbianCachedService.Name);
		if (null != acs) {
			if(isDelete) {
				acs.delete(IAlbianConfigurtionService.AlbianConfigurtionCachedNameDefault, key);
			} else {
				acs.set(IAlbianConfigurtionService.AlbianConfigurtionCachedNameDefault, key, value);
			}
		}

	}

}
