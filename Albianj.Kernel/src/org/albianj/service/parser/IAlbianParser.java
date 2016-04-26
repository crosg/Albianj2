package org.albianj.service.parser;

public interface IAlbianParser {
	public void init() throws AlbianParserException;
	
	/**
	 * 设置当前service需要解析的配置文件，如果不调用此方法，使用默认的文件名
	 * 
	 * 注意：1.	这个配置文件只是文件名，文件的路径由启动方法start设置
	 *				2. 此方法应该在service的beforeLoad生命周期方法内被调用
	 * @param fileName 此service所需要的配置文件名
	 * 
	 * @since v2.1.1
	 * @serialData	2016-03-17
	 */
	public void setConfigFileName(String fileName);
}
