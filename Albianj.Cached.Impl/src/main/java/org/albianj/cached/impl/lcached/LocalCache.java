/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.cached.impl.lcached;

import org.albianj.cached.lcached.ICacheable;
import org.albianj.cached.lcached.ILocalCached;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Default, non-distributed implementation of the Cache interface. The algorithm
 * for cache is as follows: a HashMap is maintained for fast object lookup. Two
 * linked lists are maintained: one keeps objects in the order they are accessed
 * from cache, the other keeps objects in the order they were originally added
 * to cache. When objects are added to cache, they are first wrapped by a
 * CacheObject which maintains the following pieces of information:
 * <ul>
 * <p>
 * <li>The size of the object (in bytes).
 * <li>A pointer to the node in the linked list that maintains accessed order
 * for the object. Keeping a reference to the node lets us avoid linear scans of
 * the linked list.
 * <li>A pointer to the node in the linked list that maintains the age of the
 * object in cache. Keeping a reference to the node lets us avoid linear scans
 * of the linked list.
 * </ul>
 * <p>
 * <p>
 * To get an object from cache, a hash lookup is performed to get a reference to
 * the CacheObject that wraps the real object we are looking for. The object is
 * subsequently moved to the front of the accessed linked list and any necessary
 * cache cleanups are performed. Cache deletion and expiration is performed as
 * needed.
 *
 * @author Matt Tucker
 */
public class LocalCache implements ILocalCached {

    /**
     * The map the keys and values are stored in.
     */
    @SuppressWarnings("rawtypes")
    protected Map map;

    /**
     * Linked list to maintain order that cache objects are accessed in, most
     * used to least used.
     */
    protected LinkedList lastAccessedList;

    /**
     * Linked list to maintain time that cache objects were initially added to
     * the cache, most recently added to oldest added.
     */
    protected LinkedList ageList;
    /**
     * Maximum length of time objects can exist in cache before expiring.
     */
    protected long defaultLifetime;
    /**
     * Maintain the number of cache hits and misses. A cache hit occurs every
     * time the get method is called and the cache contains the requested
     * object. A cache miss represents the opposite occurence.
     * <p>
     * <p>
     * Keeping track of cache hits and misses lets one measure how efficient the
     * cache is; the higher the percentage of hits, the more efficient.
     */
    protected long cacheHits, cacheMisses = 0L;
    /**
     * Maximum size in bytes that the cache can grow to.
     */
    private int maxCacheSize;
    /**
     * Maintains the current size of the cache in bytes.
     */
    private int cacheSize = 0;
    /**
     * The name of the cache.
     */
    private String name;

