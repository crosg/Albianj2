package org.albianj.cached.impl.lcached;

import java.util.HashMap;
import java.util.Map;

import org.albianj.cached.lcached.ILocalCached;

public class LocalCacheManager {
	@SuppressWarnings("rawtypes")
	private static Map caches = new HashMap();
	private static int maxLifetime = 300;

	/**
	 * <p>
	 * Initialize a cache by name.
	 * </p>
	 * <p/>
	 * <p>
	 * Caches require initialization before use. Be careful to initialize your
	 * cache before using it. Initializing a cache that has already been
	 * initialized once does nothing.
	 * </p>
	 * <p/>
	 * <p>
	 * The cache manager will check jive module context for overriding
	 * defaultMaxCacheSize values. The property names should be
	 * "cache.name.size" where 'name' will be the same as the cache name. If the
	 * property exists, that value will be used instead of the
	 * defaultMaxCacheSize.
	 * </p>
	 * 
	 * @param name
	 *            the name of the cache to create.
	 * @param defaultMaxCacheSize
	 *            the default max size the cache can grow to, in bytes.
	 */
	public static void initializeCache(String name, int defaultMaxCacheSize) {
		initializeCache(name, defaultMaxCacheSize, maxLifetime);
	}

	@SuppressWarnings("unchecked")
	public static void initializeCache(String name, int defaultMaxCacheSize,
			int defaultSecond) {
		LocalCache cache = (LocalCache) caches.get(name);
		if (cache == null) {
			caches.put(name, new LocalCache(name, defaultMaxCacheSize,
					defaultSecond * 1000));
		}
	}

	/**
	 * Returns the cache specified by name.
	 * 
	 * @param name
	 *            the name of the cache to return.
	 * @return the cache found, or null if no cache by that name has been
	 *         initialized.
	 */
	public static ILocalCached getCache(String name) {
		return (ILocalCached) caches.get(name);
	}

}
