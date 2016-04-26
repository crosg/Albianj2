package org.albianj.cached.service;

import java.util.List;

import org.albianj.service.IAlbianService;


public interface IAlbianCachedService extends IAlbianService{
	static String Name = "AlbianCachedService";
	void init(Object initObject);
	
	void set(String cachedName,String k,Object v);
	void set(String cachedName,String k,Object v,int tto);
	void delete(String cachedName,String k);
	boolean exist(String cachedName,String k);
	<T> T get(String cachedName,String k,Class<T> cls);
	<T> List<T> getArray(String cachedName,String k,Class<T> cls);
	
	boolean freeAll(String nodeName);
	
	Object getCachedClient(String cachedName);
	void returnCachedClient(String cachedName, Object client);
}
