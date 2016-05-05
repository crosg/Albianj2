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

import java.util.LinkedList;
import java.util.List;

import org.albianj.cached.attribute.AlbianCacheStyle;
import org.albianj.cached.attribute.IAlbianCachedAttribute;
import org.albianj.cached.attribute.IAlbianCachedServerAttribute;
import org.albianj.cached.impl.attribute.AlbianCachedAttribute;
import org.albianj.cached.impl.attribute.AlbianCachedServerAttribute;
import org.albianj.cached.service.AlbianCachedAttributeException;
import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

public class AlbianCachedService extends FreeAlbianCachedParser implements
		IAlbianCachedService {

	@Override
	protected void parserCacheds(@SuppressWarnings("rawtypes") List nodes) {
		if (Validate.isNullOrEmpty(nodes)) {
			throw new IllegalArgumentException("nodes");
		}
		String name = null;
		String firstName = null;
		for (Object node : nodes) {
			Element ele = (Element) node;
			IAlbianCachedAttribute cacheAttribute = null;
			try {
				cacheAttribute = parserCached(ele);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().errorAndThrow(
						IAlbianLoggerService.AlbianRunningLoggerName,
						AlbianCachedAttributeException.class, e,
						"cached-service is error",
						"parser the cached service:%s is error.", name);
			}
			if (null == cacheAttribute) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser cached service in the cached.xml is fail,all the attrubute can not parser.");
			}
			name = cacheAttribute.getName();
			if (Validate.isNullOrEmpty(name))
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser the cached service is error,the name is null or empty.the node text:%s.",
								ele.asXML());
			AlbianCachedMap.insert(name, cacheAttribute);

			Class<?> impl = null;
			IAlbianCachedService acs = null;
			try {
//				impl = Class.forName(cacheAttribute.getType());
				impl = AlbianClassLoader.getInstance().loadClass(cacheAttribute.getType());
				acs = (IAlbianCachedService) impl.newInstance();
				acs.init(cacheAttribute);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().errorAndThrow(
						IAlbianLoggerService.AlbianRunningLoggerName,
						AlbianCachedAttributeException.class, e,
						"cached-service is error",
						"new the cached:%s instance is fail.", name);
			}
			services.put(name, acs);
			if (null == firstName)
				firstName = name;
		}

		// the default cached is the first node in the cached.xml
		if (!services.containsKey("Default")) {
			services.put("Default", services.get(firstName));
		}
	}

	@Override
	protected IAlbianCachedAttribute parserCached(Element node) {
		// TODO Auto-generated method stub
		if (null == node)
			return null;
		String style = XmlParser.getAttributeValue(node, "Style");
		String name = XmlParser.getAttributeValue(node, "Name");
		if (Validate.isNullOrEmpty(name)) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianCachedAttributeException.class,
							"cached-service is error",
							"parser the cached service is error,the name is null or empty.the node text:%s.",
							node.asXML());
		}
		if (Validate.isNullOrEmpty(style)) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianCachedAttributeException.class,
							"cached-service is error",
							"parser the cached service:%s is error,the style is null or empty..",
							name);
		}
		IAlbianCachedAttribute ca = new AlbianCachedAttribute();
		ca.setName(name);

		String enable = XmlParser.getAttributeValue(node, "Enable");
		ca.setEnable(Validate.isNullOrEmptyOrAllSpace(enable) ? false
				: new Boolean(enable));
		String tto = XmlParser.getAttributeValue(node, "ConnectTimeout");
		ca.setConnectTimeout(Validate.isNullOrEmptyOrAllSpace(tto) ? 300000L
				: new Long(tto));
		String cluster =  XmlParser.getAttributeValue(node, "Cluster");
		ca.setCluster(Validate.isNullOrEmptyOrAllSpace(cluster) ? false
				: new Boolean(cluster));

		if (AlbianCacheStyle.RedisDesc.equals(style.toLowerCase())) {
			ca.setCacheStyle(AlbianCacheStyle.Redis);
			String type = XmlParser.getAttributeValue(node, "Type");
			if (Validate.isNullOrEmptyOrAllSpace(type)) {
				ca.setType(RedisCachedTypeDefault);
			} else {
				ca.setType(type);
			}
			List<?> cNodes = node.selectNodes("Server");
			List<IAlbianCachedServerAttribute> list = parserCachedServers(name,
					cNodes);
			if (Validate.isNullOrEmpty(list)) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser the cached service:%s is error,the redis servers is null or empty..",
								name);
			}
			ca.setServers(list);
		} else {
			ca.setCacheStyle(AlbianCacheStyle.Local);
			String type = XmlParser.getAttributeValue(node, "Type");
			if (Validate.isNullOrEmptyOrAllSpace(type)) {
				ca.setType(LocalCachedTypeDefault);
			} else {
				ca.setType(type);
			}
		}
		return ca;
	}

	protected List<IAlbianCachedServerAttribute> parserCachedServers(
			String name, @SuppressWarnings("rawtypes") List nodes) {
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianCachedAttributeException.class,
							"cached-service is error",
							"parser the cached service:%s is error,the redis servers is null or empty..",
							name);
		}
		List<IAlbianCachedServerAttribute> servers = new LinkedList<IAlbianCachedServerAttribute>();
		for (Object node : nodes) {
			IAlbianCachedServerAttribute cacheServerAttribute = null;
			try {
				cacheServerAttribute = parserCachedServer(name, (Element) node);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								e,
								"cached-service is error",
								"parser the redis server of cached service:%s is error.please lookat cached.xml",
								name);
			}
			if (null == cacheServerAttribute) {
				AlbianServiceRouter.getLogger()
						.errorAndThrow(
								IAlbianLoggerService.AlbianRunningLoggerName,
								AlbianCachedAttributeException.class,
								"cached-service is error",
								"parser the redis server of cached service:%s is error the attribute is null.please lookat cached.xml",
								name);
			}
			servers.add(cacheServerAttribute);
		}

		return servers;
	}

	protected IAlbianCachedServerAttribute parserCachedServer(String name,
			Element node) {
		// TODO Auto-generated method stub
		if (null == node)
			return null;
		String host = XmlParser.getAttributeValue(node, "Host");
		String port = XmlParser.getAttributeValue(node, "Port");
		if (Validate.isNullOrEmpty(host)) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianCachedAttributeException.class,
							"cached-service is error",
							"parser the redis-server's host of cached service:%s is error the attribute is null.please lookat cached.xml",
							name);
		}

		if (Validate.isNullOrEmpty(port)) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianCachedAttributeException.class,
							"cached-service is error",
							"parser the redis-server's port of cached service:%s is error the attribute is null.please lookat cached.xml",
							name);
		}
		IAlbianCachedServerAttribute csa = new AlbianCachedServerAttribute();
		csa.setHost(host);
		csa.setPort(new Integer(port));
		return csa;
	}

	@Override
	public void set(String cachedName, String k, Object v) {
		if (Validate.isNullOrEmpty(cachedName)) {
			services.get(CachedDefaultName).set(CachedDefaultName, k, v);
		} else {
			services.get(cachedName).set(cachedName, k, v);
		}
	}

	@Override
	public void set(String cachedName, String k, Object v, int tto) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			services.get(CachedDefaultName).set(CachedDefaultName, k, v, tto);
		} else {
			services.get(cachedName).set(cachedName, k, v, tto);
		}
	}

	@Override
	public void delete(String cachedName, String k) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			services.get(CachedDefaultName).delete(CachedDefaultName, k);
		} else {
			services.get(cachedName).delete(cachedName, k);
		}
	}

	@Override
	public boolean exist(String cachedName, String k) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			return services.get(CachedDefaultName).exist(CachedDefaultName, k);
		}
		return services.get(cachedName).exist(cachedName, k);
	}
