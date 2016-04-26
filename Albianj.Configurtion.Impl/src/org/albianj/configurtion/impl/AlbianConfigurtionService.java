package org.albianj.configurtion.impl;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.concurrent.IAlbianThreadPoolService;
import org.albianj.configurtion.ConfigItem;
import org.albianj.configurtion.IAlbianConfigurtionService;
import org.albianj.configurtion.IConfigItem;
import org.albianj.datetime.AlbianDateTime;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.FilterCondition;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.object.OrderByCondition;
import org.albianj.persistence.service.IAlbianPersistenceService;
import org.albianj.persistence.service.LoadType;
import org.albianj.service.AlbianServiceException;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.unid.service.IAlbianRemoteUNIDService;
import org.albianj.verify.Validate;

public class AlbianConfigurtionService extends FreeAlbianService implements
		IAlbianConfigurtionService {

	@Override
	public IConfigItem findConfigurtion(BigInteger id) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("Id", id));
		wheres.add(new FilterCondition("Enable", true));
		wheres.add(new FilterCondition("IsDelete", false));
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			ci = aps.loadObject(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"find configurtion from database is error.config-id:%s.",id.toString());
			return null;
		}
		if (null == ci || null == ci.getValue())
			return null;
		return ci;
	}

	@Override
	public IConfigItem loadConfigurtion(BigInteger id) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("Id", id));
		wheres.add(new FilterCondition("Enable", true));
		wheres.add(new FilterCondition("IsDelete", false));
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			ci = aps.loadObject(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load configurtion from database is error.config-id:%s.",id.toString());
			return null;		}
		if (null == ci || null == ci.getValue())
			return null;
		return ci;
	}

	@Override
	public List<IConfigItem> findChildConfigurtions(BigInteger pid) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("ParentId", pid));
		wheres.add(new FilterCondition("Enable", true));
		wheres.add(new FilterCondition("IsDelete", false));
		List<IConfigItem> cis = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			cis  = aps.loadObjects(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load child configurtions from database is error.config-pid:%s.",pid.toString());
			return null;
		}
		if (null == cis)
			return null;
		return cis;
	}

	@Override
	public List<IConfigItem> loadChildConfigurtions(BigInteger pid) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("ParentId", pid));
		wheres.add(new FilterCondition("Enable", true));
		wheres.add(new FilterCondition("IsDelete", false));
		List<IConfigItem> cis;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			cis  = aps.loadObjects(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load child configurtion from database is error.config-pid:%s.",pid.toString());
			return null;
		}
		if (null == cis)
			return null;
		return cis;

	}

	@Override
	public IConfigItem findAllConfigurtion(BigInteger id) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("Id", id));
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			ci = aps.loadObject(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"find all configurtion from database is error.config-id:%s.",id.toString());
			return null;
		}
		if (null == ci || null == ci.getValue())
			return null;
		return ci;
	}

	@Override
	public IConfigItem loadAllConfigurtion(BigInteger id) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("Id", id));
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			ci = aps.loadObject(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load all configurtion from database is error.config-id:%s.",id.toString());
			return null;
		}
		if (null == ci || null == ci.getValue())
			return null;
		return ci;
	}

	@Override
	public List<IConfigItem> findAllChildConfigurtions(BigInteger pid) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("ParentId", pid));
		List<IConfigItem> cis;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name);
			if(null == aps){
				throw new AlbianDataServiceException("the persistence service is null.");
			}
			cis = aps.loadObjects(null,
					IConfigItem.class, LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"find all child configurtion from database is error.config-pid:%s.",pid.toString());
			return null;
		}
		if (null == cis)
			return null;
		return cis;
	}

	@Override
	public List<IConfigItem> loadAllChildConfigurtions(BigInteger pid) {
		// TODO Auto-generated method stub
		LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
		wheres.add(new FilterCondition("ParentId", pid));
		List<IConfigItem> cis = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			cis = aps.loadObjects(null,
					IConfigItem.class, LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load all child configurtion from database is error.config-pid:%s.",pid.toString());
			return null;
		}
		if (null == cis)
			return null;
		return cis;
	}

	@Override
	public Object findConfigurtionValue(String... names) {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		for (String name : names) {
			sb.append(name).append(AlbianCachedKeySepDefault);
		}
		if (0 < sb.length())
			sb.deleteCharAt(sb.length() - 1);
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class,
				IAlbianCachedService.Name);
		if (null == acs) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianServiceException.class,
							"ConfigurtionService is error.",
							"can not get cached-service by name:%s from albian-kernel for configurtion service.",
							IAlbianCachedService.Name);
		}
		ConfigItem ci = acs
				.get(AlbianConfigurtionCachedNameDefault, sb.toString(),ConfigItem.class);
		if (null == ci)
			return loadConfigurtionValue(names);

		//IConfigItem ci = (IConfigItem) obj;
		if (ci.getEnable() && !ci.getIsDelete())
			return ci.getValue();
		return null;
	}

	@Override
	public Object loadConfigurtionValue(String... names) {
		// TODO Auto-generated method stub
		if (null == names || 0 == names.length)
			return null;
		BigInteger pid = IConfigItem.RootId;
		IConfigItem ci = null;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.length; i++) {
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Name", names[i]));
			wheres.add(new FilterCondition("Enable", true));
			wheres.add(new FilterCondition("IsDelete", false));
			wheres.add(new FilterCondition("ParentId", pid));
			wheres.add(new FilterCondition("Level", i + 1, true));
			try {
				IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
				ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
			} catch (AlbianDataServiceException e) {
				AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
						"load configurtion from database is error.config level is %d,name is:%s.",i,names[i]);
				return null;
			}
			if (null == ci)
				return null;
			pid = ci.getParentId();
			sb.append(names[i]).append(AlbianCachedKeySepDefault);
		}

		IAlbianThreadPoolService tps = AlbianServiceRouter.getService(
				IAlbianThreadPoolService.class,
				IAlbianThreadPoolService.Name);
		if (null != tps) {
			AlbianConfigurtionCachedThread acct = new AlbianConfigurtionCachedThread(
					false, sb.toString(), ci);
			tps.execute(acct);
		}
		return ci.getValue();
	}

	@Override
	public boolean create(IConfigItem cfi, String mender) {
		// TODO Auto-generated method stub
		if (null == cfi || Validate.isNullOrEmptyOrAllSpace(mender))
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							InvalidParameterException.class,
							"ConfigurtionService is error.",
							"the argument is null or empty.and the memder is must exist.");

		Timestamp ts = AlbianDateTime.getDateTimeNow();
		cfi.setLastMender(mender);
		cfi.setLastModify(ts);
		cfi.setCreateTime(ts);
		cfi.setAuthor(mender);
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			return aps.create(null,cfi);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"create configurtion to database is error.config name:%s,id:%s,mender:%s.",
					cfi.getName(),cfi.getId().toString(),mender);
		}
		return false;
	}

	@Override
	public boolean modify(BigInteger id, Object value, String mender) {
		// TODO Auto-generated method stub
		if (null == id || Validate.isNullOrEmptyOrAllSpace(mender))
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							InvalidParameterException.class,
							"ConfigurtionService is error.",
							"the argument is null or empty.and the memder is must exist.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for modify is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return false;
		ci.setLastMender(mender);
		ci.setValue(value);
		ci.setLastModify(AlbianDateTime.getDateTimeNow());

		boolean rc = false;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			rc = aps.modify(null,ci);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"modify configurtion to database is error.config name:%s,id:%s,mender:%s.",
					ci.getName(),ci.getId().toString(),mender);
		}
		if (rc) {
			IAlbianThreadPoolService tps = AlbianServiceRouter.getService(
					IAlbianThreadPoolService.class,
					IAlbianThreadPoolService.Name);
			if (null != tps) {
				AlbianConfigurtionCachedThread acct = new AlbianConfigurtionCachedThread(
						true,
						String.format(
								"%s%s%s",
								Validate.isNullOrEmpty(ci.getParentNamePath()) ? ""
										: ci.getParentNamePath(),
								AlbianCachedKeySepDefault, ci.getName()), null);
				tps.execute(acct);
			}
		}
		return rc;
	}

	@Override
	public boolean delete(BigInteger id, String mender) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmptyOrAllSpace(mender))
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							InvalidParameterException.class,
							"ConfigurtionService is error.",
							"the argument is null or empty.and the memder is must exist.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for deleting is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return false;
		ci.setIsDelete(true);
		ci.setLastMender(mender);
		ci.setLastModify(AlbianDateTime.getDateTimeNow());
		boolean rc = false;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			rc = aps.modify(null,ci);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"delete configurtion to database is error.config name:%s,id:%s,mender:%s.",
					ci.getName(),ci.getId().toString(),mender);
		}
		if (rc) {
			IAlbianThreadPoolService tps = AlbianServiceRouter.getService(
					IAlbianThreadPoolService.class,
					IAlbianThreadPoolService.Name);
			if (null != tps) {
				AlbianConfigurtionCachedThread acct = new AlbianConfigurtionCachedThread(
						true,
						String.format(
								"%s%s%s",
								Validate.isNullOrEmpty(ci.getParentNamePath()) ? ""
										: ci.getParentNamePath(),
								AlbianCachedKeySepDefault, ci.getName()), null);
				tps.execute(acct);
			}
		}
		return rc;
	}

	@Override
	public boolean disable(BigInteger id, String mender) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmptyOrAllSpace(mender))
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							InvalidParameterException.class,
							"ConfigurtionService is error.",
							"the argument is null or empty.and the memder is must exist.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for disable is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return false;
		ci.setEnable(false);
		ci.setLastMender(mender);
		ci.setLastModify(AlbianDateTime.getDateTimeNow());
		boolean rc = false;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			rc = aps.modify(null,ci);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"disable configurtion to database is error.config name:%s,id:%s,mender:%s.",
					ci.getName(),ci.getId().toString(),mender);
		}
		if (rc) {
			IAlbianThreadPoolService tps = AlbianServiceRouter.getService(
					IAlbianThreadPoolService.class,
					IAlbianThreadPoolService.Name);
			if (null != tps) {
				AlbianConfigurtionCachedThread acct = new AlbianConfigurtionCachedThread(
						true,
						String.format(
								"%s%s%s",
								Validate.isNullOrEmpty(ci.getParentNamePath()) ? ""
										: ci.getParentNamePath(),
								AlbianCachedKeySepDefault, ci.getName()), null);
				tps.execute(acct);
			}
		}
		return rc;
	}

	@Override
	public boolean enable(BigInteger id, String mender) {
		// TODO Auto-generated method stub
		if (Validate.isNullOrEmptyOrAllSpace(mender))
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							InvalidParameterException.class,
							"ConfigurtionService is error.",
							"the argument is null or empty.and the memder is must exist.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for enable is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return false;
		ci.setEnable(true);
		ci.setLastMender(mender);
		ci.setLastModify(AlbianDateTime.getDateTimeNow());
		boolean rc = false;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			rc = aps.modify(null,ci);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"enable configurtion to database is error.config name:%s,id:%s,mender:%s.",
					ci.getName(),ci.getId().toString(),mender);
		}
		if (rc) {
			IAlbianThreadPoolService tps = AlbianServiceRouter.getService(
					IAlbianThreadPoolService.class,
					IAlbianThreadPoolService.Name);
			if (null != tps) {
				AlbianConfigurtionCachedThread acct = new AlbianConfigurtionCachedThread(
						true,
						String.format(
								"%s%s%s",
								Validate.isNullOrEmpty(ci.getParentNamePath()) ? ""
										: ci.getParentNamePath(),
								AlbianCachedKeySepDefault, ci.getName()), null);
				tps.execute(acct);
			}
		}
		return rc;
	}

	@Override
	public BigInteger getConfigItemId(int level) {
		// TODO Auto-generated method stub
		IAlbianRemoteUNIDService ids = AlbianServiceRouter.getService(
				IAlbianRemoteUNIDService.class,
				IAlbianRemoteUNIDService.Name);
		if (null == ids) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianServiceException.class,
							"ConfigurtionService is error.",
							"can not get remoteid-service by name:%s from albian-kernel for configurtion service.",
							IAlbianRemoteUNIDService.Name);
		}
		BigInteger id = ids.createConfigItemId();
		BigInteger l = new BigInteger(Integer.toString(level));
		BigInteger newid = id.add(l);
		return newid;
	}

	@Override
	public void expireConfigItemCacheForce(BigInteger id,String mender) {
		// TODO Auto-generated method stub
		if (null == id)
			throw new InvalidParameterException("the config id is null.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.quickly, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for expire is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return;
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class,
				IAlbianCachedService.Name);
		if (null == acs) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianServiceException.class,
							"ConfigurtionService is error.",
							"can not get cached-service by name:%s from albian-kernel for configurtion service.",
							IAlbianCachedService.Name);
		}
		acs.delete(AlbianConfigurtionCachedNameDefault, String.format(
				"%s%s%s",
				Validate.isNullOrEmpty(ci.getParentNamePath()) ? "" : ci
						.getParentNamePath(), AlbianCachedKeySepDefault, ci
						.getName()));
	}

	@Override
	public void resetConfigItemCacheForce(BigInteger id,String mender) {
		// TODO Auto-generated method stub
		if (null == id)
			throw new InvalidParameterException("the config id is null.");
		IConfigItem ci = null;
		try {
			IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Id", id));
			ci = aps.loadObject(null,IConfigItem.class,LoadType.exact, wheres);
		} catch (AlbianDataServiceException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, e,
					"load config for reset is error.id:%s,mender:%s.",
					id.toString(),mender);
		}
		if (null == ci)
			return;
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class,
				IAlbianCachedService.Name);
		if (null == acs) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianServiceException.class,
							"ConfigurtionService is error.",
							"can not get cached-service by name:%s from albian-kernel for configurtion service.",
							IAlbianCachedService.Name);
		}
		acs.set(AlbianConfigurtionCachedNameDefault, String.format(
				"%s%s%s",
				Validate.isNullOrEmpty(ci.getParentNamePath()) ? "" : ci
						.getParentNamePath(), AlbianCachedKeySepDefault, ci
						.getName()), ci);
	}

	@Override
	public void fuelConfigurtionCache() {
		// TODO Auto-generated method stub
		IAlbianCachedService acs = AlbianServiceRouter.getService(
				IAlbianCachedService.class,
				IAlbianCachedService.Name);
		if (null == acs) {
			AlbianServiceRouter.getLogger()
					.errorAndThrow(
							IAlbianLoggerService.AlbianRunningLoggerName,
							AlbianServiceException.class,
							"ConfigurtionService is error.",
							"can not get cached-service by name:%s from albian-kernel for configurtion service.",
							IAlbianCachedService.Name);
		}

		for (int level = 0; level <= 5; level++) {
			LinkedList<IFilterCondition> wheres = new LinkedList<IFilterCondition>();
			wheres.add(new FilterCondition("Enable", true));
			wheres.add(new FilterCondition("IsDelete", false));
			wheres.add(new FilterCondition("Level", level, true));
			LinkedList<IOrderByCondition> orderbys = new LinkedList<IOrderByCondition>();
			orderbys.add(new OrderByCondition("Id"));
			int unit = 50;
			int times = 0;
			while (true) {
				List<IConfigItem> list = null;
				try {
					IAlbianPersistenceService aps = AlbianServiceRouter.getService(IAlbianPersistenceService.class,IAlbianPersistenceService.Name,true);
					list = aps.loadObjects(null,IConfigItem.class,LoadType.quickly,
									unit * times, unit, wheres, orderbys);
				} catch (AlbianDataServiceException e1) {
					AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
							"load config for fule is error.id:%s,mender:%s.");
				}
				if (Validate.isNullOrEmpty(list))
					break;
				for (IConfigItem ci : list) {
					acs.set(AlbianConfigurtionCachedNameDefault, String.format(
							"%s%s%s",
							Validate.isNullOrEmpty(ci.getParentNamePath()) ? ""
									: ci.getParentNamePath(),
							AlbianCachedKeySepDefault, ci.getName()), ci);
				}

				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {

				}
				if (unit > list.size())
					break;
				times++;
			}
		}
	}

}
