package org.albianj.persistence.service;

/**
 * 查询数据的方式
 * @author seapeak
 *
 */
public enum LoadType {
	/**
	 * 最快速度加载数据
	 * 首先从缓存中先加载数据，缓存中没有，从read-router配置的数据库中加载
	 */
	quickly,
	/**
	 * 允许加载脏数据
	 * 直接从reader-router配置的数据库中加载数据
	 */
	dirty,
	/**
	 * 精确的加载数据
	 * 从writer-router配置的数据库中加载数据，这个加载方式应该只对需要写入操作的load数据执行
	 */
	exact,
}
