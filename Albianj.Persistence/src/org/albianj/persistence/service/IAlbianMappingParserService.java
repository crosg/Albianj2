package org.albianj.persistence.service;

import java.beans.PropertyDescriptor;

import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.service.parser.IAlbianParserService;

/**
 * albianj的数据实体解析类，即albianj的数据实体底层service。
 * 此类会在albianj启动的时候被加载（前提是在service.xml中配置了此服务）。
 * 配置方法：
 * <br/>
 * {@code
 * <xml>
 * 	<Service Id="AlbianMappingService" Type="org.albianj.persistence.impl.mapping.AlbianMappingParserService" />
 * <xml>
 * }
 * 在加载的过程中，
 * 此类会解析config文件夹下的persistence.xml文件，所有解析的路由信息都会被存在放当前进程中缓存。
 * 
 * 注意：此类为albianj数据层的必须service，使用albianj的数据层必须启动此serivce。
 * 
 * @author seapeak
 * @since v1.0
 *
 */
public interface IAlbianMappingParserService extends IAlbianParserService {
	
	/**
	 * 此service在service.xml中的id
	 */
	static String Name = "AlbianMappingService";
	
	/**
	 * 增加数据实体的类型信息
	 * @param name 实体的接口信息
	 * @param aba 类型信息
	 */
	public void addAlbianObjectAttribute(String name,IAlbianObjectAttribute aba);
	
	/**
	 * 获取数据实体的类型信息
	 * @param name 实体的接口信息
	 * @return 类型信息
	 */
	public IAlbianObjectAttribute getAlbianObjectAttribute(String name);
	
	/**
	 * 增加数据实体的类型和接口之间的关系
	 * @param type
	 * @param inter
	 */
	public void addAlbianObjectClassToInterface(String type,String inter);
	
	/**
	 * 根据实体类型获取实体的接口
	 * @param type 实体的具体类型
	 * @return 实体的接口
	 */
	public String getAlbianObjectInterface(String type);
	
	/**
	 * 增加数据实体的基本信息，该基本信息根据数据实体的类型反射而来
	 * @param type 需要反射的实体类
	 * @param pds 反射出来的数据实体信息
	 */
	public void addAlbianObjectPropertyDescriptor(String type,PropertyDescriptor[] pds);
	
	/**
	 * 根据数据实体类型，获取数据实体反射出来的信息
	 * @param type 需要反射的实体类
	 * @return 反射出来的数据实体信息
	 */
	public PropertyDescriptor[] getAlbianObjectPropertyDescriptor(String type);
	
}
