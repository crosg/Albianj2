package org.albianj.cached.impl.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.albianj.cached.attribute.IAlbianCachedAttribute;
import org.albianj.cached.attribute.IAlbianCachedServerAttribute;
import org.albianj.cached.service.AlbianCachedAttributeException;
import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

public class AlbianRedisCachedAdapter extends FreeAlbianService implements
		IAlbianCachedService {
	boolean isCluster = false;
	ShardedJedisPool sjp = null;
	JedisCluster jc = null;

	@Override
	public void init(Object initObject) {
		// TODO Auto-generated method stub
		IAlbianCachedAttribute aca = (IAlbianCachedAttribute) initObject;
		if (aca.getCluster()) {
			isCluster = true;
			List<IAlbianCachedServerAttribute> servers = aca.getServers();
			if (Validate.isNullOrEmpty(servers)) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser the redis-server of cached service:%s is error,it is null or empty.",
								aca.getName());
			}

			Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
			for (IAlbianCachedServerAttribute s : servers) {
				jedisClusterNodes
						.add(new HostAndPort(s.getHost(), s.getPort()));
			}

			jc = new JedisCluster(jedisClusterNodes);
		} else {
			GenericObjectPoolConfig gopc = new GenericObjectPoolConfig();
			gopc.setMaxIdle(100);
			gopc.setMinIdle(10);
			gopc.setTestOnBorrow(true);
			gopc.setTestOnReturn(true);
//			JedisPoolConfig jpc = new JedisPoolConfig();
//			jpc.setTestOnBorrow(true);
//			jpc.setTestOnReturn(true);
			// jpc.setMaxActive(aca.getConnectPoolSize() >= 100 ?
			// aca.getConnectPoolSize() : 100);
//			jpc.setMaxIdle(100);
//			jpc.setMinIdle(0);
			List<IAlbianCachedServerAttribute> servers = aca.getServers();
			if (Validate.isNullOrEmpty(servers)) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser the redis-server of cached service:%s is error,it is null or empty.",
								aca.getName());
			}
			List<JedisShardInfo> jsis = new LinkedList<JedisShardInfo>();
			for (IAlbianCachedServerAttribute s : servers) {
				JedisShardInfo jsi = new JedisShardInfo(s.getHost(),
						s.getPort());
				jsis.add(jsi);
			}
			sjp = new ShardedJedisPool(gopc, jsis);
		}
	}

	@Override
	public void set(String cachedName, String k, Object v) {
		// TODO Auto-generated method stub
		String body = JSON.toJSONString(v, SerializerFeature.WriteClassName);
		if (isCluster) {
			jc.set(k, body);
		} else {
			ShardedJedis sj = null;
			try {
				sj = sjp.getResource();
				sj.set(k, body);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().error(
						IAlbianLoggerService.AlbianRunningLoggerName, e,
						"set object:%s is fail.", k);
			} finally {
				if (null != sj)
					sjp.returnResourceObject(sj);
			}
		}
	}

	@Override
	public void set(String cachedName, String k, Object v, int tto) {
		// TODO Auto-generated method stub
		String body = JSON.toJSONString(v, SerializerFeature.WriteClassName);
		if (isCluster) {
			jc.set(k, body);
			jc.expire(k, tto);
		} else {
			ShardedJedis sj = null;
			try {
				sj = sjp.getResource();
				sj.set(k, body);
				sj.expire(k, tto);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().error(
						IAlbianLoggerService.AlbianRunningLoggerName, e,
						"set object:%s is fail.", k);
			} finally {
				if (null != sj)
					sjp.returnResourceObject(sj);
			}
		}
	}

	@Override
	public void delete(String cachedName, String k) {
		// TODO Auto-generated method stub
		if (isCluster) {
			jc.del(k);
		} else {
			ShardedJedis sj = null;
			try {
				sj = sjp.getResource();
				sj.del(k);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().error(
						IAlbianLoggerService.AlbianRunningLoggerName, e,
						"delete object:%s is fail.", k);
			} finally {
				if (null != sj)
					sjp.returnResourceObject(sj);
			}
		}
	}

	@Override
	public boolean exist(String cachedName, String k) {
		// TODO Auto-generated method stub
		if (isCluster) {
			return jc.exists(k);
		} else {
			ShardedJedis sj = null;
			try {
				sj = sjp.getResource();
				return sj.exists(k);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().error(
						IAlbianLoggerService.AlbianRunningLoggerName, e,
						"find object:%s is exist fail.", k);
			} finally {
				if (null != sj)
					sjp.returnResourceObject(sj);
			}
		}
		return false;
	}

	@Override
	public <T> T get(String cachedName, String k,Class<T> cls) {
		// TODO Auto-generated method stub
		if (isCluster) {
			String body = jc.get(k);
			return JSON.parseObject(body,cls);
		}
		ShardedJedis sj = null;
		try {
			sj = sjp.getResource();
			String body = sj.get(k);
			return JSON.parseObject(body,cls);
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e,
					"get object:%s is fail.", k);
		} finally {
			if (null != sj)
				sjp.returnResourceObject(sj);
		}
		return null;
	}
	
	@Override
	public <T> List<T> getArray(String cachedName, String k,Class<T> cls) {
		// TODO Auto-generated method stub
		if (isCluster) {
			String body = jc.get(k);
			return JSON.parseArray(body, cls);
		}
		ShardedJedis sj = null;
		try {
			sj = sjp.getResource();
			String body = sj.get(k);
			return JSON.parseArray(body, cls);
		} catch (Exception e) {
			AlbianServiceRouter.getLogger().error(
					IAlbianLoggerService.AlbianRunningLoggerName, e,
					"get object:%s is fail.", k);
		} finally {
			if (null != sj)
				sjp.returnResourceObject(sj);
		}
		return null;
	}
	
	@Override
	public boolean freeAll(String nodeName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getCachedClient(String cachedName) {
		// TODO Auto-generated method stub
		if (isCluster)
			return jc;
		return sjp.getResource();
	}
	
	public void returnCachedClient(String cachedName, Object client) {
		if (!isCluster) {
			if (null != client) {
				ShardedJedis sj = (ShardedJedis) client;
				sjp.returnResourceObject(sj);
			}
		}
	}

}
