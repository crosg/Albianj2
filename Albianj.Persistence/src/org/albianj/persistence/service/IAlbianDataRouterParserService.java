package org.albianj.persistence.service;

import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.service.parser.IAlbianParserService;

/**
 * albianj的数据路由解析类，即albianj的数据路由底层service。
 * 此类会在albianj启动的时候被加载（前提是在service.xml中配置了此服务）。
 * 配置方法：
 * <br/>
 * {@code
 * <xml>
 * 	<Service Id="AlbianDataRouterService" Type="org.albianj.persistence.impl.routing.AlbianDataRouterParserService" />
 * <xml>
 * }
 * 在加载的过程中，
 * 此类会解析config文件夹下的drouter.xml文件，所有解析的路由信息都会被存在放当前进程中缓存。
 * 
 * 注意：此类为albianj数据层的必须service，使用albianj的数据层必须启动此serivce。
 * 
 * @author seapeak
 * @since v1.0
 *
 */
public interface IAlbianDataRouterParserService extends IAlbianParserService {
	
	/**
	 * 此service在service.xml中的id
	 */
	static  String Name = "AlbianDataRouterService";
	
	/**
	 * 增加一条路由信息
	 * @param name 需要路由实体的接口完全名
	 * @param dra 路由的元信息
	 * @since v2.0
	 */
	public void addDataRouterAttribute(String name,IDataRoutersAttribute dra);
	/**
	 * 获取指定接口的对象的路由信息
	 * @param 需要路由实体的接口完全名
	 * @return 路由的元信息
	 * @since v2.0
	 */
	public IDataRoutersAttribute getDataRouterAttribute(String name);
}
