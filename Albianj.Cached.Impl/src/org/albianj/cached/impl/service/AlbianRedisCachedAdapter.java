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
