package org.albianj.persistence.service;

import java.sql.Connection;

import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.service.parser.IAlbianParserService;

/**
 * albianj的数据存储解析类，即albianj的数据存储底层service。
 * 此类会在albianj启动的时候被加载（前提是在service.xml中配置了此服务）。
 * 配置方法：
 * <br/>
 * {@code
 * <xml>
 * 	<Service Id="AlbianStorageService" Type="org.albianj.persistence.impl.storage.AlbianStorageParserService" />
 * <xml>
 * }
 * 在加载的过程中，
 * 此类会解析config文件夹下的storage.xml文件，所有解析的路由信息都会被存在放当前进程中缓存。
 * 
 * 注意：此类为albianj数据层的必须service，使用albianj的数据层必须启动此serivce。
 * 
 * @author seapeak
 * @since v1.0
 *
 */
public interface IAlbianStorageParserService extends IAlbianParserService{
	
	/**
	 * 此service在service.xml中的id
	 */
	static  String Name = "AlbianStorageService";
	
	/**
	 * 增加storage的元信息
	 * @param name storage的名称
	 * @param sa storage的元信息
	 */
	public void addStorageAttribute(String name,IStorageAttribute sa);
	
	/**
	 * 根据storage的名称获取storage的元信息
	 * @param name storage的名称
	 * @return storage的元信息
	 */
	public IStorageAttribute getStorageAttribute(String name);
	
	/**
	 * 根据storage的元信息获取链接
	 * @param rsa
	 * @return
	 */
	public Connection getConnection(IRunningStorageAttribute rsa);
}
