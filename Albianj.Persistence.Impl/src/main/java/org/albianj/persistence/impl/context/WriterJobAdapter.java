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

import org.albianj.boot.AlbianBundleContext;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.impl.db.IPersistenceUpdateCommand;
import org.albianj.persistence.object.*;
import org.albianj.persistence.service.AlbianEntityMetadata;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianBuiltinNames;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.util.*;

public class WriterJobAdapter extends FreeWriterJobAdapter {
    protected Map<String, Object> buildSqlParameter(String sessioId, IAlbianObject object,
                                                    IAlbianObjectAttribute albianObject,
                                                    Map<String, IAlbianEntityFieldAttribute> fieldsAttr) throws AlbianDataServiceException {
        Map<String, Object> mapValue = new HashMap<String, Object>();
        String name = "";
        for (IAlbianEntityFieldAttribute fAttr : fieldsAttr.values()) {
            name = fAttr.getPropertyName();
            try {
                if ("string".equalsIgnoreCase(fAttr.getEntityField().getType()
                        .getSimpleName())) {
                    Object oValue = fAttr.getEntityField().get(object);
                    if (null == oValue) {
                        mapValue.put(name, null);
                    } else {
                        String value = oValue.toString();
                        if ((-1 == fAttr.getLength()) || (fAttr.getLength() >= value.length())) {
                            mapValue.put(name, value);
                        } else {
                            mapValue.put(name,
                                    value.substring(0, fAttr.getLength()));
                        }
                    }
                } else {
                    mapValue.put(name, fAttr.getEntityField().get(object));
                }
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessioId, AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "invoke bean read method is error.the property is:%s.job id:%s.",
                        albianObject.getType(), name);
            }

        }

