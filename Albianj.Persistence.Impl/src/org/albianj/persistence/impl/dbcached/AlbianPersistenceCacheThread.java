package org.albianj.persistence.impl.dbcached;

import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class AlbianPersistenceCacheThread implements Runnable {
	private String name = null;
	private String key = null;
	private Object value = null;
	private int tto = 300;
	
	public AlbianPersistenceCacheThread(String name,String key,Object value,int tto) {
		this.name = name;
		this.key = key;
		this.value = value;
		this.tto = tto;
	}

	@Override
	public void run() {
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class, IAlbianCachedService.Name);
		try{
			if (null != acs) {
					acs.set(Validate.isNullOrEmptyOrAllSpace(name)
							? IAlbianObject.AlbianObjectCachedNameDefault
									: name, key, value,tto);
			}
		}catch(Exception e){
			
		}
	}

}
