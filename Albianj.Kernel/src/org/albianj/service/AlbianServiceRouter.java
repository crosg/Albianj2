package org.albianj.service;

import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.verify.Validate;

/**
 * albianj的service管理类，交由albianj托管的service全部由这个类提供获取service。
 */
public class AlbianServiceRouter extends ServiceContainer {

	public static IAlbianLoggerService getLogger() {
		return getService(IAlbianLoggerService.class, IAlbianLoggerService.Name,false);
	}
	
	public static IAlbianLogicIdService getLogIdService(){
		return getService(IAlbianLogicIdService.class, IAlbianLogicIdService.Name,false);
	}

	/**
	 * 获取service.xml中配置的service.
	 * 注意： 1：获取的service都是单例模式
	 *
	 * @param <T> 获取serivce的定义接口类
	 * @param cla  获取serivce的定义接口类的class信息
	 * @param id service。xml中配置的id
	 * @param isThrowIfException 是否在获取service出错或者没有获取service时候抛出异常，true为抛出异常；false不抛出异常，但是service返回null
	 * @return 返回获取的service
	 * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
	 */
	public static <T extends IAlbianService> T getSingletonService(Class<T> cla, String id, boolean isThrowIfException)
			throws IllegalArgumentException {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {

			getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
					"Kernel is error.", "service id is null or empty,and can not found.");
		}

		try {
			IAlbianService service = (IAlbianService) ServiceContainer.getService(id);
			if (null == service)
				return null;
			return cla.cast(service);
		} catch (IllegalArgumentException exc) {
			getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, exc, "Get service:%1$s is error.", id);

			if (isThrowIfException)
				throw exc;
		}
		return null;
	}

	/**
	 * 获取service.xml中配置的service.
	 * 注意： 1：获取的service都是单例模式
	 * 		2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
	 *
	 * @param <T> 获取serivce的定义接口类
	 * @param cla  获取serivce的定义接口类的class信息
	 * @param id service。xml中配置的id
	 * @return 返回获取的service，在获取service出错或者没有获取service时候抛出异常
	 * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
	 */
	public static <T extends IAlbianService> T getSingletonService(Class<T> cla, String id) {
		return getSingletonService(cla, id, false);
	}
	
	
	/**
	 * 获取service.xml中配置的service.
	 * 注意： 1：获取的service都是单例模式
	 * 		2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
	 *
	 * @param <T> 获取serivce的定义接口类
	 * @param cla  获取serivce的定义接口类的class信息
	 * @param id service。xml中配置的id
	 * @param isThrowIfException 是否在获取service出错或者没有获取service时候抛出异常，true为抛出异常；false不抛出异常，但是service返回null
	 * @return 返回获取的service
	 * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
	 */
	@Deprecated
	public static <T extends IAlbianService> T getService(Class<T> cla, String id, boolean isThrowIfException)
			throws IllegalArgumentException {
		if (Validate.isNullOrEmptyOrAllSpace(id)) {

			getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, IllegalArgumentException.class,
					"Kernel is error.", "service id is null or empty,and can not found.");
		}

		try {
			IAlbianService service = (IAlbianService) ServiceContainer.getService(id);
			if (null == service)
				return null;
			return cla.cast(service);
		} catch (IllegalArgumentException exc) {
			getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, exc, "Get service:%1$s is error.", id);

			if (isThrowIfException)
				throw exc;
		}
		return null;
	}

	/**
	 * 获取service.xml中配置的service.
	 * 注意： 1：获取的service都是单例模式
	 * 		2：这个方法已经被废弃，不再进行维护，请使用getSingletonService替代
	 *
	 * @param <T> 获取serivce的定义接口类
	 * @param cla  获取serivce的定义接口类的class信息
	 * @param id service。xml中配置的id
	 * @return 返回获取的service，在获取service出错或者没有获取service时候抛出异常
	 * @throws IllegalArgumentException id在service.xml中找不到或者是获取的service不能转换陈cla提供的class信息，将抛出遗产
	 */
	@Deprecated
	public static <T extends IAlbianService> T getService(Class<T> cla, String id) {
		return getService(cla, id, false);
	}
	
}
