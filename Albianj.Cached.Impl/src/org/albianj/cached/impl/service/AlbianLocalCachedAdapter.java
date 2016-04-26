package org.albianj.cached.impl.service;

import java.util.List;

import org.albianj.cached.attribute.IAlbianCachedAttribute;
import org.albianj.cached.impl.lcached.LocalCacheManager;
import org.albianj.cached.lcached.ILocalCached;
import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;

public class AlbianLocalCachedAdapter extends FreeAlbianService implements IAlbianCachedService {
	ILocalCached lc = null;
	
	@Override
	public void init(Object initObject) {
		// TODO Auto-generated method stub
		IAlbianCachedAttribute aca = (IAlbianCachedAttribute) initObject;
		LocalCacheManager.initializeCache(aca.getName(), 100000);
		lc = LocalCacheManager.getCache(aca.getName());
	}

	@Override
	public void set(String cachedName, String k, Object v) {
		// TODO Auto-generated method stub
		if(null == lc || Validate.isNullOrEmpty(k)) return;
		lc.put(k, v);
	}

	@Override
	public void set(String cachedName, String k, Object v, int tto) {
		if(null == lc || Validate.isNullOrEmpty(k)) return;
		// TODO Auto-generated method stub
		lc.put(k, v, tto * 1000);
	}

	@Override
	public void delete(String cachedName, String k) {
		if(null == lc || Validate.isNullOrEmpty(k)) return;
		// TODO Auto-generated method stub
		lc.remove(k);
	}

	@Override
	public boolean exist(String cachedName, String k) {
		if(null == lc || Validate.isNullOrEmpty(k)) return false;
		// TODO Auto-generated method stub
		return lc.containsKey(k);
	}

	@Override
	public <T> T get(String cachedName, String k,Class<T> cls) {
		if(null == lc || Validate.isNullOrEmpty(k)) return null;
		// TODO Auto-generated method stub
		return (T) lc.get(k);
	}

	@Override
	public boolean freeAll(String nodeName) {
		if(null == lc) return false;
		// TODO Auto-generated method stub
		 lc.clear();
		 return true;
	}

	@Override
	public Object getCachedClient(String cachedName) {
		// TODO Auto-generated method stub
		return lc;
	}

	@Override
	public <T> List<T> getArray(String cachedName, String k, Class<T> cls) {
		// TODO Auto-generated method stub
		return (List<T>) lc.get(k);
	}

	@Override
	public void returnCachedClient(String cachedName, Object client) {
		// TODO Auto-generated method stub
		return;
	}
	
	

}