//
//	@Override
//	public Object get(String cachedName, String k) {
//		// TODO Auto-generated method stub
//		if (Validate.isNullOrEmpty(cachedName)) {
//			return services.get(CachedDefaultName).get(CachedDefaultName, k);
//		}
//		return services.get(cachedName).get(cachedName, k);
//	}

	@Override
	public boolean freeAll(String cachedName) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			return services.get(CachedDefaultName).freeAll(CachedDefaultName);
		}
		return services.get(cachedName).freeAll(cachedName);
	}

	@Override
	public void init(Object initObject) {
		return;
	}

	@Override
	public Object getCachedClient(String cachedName) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			return services.get(CachedDefaultName).getCachedClient(
					CachedDefaultName);
		}
		return services.get(cachedName).getCachedClient(cachedName);
	}

	@Override
	public <T> T get(String cachedName, String k, Class<T> cls) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				if (Validate.isNullOrEmpty(cachedName)) {
					return services.get(CachedDefaultName).get(CachedDefaultName, k,cls);
				}
				return services.get(cachedName).get(cachedName, k,cls);
	}

	@Override
	public <T> List<T> getArray(String cachedName, String k, Class<T> cls) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
			return services.get(CachedDefaultName).getArray(CachedDefaultName, k,cls);
		}
		return services.get(cachedName).getArray(cachedName, k,cls);
	}

	@Override
	public void returnCachedClient(String cachedName, Object client) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmpty(cachedName)) {
		 services.get(CachedDefaultName).returnCachedClient(
					CachedDefaultName,client);
		}
		services.get(cachedName).returnCachedClient(cachedName,client);
	}

}