    /**
     * Create a new cache and specify the maximum size of for the cache in
     * bytes, and the maximum lifetime of objects.
     *
     * @param name        a name for the cache.
     * @param maxSize     the maximum size of the cache in bytes. -1 means the cache has
     *                    no max size.
     * @param maxLifetime the maximum amount of time objects can exist in cache before
     *                    being deleted. -1 means objects never expire.
     */
    @SuppressWarnings("rawtypes")
    public LocalCache(String name, int maxSize, long maxLifetime) {
        this.name = name;
        this.maxCacheSize = maxSize;
        this.defaultLifetime = maxLifetime;

        // Our primary data structure is a HashMap. The default capacity of 11
        // is too small in almost all cases, so we set it bigger.
        map = new HashMap(103);

        lastAccessedList = new LinkedList();
        ageList = new LinkedList();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#put(java.lang.Object,
     * java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public synchronized Object put(Object key, Object value) {
        // Delete an old entry if it exists.
        remove(key);

        int objectSize = calculateSize(value);

        // If the object is bigger than the entire cache, simply don't add it.
        if (maxCacheSize > 0 && objectSize > maxCacheSize * .90) {
            // Log.warn("Cache: " + name + " -- object with key " + key +
            // " is too large to fit in cache. Size is " + objectSize);
            return value;
        }
        cacheSize += objectSize;
        CacheObject cacheObject = new CacheObject(value, objectSize);
        map.put(key, cacheObject);
        // Make an entry into the cache order list.
        LinkedListNode lastAccessedNode = lastAccessedList.addFirst(key);
        // Store the cache order list entry so that we can get back to it
        // during later lookups.
        cacheObject.lastAccessedListNode = lastAccessedNode;
        // Add the object to the age list
        LinkedListNode ageNode = ageList.addFirst(key);
        // We make an explicit call to currentTimeMillis() so that total
        // accuracy
        // of lifetime calculations is better than one second.
        ageNode.timestamp = System.currentTimeMillis();
        ageNode.creationTimes = this.defaultLifetime;
        cacheObject.ageListNode = ageNode;

        // If cache is too full, remove least used cache entries until it is
        // not too full.
        cullCache();

        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#put(java.lang.Object,
     * java.lang.Object, long)
     */
    @SuppressWarnings("unchecked")
    public synchronized Object put(Object key, Object value, long timespan) {
        // Delete an old entry if it exists.
        remove(key);

        int objectSize = calculateSize(value);

        // If the object is bigger than the entire cache, simply don't add it.
        if (maxCacheSize > 0 && objectSize > maxCacheSize * .90) {
            // Log.warn("Cache: " + name + " -- object with key " + key +
            // " is too large to fit in cache. Size is " + objectSize);
            return value;
        }
        cacheSize += objectSize;
        CacheObject cacheObject = new CacheObject(value, objectSize);
        map.put(key, cacheObject);
        // Make an entry into the cache order list.
        LinkedListNode lastAccessedNode = lastAccessedList.addFirst(key);
        // Store the cache order list entry so that we can get back to it
        // during later lookups.
        cacheObject.lastAccessedListNode = lastAccessedNode;
        // Add the object to the age list
        LinkedListNode ageNode = ageList.addFirst(key);
        // We make an explicit call to currentTimeMillis() so that total
        // accuracy
        // of lifetime calculations is better than one second.
        // if(timespan < this.maxLifetime)
        // ageNode.timestamp = System.currentTimeMillis() + this.maxLifetime -
        // timespan;
        // else
        // ageNode.timestamp = System.currentTimeMillis() - (timespan -
        // this.maxLifetime);
        ageNode.creationTimes = System.currentTimeMillis();
        ageNode.timestamp = timespan;
        cacheObject.ageListNode = ageNode;

        // If cache is too full, remove least used cache entries until it is
        // not too full.
        cullCache();

        return value;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#get(java.lang.Object)
     */
    public synchronized Object get(Object key) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        CacheObject cacheObject = (CacheObject) map.get(key);
        if (cacheObject == null) {
            // The object didn't exist in cache, so increment cache misses.
            cacheMisses++;
            return null;
        }

        // The object exists in cache, so increment cache hits. Also, increment
        // the object's read count.
        cacheHits++;
        cacheObject.readCount++;

        // Remove the object from it's current place in the cache order list,
        // and re-insert it at the front of the list.
        cacheObject.lastAccessedListNode.remove();
        lastAccessedList.addFirst(cacheObject.lastAccessedListNode);

        return cacheObject.object;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#remove(java.lang.Object)
     */
    public synchronized Object remove(Object key) {
        CacheObject cacheObject = (CacheObject) map.get(key);
        // If the object is not in cache, stop trying to remove it.
        if (cacheObject == null) {
            return null;
        }
        // remove from the hash map
        map.remove(key);
        // remove from the cache order list
        cacheObject.lastAccessedListNode.remove();
        cacheObject.ageListNode.remove();
        // remove references to linked list nodes
        cacheObject.ageListNode = null;
        cacheObject.lastAccessedListNode = null;
        // removed the object, so subtract its size from the total.
        cacheSize -= cacheObject.size;
        return cacheObject.object;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#clear()
     */
    public synchronized void clear() {
        Object[] keys = map.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            remove(keys[i]);
        }

        // Now, reset all containers.
        map.clear();
        lastAccessedList.clear();
        lastAccessedList = new LinkedList();
        ageList.clear();
        ageList = new LinkedList();

        cacheSize = 0;
        cacheHits = 0;
        cacheMisses = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#size()
     */
    public int size() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        return map.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#isEmpty()
     */
    public boolean isEmpty() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        return map.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#values()
     */
    @SuppressWarnings("rawtypes")
    public Collection values() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        Object[] cacheObjects = map.values().toArray();
        Object[] values = new Object[cacheObjects.length];
        for (int i = 0; i < cacheObjects.length; i++) {
            values[i] = ((CacheObject) cacheObjects[i]).object;
        }
        return Collections.unmodifiableList(Arrays.asList(values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#containsKey(java.lang.Object)
     */
    public boolean containsKey(Object key) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        return map.containsKey(key);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#putAll(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    public void putAll(Map map) {
        for (Iterator i = map.keySet().iterator(); i.hasNext(); ) {
            Object key = i.next();
            Object value = map.get(key);
            put(key, value);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.albianj.expiredcached.impl.ICache#containsValue(java.lang.Object)
     */
    public boolean containsValue(Object value) {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        int objectSize = calculateSize(value);
        CacheObject cacheObject = new CacheObject(value, objectSize);
        return map.containsValue(cacheObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#entrySet()
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set entrySet() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        return Collections.unmodifiableSet(map.entrySet());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#keySet()
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Set keySet() {
        // First, clear all entries that have been in cache longer than the
        // maximum defined age.
        deleteExpiredEntries();

        return Collections.unmodifiableSet(map.keySet());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getName()
     */
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getCacheHits()
     */
    public long getCacheHits() {
        return cacheHits;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getCacheMisses()
     */
    public long getCacheMisses() {
        return cacheMisses;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getCacheSize()
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getMaxCacheSize()
     */
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#setMaxCacheSize(int)
     */
    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
        // It's possible that the new max size is smaller than our current cache
        // size. If so, we need to delete infrequently used items.
        cullCache();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#getDefaultLifetime()
     */
    public long getDefaultLifetime() {
        return defaultLifetime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.albianj.expiredcached.impl.ICache#setDefaultLifetime(long)
     */
    public void setDefaultLifetime(long defaultLifetime) {
        this.defaultLifetime = defaultLifetime;
    }

    /**
     * Returns the size of an object in bytes. Determining size by serialization
     * is only used as a last resort.
     *
     * @return the size of an object in bytes.
     */
    private int calculateSize(Object object) {
        // If the object is Cacheable, ask it its size.
        if (object instanceof ICacheable) {
            return ((ICacheable) object).getCachedSize();
        }
        // Check for other common types of objects put into cache.
        else if (object instanceof Long) {
            return CacheSizes.sizeOfLong();
        } else if (object instanceof Integer) {
            return CacheSizes.sizeOfObject() + CacheSizes.sizeOfInt();
        } else if (object instanceof Boolean) {
            return CacheSizes.sizeOfObject() + CacheSizes.sizeOfBoolean();
        } else if (object instanceof long[]) {
            long[] array = (long[]) object;
            return CacheSizes.sizeOfObject() + array.length
                    * CacheSizes.sizeOfLong();
        }
        // Default behavior -- serialize the object to determine its size.
        else {
            int size = 1;
            try {
                // Default to serializing the object out to determine size.
                NullOutputStream out = new NullOutputStream();
                @SuppressWarnings("resource")
                ObjectOutputStream outObj = new ObjectOutputStream(out);
                outObj.writeObject(object);
                size = out.size();
            } catch (IOException ioe) {
                // Log.error(ioe);
            }
            return size;
        }
    }

    /**
     * Clears all entries out of cache where the entries are older than the
     * maximum defined age.
     */
    protected void deleteExpiredEntries() {
        // Check if expiration is turned on.
        if (defaultLifetime <= 0) {
            return;
        }

        // Remove all old entries. To do this, we remove objects from the end
        // of the linked list until they are no longer too old. We get to avoid
        // any hash lookups or looking at any more objects than is strictly
        // neccessary.
        LinkedListNode node = ageList.getLast();
        // If there are no entries in the age list, return.
        if (node == null) {
            return;
        }

        // Determine the expireTime, which is the moment in time that elements
        // should expire from cache. Then, we can do an easy to check to see
        // if the expire time is greater than the expire time.
        // long expireTime = System.currentTimeMillis() - maxLifetime;

        while (node.timestamp < System.currentTimeMillis() - node.creationTimes) {
            // Remove the object
            remove(node.object);

            // Get the next node.
            node = ageList.getLast();
            // If there are no more entries in the age list, return.
            if (node == null) {
                return;
            }
        }
    }

    /**
     * Removes objects from cache if the cache is too full. "Too full" is
     * defined as within 3% of the maximum cache size. Whenever the cache is is
     * too big, the least frequently used elements are deleted until the cache
     * is at least 10% empty.
     */
    protected final void cullCache() {
        // Check if a max cache size is defined.
        if (maxCacheSize < 0) {
            return;
        }

        // See if the cache size is within 3% of being too big. If so, clean out
        // cache until it's 10% free.
        if (cacheSize >= maxCacheSize * .97) {
            // First, delete any old entries to see how much memory that frees.
            deleteExpiredEntries();
            int desiredSize = (int) (maxCacheSize * .90);
            while (cacheSize > desiredSize) {
                // Get the key and invoke the remove method on it.
                remove(lastAccessedList.getLast().object);
            }
        }
    }

    /**
     * Wrapper for all objects put into cache. It's primary purpose is to
     * maintain references to the linked lists that maintain the creation time
     * of the object and the ordering of the most used objects.
     */
    private static class CacheObject {

        /**
         * Underlying object wrapped by the CacheObject.
         */
        public Object object;

        /**
         * The size of the Cacheable object. The size of the Cacheable object is
         * only computed once when it is added to the cache. This makes the
         * assumption that once objects are added to cache, they are mostly
         * read-only and that their size does not change significantly over
         * time.
         */
        public int size;

        /**
         * A reference to the node in the cache order list. We keep the
         * reference here to avoid linear scans of the list. Every time the
         * object is accessed, the node is removed from its current spot in the
         * list and moved to the front.
         */
        public LinkedListNode lastAccessedListNode;

        /**
         * A reference to the node in the age order list. We keep the reference
         * here to avoid linear scans of the list. The reference is used if the
         * object has to be deleted from the list.
         */
        public LinkedListNode ageListNode;

        /**
         * A count of the number of times the object has been read from cache.
         */
        @SuppressWarnings("unused")
        public int readCount = 0;

        /**
         * Creates a new cache object wrapper. The size of the Cacheable object
         * must be passed in in order to prevent another possibly expensive
         * lookup by querying the object itself for its size.
         * <p>
         *
         * @param object the underlying Object to wrap.
         * @param size   the size of the Cachable object in bytes.
         */
        public CacheObject(Object object, int size) {
            this.object = object;
            this.size = size;
        }
    }

    /**
     * An extension of OutputStream that does nothing but calculate the number
     * of bytes written through it.
     */
    private static class NullOutputStream extends OutputStream {

        int size = 0;

        public void write(int b) throws IOException {
            size++;
        }

        public void write(byte[] b) throws IOException {
            size += b.length;
        }

        public void write(byte[] b, int off, int len) {
            size += len;
        }

        /**
         * Returns the number of bytes written out through the stream.
         *
         * @return the number of bytes written to the stream.
         */
        public int size() {
            return size;
        }
    }
}

// Read more:
// http://kickjava.com/src/org/jivesoftware/util/Cache.java.htm#ixzz1WZAH5mgd
