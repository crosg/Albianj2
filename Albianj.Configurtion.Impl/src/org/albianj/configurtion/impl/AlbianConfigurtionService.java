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
