package org.albianj.persistence.object;

import java.io.Serializable;

/**
 * Albian对象的基接口，所有需要Albianj管理的数据对象都需要直接或者间接的继承置此接口。
 * </br>
 * 一般情况下，没有必须要直接继承此接口，继承该接口的抽象类 org.albianj.persistence.impl.object.FreeAlbianObject
 * </br>
 * 如果必须直接继承此接口，必须要明确IsAlbianNew和OldAlbianObject的值，否则数据库操作将会出现无法预料的问题。
 * <p>
 * 
 * @author Seapeak
 * @see org.albianj.persistence.impl.object.FreeAlbianObject
 *
 */
public interface IAlbianObject extends Serializable {
	
	/**
	 * 此变量为AlbianObject的默认缓存名称.
	 * </br>
	 * 当persisten.xml配置文件中的AlbianObject缓存打开(Enable=true)并且Name项为空、未配置或者是""时，使用此名字。
	 * </br>
	 * Name的xpath：AlbianObjects/AlbianObject/Cache/Name
	 */
	static final String AlbianObjectCachedNameDefault = "AlbianObjectCached";

	/**
	 * Albian 内核级方法，不要调用。
	 * </br>
	 * 此方法为Albian内部使用
	 * <p>
	 * @return 当前对象是否为新建对象。new的对象为新对象，从数据库load或者find的对象为old对象
	 */
	public boolean getIsAlbianNew();

	/**
	 * Albian 内核级方法，不要调用
	 * </br>
	 * 此方法为Albian内部使用，调用此方法将会影响到对于数据库的Insert和Update操作，导致数据不正确或者操作无法完成
	 * <p>
	 * @param isNew:默认为true，方便初始化对象操作；当从数据库获取对象时，isNew将会自动赋值为false
	 */
	public void setIsAlbianNew(boolean isNew);

	/**
	 * Albian 内核级方法，不要调用
	 * 
	 * @param key
	 * @param v
	 */
	public void setOldAlbianObject(String key, Object v);

	/**
	 * Albian 内核级方法，不要调用
	 * 
	 * @param key
	 * @return
	 */
	public Object getOldAlbianObject(String key);
}