        return mapValue;
    }

    protected List<IDataRouterAttribute> parserRoutings(String sessionId, IAlbianObject object,
                                                        IDataRoutersAttribute routings, IAlbianObjectAttribute albianObject) {
        List<IDataRouterAttribute> useRoutings = new Vector<IDataRouterAttribute>();
        if (null == routings) {
            IDataRouterAttribute dra = albianObject.getDefaultRouting();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "albian-object:%s writer-data-routers are null then use default storage:%s.",
                    albianObject.getType(), dra.getName());

            useRoutings.add(dra);
        } else {
            if (Validate.isNullOrEmpty(routings.getWriterRouters())) {
                IDataRouterAttribute dra = albianObject.getDefaultRouting();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Warn,
                        "albian-object:%s writer-data-routers are null then use default storage:%s.",
                        albianObject.getType(), dra.getName());


                useRoutings.add(dra);
            } else {
                if (routings.getWriterRouterEnable()) {
                    IAlbianObjectDataRouter hashMapping = routings
                            .getDataRouter();
                    if (null == hashMapping) {
                        Map<String, IDataRouterAttribute> wrs = routings.getWriterRouters();
                        List<IDataRouterAttribute> ras = new Vector<IDataRouterAttribute>();
                        for (IDataRouterAttribute dra : wrs.values()) {
                            if (dra.getEnable()) {
                                ras = new Vector<IDataRouterAttribute>();
                                useRoutings.add(dra);

                                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                        sessionId, AlbianLoggerLevel.Warn,
                                        "albian-object:%s writer-data-router arithmetic is null then use default storage:%s.",
                                        albianObject.getType(), dra.getName());
                                break;
                            }
                        }
                    } else {
                        List<IDataRouterAttribute> writerRoutings = hashMapping
                                .mappingWriterRouting(
                                        routings.getWriterRouters(), object);
                        if (Validate.isNullOrEmpty(writerRoutings)) {
                            IDataRouterAttribute dra = albianObject.getDefaultRouting();
                            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                    sessionId, AlbianLoggerLevel.Warn,
                                    "albian-object:%s writer-data-router arithmetic is null then use default storage:%s.",
                                    albianObject.getType(), dra.getName());

                            useRoutings.add(dra);
                        } else {
                            for (IDataRouterAttribute writerRouting : writerRoutings) {
                                if (writerRouting.getEnable()) {
                                    useRoutings.add(writerRouting);
                                }
                            }
                            if (Validate.isNullOrEmpty(useRoutings)) {
                                IDataRouterAttribute dra = albianObject.getDefaultRouting();
                                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                                        sessionId, AlbianLoggerLevel.Warn,
                                        "albian-object:%s writer-data-router arithmetic is disable then use default storage:%s.",
                                        albianObject.getType(), dra.getName());
                                useRoutings.add(dra);
                            }
                        }
                    }
                }
            }
        }
        return useRoutings;
    }

    protected String parserRoutingStorage(String jobId, IAlbianObject obj,
                                          IDataRouterAttribute routing, IAlbianObjectDataRouter hashMapping,
                                          IAlbianObjectAttribute albianObject) throws AlbianDataServiceException {
        if (null == routing) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    jobId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the writer data router of object:%s is null.",
                    albianObject.getType());
        }
        if (null == hashMapping) {
            String name = routing.getStorageName();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    jobId, AlbianLoggerLevel.Warn,
                    "albian-object:%s writer-data-router arithmetic is not found and use default storage:%s.",
                    albianObject.getType(), name);
            return name;
        } else {
            String name = hashMapping.mappingWriterRoutingStorage(routing, obj);
            if (Validate.isNullOrEmpty(name)) {
                String dname = routing.getStorageName();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        jobId, AlbianLoggerLevel.Warn,
                        "albian-object:%s writer-data-router is not found by arithmetic and use default storage:%s.",
                        albianObject.getType(), dname);
                return dname;
            } else {
                return name;
            }
        }
    }

    protected String parserRoutingDatabase(String jobId, IAlbianObject obj,
                                           IStorageAttribute storage, IAlbianObjectDataRouter hashMapping,
                                           IAlbianObjectAttribute albianObject) throws AlbianDataServiceException {
        if (null == storage) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    jobId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the writer data router of object:%s is null.",
                    albianObject.getType());
        }
        if (null == hashMapping) {
            String name = storage.getDatabase();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    jobId, AlbianLoggerLevel.Warn,
                    "albian-object:%s writer-data-router arithmetic is not found and use default database:%s.",
                    albianObject.getType(), name);
            return name;
        } else {
            String name = hashMapping.mappingWriterRoutingDatabase(storage, obj);
            if (Validate.isNullOrEmpty(name)) {
                String dname = storage.getDatabase();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        jobId, AlbianLoggerLevel.Warn,
                        "albian-object:%s writer-data-router is not found by arithmetic and use default database:%s.",
                        albianObject.getType(), dname);
                return dname;
            } else {
                return name;
            }
        }
    }


    protected void buildWriterJob(AlbianBundleContext bundleContext, IWriterJob job, IAlbianObject entity,
                                  String storageAlias, String tableAlias,
                                  IPersistenceUpdateCommand cmd) {
        Class<?> cls = entity.getClass();
        String className = cls.getName();
        AlbianEntityMetadata entityMetadata = bundleContext.getModuleConf(AlbianBuiltinNames.Conf.Persistence);
        IAlbianObjectAttribute objAttr = entityMetadata.getEntityMetadataByType(cls);

        Map<String, IAlbianEntityFieldAttribute> fieldsAttr = objAttr.getFields();
        if (Validate.isNullOrEmpty(fieldsAttr)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s PropertyDescriptor is not found.", className);
        }
        Map<String, Object> sqlParaVals = buildSqlParameter(job.getId(), entity,
                objAttr, fieldsAttr);

        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        if (!Validate.isNullOrEmptyOrAllSpace(storageAlias)) {
            String tableName = Validate.isNullOrEmptyOrAllSpace(tableAlias)
                    ? objAttr.getImplClzz().getSimpleName()
                    : tableAlias;
            IStorageAttribute stgAttr = asps.getStorageAttribute(storageAlias);
            IPersistenceCommand pstCmd = cmd.buildPstCmd(job.getId(), stgAttr.getDatabaseStyle(),
                    tableName, entity, objAttr, sqlParaVals, job.isRollbackOnError());
            addWrtTsk(job, stgAttr, stgAttr.getDatabase(), pstCmd);
        } else {
            IDataRoutersAttribute drtsAttr = objAttr.getDataRouters();
            List<IDataRouterAttribute> sltDrtAttr = parserRoutings(job.getId(), entity,
                    drtsAttr, objAttr);
            for (IDataRouterAttribute drtAttr : sltDrtAttr) {
                IAlbianObjectDataRouter drouter = null == drtsAttr ? null : drtsAttr.getDataRouter();
                String storageName = parserRoutingStorage(job.getId(), entity, drtAttr,
                        drouter, objAttr);
                IStorageAttribute stgAttr = asps.getStorageAttribute(storageName);
                String database = parserRoutingDatabase(job.getId(), entity, stgAttr,
                        drouter, objAttr);

                String tableName = drouter.mappingWriterTable(drtAttr, entity);

                IPersistenceCommand pstCmd = cmd.buildPstCmd(job.getId(), stgAttr.getDatabaseStyle(),
                        tableName, entity, objAttr, sqlParaVals, job.isRollbackOnError());
                if (null == cmd)
                    continue;// no the upload operator

                addWrtTsk(job, stgAttr, database, pstCmd);
            }
        }
    }

    private void addWrtTsk(IWriterJob job, IStorageAttribute storage, String database, IPersistenceCommand pstCmd) {
        String key = storage.getName() + database;
        if (Validate.isNull(job.getWriterTasks())) {
            Map<String, IWriterTask> tasks = new LinkedHashMap<>();
            IWriterTask task = new WriterTask();
            List<IPersistenceCommand> cmds = new Vector<>();
            cmds.add(pstCmd);
            task.setCommands(cmds);
            task.setStorage(new RunningStorageAttribute(storage, database));
            tasks.put(key, task);
            job.setWriterTasks(tasks);
        } else {
            if (job.getWriterTasks().containsKey(key)) {
                job.getWriterTasks().get(key).getCommands()
                        .add(pstCmd);
            } else {
                IWriterTask task = new WriterTask();
                List<IPersistenceCommand> cmds = new Vector<>();
                cmds.add(pstCmd);
                task.setCommands(cmds);
                task.setStorage(new RunningStorageAttribute(storage, database));
                job.getWriterTasks().put(key, task);
            }
        }
    }

}
