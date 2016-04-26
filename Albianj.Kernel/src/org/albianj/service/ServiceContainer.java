package org.albianj.service;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.albianj.logger.IAlbianLoggerService;


public class ServiceContainer {
	
	private static ConcurrentHashMap<String, IAlbianService> _container=  new ConcurrentHashMap<String, IAlbianService>();

	public static boolean existService(String id)
			throws IllegalArgumentException {
		return _container.contains(id);
	}

	// no synchronized
	public static IAlbianService getService(String id) throws IllegalArgumentException {
		return _container.get(id);
	}

	public synchronized static void addService(String id, IAlbianService value)
			throws IllegalArgumentException {
		if(null == id || null == value)
			throw new IllegalArgumentException("argument is null.");
		if(_container.containsKey(id)){
			_container.replace(id, value);
		} else {
			_container.put(id, value);
		}
	}

	public static void removeService(String id)
			throws IllegalArgumentException {
		//can not remove logger
		if(IAlbianLoggerService.Name.equals(id))
			return;
		_container.remove(id);
	}

	public static void clear() {
		_container.clear();
	}

	public static Set<String> getAllServiceNames() {
		return _container.keySet();
	}
}
