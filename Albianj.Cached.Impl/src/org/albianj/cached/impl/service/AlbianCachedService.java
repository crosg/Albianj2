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
