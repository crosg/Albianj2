package org.albianj.persistence.impl.dbcached;

import java.util.HashMap;
//import net.rubyeye.xmemcached.MemcachedClient;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.concurrent.IAlbianThreadPoolService;
import org.albianj.persistence.impl.context.ChainExpressionParser;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class AlbianPersistenceCache {
	public static String buildKey(Class<?> cls, int start, int step, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) {
		StringBuilder sb = new StringBuilder();
		sb.append(cls.getName()).append("_").append(start).append("_").append(step).append("_");
		List<IFilterCondition> wheres = new LinkedList<>();
		ChainExpressionParser.toFilterConditionArray(f,wheres);
		if (null != wheres) {
			for (IFilterCondition where : wheres) {
				sb.append(where.getRelationalOperator()).append("_")
						.append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? where.getFieldName()
								: where.getAliasName())
						.append("_")
						// .append(where.getFieldName()).append("_")
						.append(where.getLogicalOperation()).append("_").append(where.getValue());
			}
		}
		if (null != orderbys) {
			for (IOrderByCondition orderby : orderbys) {
				sb.append(orderby.getFieldName()).append("_").append(orderby.getSortStyle()).append("_");
			}
		}
		return sb.toString();
	}

	public static <T extends IAlbianObject> void setObjects(Class<T> cls, int start, int step,
			IChainExpression f, LinkedList<IOrderByCondition> orderbys, List<T> objs) {
//		IAlbianObjectAttribute aoa = (IAlbianObjectAttribute) AlbianObjectsMap.get(cls.getName());
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		
		String key = buildKey(cls, start, step, f, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, objs, tto);
		tps.execute(apct);

	}

	public static <T extends IAlbianObject> void setObjects(Class<T> cls, String key, List<T> newObj) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, newObj, tto);
		tps.execute(apct);

	}

	public static <T extends IAlbianObject> void setObject(Class<T> cls, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys, IAlbianObject obj) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		String key = buildKey(cls, 0, 0, f, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
		tps.execute(apct);
	}

	public static <T extends IAlbianObject> void setPagesize(Class<T> cls, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys, long pagesize) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		String key = buildKey(cls, -1, -1, f, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, pagesize, tto);
		tps.execute(apct);
	}

	public static <T extends IAlbianObject> void setObject(Class<T> cls, String key, IAlbianObject obj) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
		tps.execute(apct);
	}

	public static <T extends IAlbianObject> T findObject(Class<T> cls, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		String key = buildKey(cls, 0, 0, f, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);

			if (null != acs) {
				T obj = acs.get(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	public static <T extends IAlbianObject> T findObject(Class<T> cls, String key) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				T obj = acs.get(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, int start, int step,
			IChainExpression f, LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		String key = buildKey(cls, start, step, f, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, String key) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	public static <T extends IAlbianObject> long findPagesize(Class<T> cls, IChainExpression f,
			LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return -1;
		String key = buildKey(cls, -1, -1, f, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				Long num = acs.get(aoa.getCache().getName(), key, Long.class);
				if (null == num) {
					return -1;
				}
				return num;
			}
		} catch (Exception e) {

		}
		return -1;
	}

	public static <T extends IAlbianObject> long findPagesize(Class<T> cls, String key) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return -1;
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				Long num = acs.get(aoa.getCache().getName(), key, Long.class);
				if (null == num) {
					return -1;
				}
				return num;
			}
		} catch (Exception e) {

		}
		return -1;
	}
	
	
	
	
	
	
	@Deprecated
	public static String buildKey(Class<?> cls, int start, int step, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys) {
		StringBuilder sb = new StringBuilder();
		sb.append(cls.getName()).append("_").append(start).append("_").append(step).append("_");
		if (null != wheres) {
			for (IFilterCondition where : wheres) {
				sb.append(where.getRelationalOperator()).append("_")
						.append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? where.getFieldName()
								: where.getAliasName())
						.append("_")
						// .append(where.getFieldName()).append("_")
						.append(where.getLogicalOperation()).append("_").append(where.getValue());
			}
		}
		if (null != orderbys) {
			for (IOrderByCondition orderby : orderbys) {
				sb.append(orderby.getFieldName()).append("_").append(orderby.getSortStyle()).append("_");
			}
		}
		return sb.toString();
	}
	
	@Deprecated
	public static <T extends IAlbianObject> void setObjects(Class<T> cls, int start, int step,
			LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys, List<T> objs) {
//		IAlbianObjectAttribute aoa = (IAlbianObjectAttribute) AlbianObjectsMap.get(cls.getName());
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		String key = buildKey(cls, start, step, wheres, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, objs, tto);
		tps.execute(apct);

	}

	@Deprecated
	public static <T extends IAlbianObject> void setObject(Class<T> cls, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys, IAlbianObject obj) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		String key = buildKey(cls, 0, 0, wheres, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, obj, tto);
		tps.execute(apct);
	}
	
	@Deprecated
	public static <T extends IAlbianObject> void setPagesize(Class<T> cls, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys, long pagesize) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return;
		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name, false);
		if (null == tps)
			return;
		String key = buildKey(cls, -1, -1, wheres, orderbys);
		int tto = 0 == aoa.getCache().getLifeTime() ? 300 : aoa.getCache().getLifeTime();
		String name = Validate.isNullOrEmptyOrAllSpace(aoa.getCache().getName())
				? IAlbianObject.AlbianObjectCachedNameDefault : aoa.getCache().getName();
		AlbianPersistenceCacheThread apct = new AlbianPersistenceCacheThread(name, key, pagesize, tto);
		tps.execute(apct);
	}
	
	@Deprecated
	public static <T extends IAlbianObject> T findObject(Class<T> cls, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		String key = buildKey(cls, 0, 0, wheres, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);

			if (null != acs) {
				T obj = acs.get(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}
	
	@Deprecated
	public static <T extends IAlbianObject> List<T> findObjects(Class<T> cls, int start, int step,
			LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return null;
		String key = buildKey(cls, start, step, wheres, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				List<T> obj = acs.getArray(aoa.getCache().getName(), key, cls);
				if (null != obj) {
					return obj;
				}
			}
		} catch (Exception e) {

		}
		return null;
	}

	@Deprecated
	public static <T extends IAlbianObject> long findPagesize(Class<T> cls, LinkedList<IFilterCondition> wheres,
			LinkedList<IOrderByCondition> orderbys) {
		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute aoa = amps.getAlbianObjectAttribute(cls.getName());
		if (!aoa.getCache().getEnable())
			return -1;
		String key = buildKey(cls, -1, -1, wheres, orderbys);
		try {
			IAlbianCachedService acs = AlbianServiceRouter.getService(IAlbianCachedService.class,
					IAlbianCachedService.Name, false);
			if (null != acs) {
				Long num = acs.get(aoa.getCache().getName(), key, Long.class);
				if (null == num) {
					return -1;
				}
				return num;
			}
		} catch (Exception e) {

		}
		return -1;
	}

	
}
