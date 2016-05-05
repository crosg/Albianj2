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
package org.albianj.persistence.impl.context;

import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IAlbianObjectDataRouter;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.object.IFilterCondition;
import org.albianj.persistence.object.IOrderByCondition;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class ReaderJobAdapter extends FreeReaderJobAdapter implements IReaderJobAdapter {
	protected IDataRouterAttribute parserReaderRouting(Class<?> cls, String jobId, boolean isExact, String routingName,
			Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
					throws AlbianDataServiceException {
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		IAlbianMappingParserService amps = AlbianServiceRouter.getService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
		IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);
		
//		IAlbianObjectAttribute albianObject = (IAlbianObjectAttribute) AlbianObjectsMap.get(className);
		if (null == albianObject) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"albian-object:%s attribute is not found.job id:%s.", className, jobId);
		}

		Map<String, IDataRouterAttribute> routers = isExact ? routings.getWriterRouters() : routings.getReaderRouters();

		if (null == routings || Validate.isNullOrEmpty(routers)) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is null or empty and use default:%s.job id:%s.", className,
					dra.getName(), jobId);
			return dra;
		}

		if (Validate.isNullOrEmptyOrAllSpace(routingName)) {
			if (isExact) {
				if (!routings.getWriterRouterEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
			} else {
				if (!routings.getReaderRouterEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
			}
			IAlbianObjectDataRouter hashMapping = routings.getDataRouter();
			if (null != hashMapping) {
				IDataRouterAttribute routing = null;
				if (isExact) {
					routing = hashMapping.mappingExactReaderRouting(routings.getWriterRouters(), hashWheres,
							hashOrderbys);
				} else {
					routing = hashMapping.mappingReaderRouting(routings.getReaderRouters(), hashWheres, hashOrderbys);
				}
				if (null == routing) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s not found router. then use defaut:%s.job id:%s.",
							className, dra.getName(), jobId);
					return dra;
				}
				if (!routing.getEnable()) {
					IDataRouterAttribute dra = albianObject.getDefaultRouting();
					AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
							"the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.job id:%s.",
							className, routing.getName(), dra.getName(), jobId);
					return dra;
				}
				return routing;
			}
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"the reader-date-router is not appoint and the object:%s reader-date-router arithmetic is null. then use defaut:%s.job id:%s.",
					className, dra.getName(), jobId);
			return dra;
		}

		IDataRouterAttribute routing = routers.get(routingName);

		if (null == routing) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found and use default:%s.job id:%s.", className,
					dra.getName(), jobId);
			return dra;
		}
		if (!routing.getEnable()) {
			IDataRouterAttribute dra = albianObject.getDefaultRouting();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.job id:%s.",
					className, routing.getName(), dra.getName(), jobId);
			return dra;
		}
		return routing;
	}

	protected String parserRoutingStorage(Class<?> cls, String jobId,boolean isExact, IDataRouterAttribute readerRouting,
			Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
					throws AlbianDataServiceException {
		String className = cls.getName();
		IAlbianDataRouterParserService adrps = AlbianServiceRouter.getService(IAlbianDataRouterParserService.class,IAlbianDataRouterParserService.Name);
		IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

		if (null == readerRouting) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianSqlLoggerName,
					AlbianDataServiceException.class, "DataService is error.",
					"the reader data router of object:%s is null.job id:%s.", className, jobId);
		}

		if (null == routings) {
			String name = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found and use default storage:%s.job id:%s.", className,
					name, jobId);
			return name;
		}
		IAlbianObjectDataRouter router = routings.getDataRouter();
		if (null == router) {
			String name = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router arithmetic is not found and use default storage:%s.job id:%s.",
					className, name, jobId);
			return name;
		}

		String name = isExact ? router.mappingExactReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys) 
				: router.mappingReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys);
		if (Validate.isNullOrEmpty(name)) {
			String dname = readerRouting.getStorageName();
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianSqlLoggerName,
					"albian-object:%s reader-data-router is not found by arithmetic and use default storage:%s.job id:%s.",
					className, dname, jobId);
			return dname;
		} else {
			return name;
		}
	}

}
