package org.albianj.cached.lcached;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface ILocalCached {

	public Object put(Object key, Object value);

	public Object put(Object key, Object value, long timespan);

	public Object get(Object key);

	public Object remove(Object key);

	public void clear();

	public int size();

	public boolean isEmpty();

	@SuppressWarnings("rawtypes")
	public Collection values();

	public boolean containsKey(Object key);

	@SuppressWarnings("rawtypes")
	public void putAll(Map map);

	public boolean containsValue(Object value);

	@SuppressWarnings("rawtypes")
	public Set entrySet();

	@SuppressWarnings("rawtypes")
	public Set keySet();

	/**
	 * Returns the name of this cache. The name is completely arbitrary and used
	 * only for display to administrators.
	 * 
	 * @return the name of this cache.
	 */
	public String getName();

	/**
	 * Returns the number of cache hits. A cache hit occurs every time the get
	 * method is called and the cache contains the requested object.
	 * <p>
	 * 
	 * Keeping track of cache hits and misses lets one measure how efficient the
	 * cache is; the higher the percentage of hits, the more efficient.
	 * 
	 * @return the number of cache hits.
	 */
	public long getCacheHits();

	/**
	 * Returns the number of cache misses. A cache miss occurs every time the
	 * get method is called and the cache does not contain the requested object.
	 * <p>
	 * 
	 * Keeping track of cache hits and misses lets one measure how efficient the
	 * cache is; the higher the percentage of hits, the more efficient.
	 * 
	 * @return the number of cache hits.
	 */
	public long getCacheMisses();

	/**
	 * Returns the size of the cache contents in bytes. This value is only a
	 * rough approximation, so cache users should expect that actual VM memory
	 * used by the cache could be significantly higher than the value reported
	 * by this method.
	 * 
	 * @return the size of the cache contents in bytes.
	 */
	public int getCacheSize();

	/**
	 * Returns the maximum size of the cache (in bytes). If the cache grows
	 * larger than the max size, the least frequently used items will be
	 * removed. If the max cache size is set to -1, there is no size limit.
	 * 
	 * @return the maximum size of the cache (-1 indicates unlimited max size).
	 */
	public int getMaxCacheSize();

	/**
	 * Sets the maximum size of the cache. If the cache grows larger than the
	 * max size, the least frequently used items will be removed. If the max
	 * cache size is set to -1, there is no size limit.
	 * 
	 * @param maxCacheSize
	 *            the maximum size of this cache (-1 indicates unlimited max
	 *            size).
	 */
	public void setMaxCacheSize(int maxCacheSize);

	/**
	 * Returns the maximum number of milleseconds that any object can live in
	 * cache. Once the specified number of milleseconds passes, the object will
	 * be automatically expried from cache. If the max lifetime is set to -1,
	 * then objects never expire.
	 * 
	 * @return the maximum number of milleseconds before objects are expired.
	 */
	public long getDefaultLifetime();

	/**
	 * Sets the maximum number of milleseconds that any object can live in
	 * cache. Once the specified number of milleseconds passes, the object will
	 * be automatically expried from cache. If the max lifetime is set to -1,
	 * then objects never expire.
	 * 
	 * @param maxLifetime
	 *            the maximum number of milleseconds before objects are expired.
	 */
	public void setDefaultLifetime(long defaultLifetime);

}