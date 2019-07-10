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

import org.albianj.argument.RefArg;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.impl.db.SqlParameter;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.impl.toolkit.EnumMapping;
import org.albianj.persistence.object.*;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.util.LinkedList;
import java.util.Map;

public class ReaderJobAdapter extends FreeReaderJobAdapter implements IReaderJobAdapter {

    protected IStorageAttribute makeReaderToStorageCtx(IAlbianObjectAttribute objAttr,
                                                       boolean isExact,
                                                       String storageAlias,
                                                       String tableAlias,
                                                       String drouterAlias,
                                                       Map<String, IFilterCondition> hashWheres,
                                                       Map<String, IOrderByCondition> hashOrderbys,
                                                       RefArg<String> dbName,
                                                       RefArg<String> tableName
    ) {
        IStorageAttribute stgAttr = null;
        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        if (Validate.isNullOrEmptyOrAllSpace(drouterAlias)) { // not exist fix-drouterAlias
            if (Validate.isNullOrEmptyOrAllSpace(storageAlias)) { // use drouer callback
                IDataRoutersAttribute drsAttr = objAttr.getDataRouters();
                IAlbianObjectDataRouter drouter = drsAttr.getDataRouter();
                if (isExact) {
                    IDataRouterAttribute drAttr = drouter.mappingExactReaderRouting(drsAttr.getWriterRouters(), hashWheres, hashOrderbys);
                    String storageName = drAttr.getStorageName();
                    stgAttr = asps.getStorageAttribute(storageName);
                    dbName.setValue(drouter.mappingExactReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
                    tableName.setValue(drouter.mappingExactReaderTable(drAttr, hashWheres, hashOrderbys));
                } else {
                    IDataRouterAttribute drAttr = drouter.mappingReaderRouting(drsAttr.getReaderRouters(), hashWheres, hashOrderbys);
                    String storageName = drAttr.getStorageName();
                    stgAttr = asps.getStorageAttribute(storageName);
                    dbName.setValue(drouter.mappingReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
                    tableName.setValue(drouter.mappingReaderTable(drAttr, hashWheres, hashOrderbys));
                }
            } else { // fix storageAlias then use it and table is class simplename default
                stgAttr = asps.getStorageAttribute(storageAlias);
                dbName.setValue(stgAttr.getDatabase());
                tableName.setValue(Validate.isNullOrEmptyOrAllSpace(tableAlias) ? objAttr.getImplClzz().getSimpleName() : tableAlias);
            }
        } else { // do fix drouter
            IDataRoutersAttribute drsAttr = objAttr.getDataRouters();
            IAlbianObjectDataRouter drouter = drsAttr.getDataRouter();
            IDataRouterAttribute drAttr = drsAttr.getReaderRouters().get(drouterAlias);
            String storageName = drAttr.getStorageName();
            stgAttr = asps.getStorageAttribute(storageName);
            dbName.setValue(drouter.mappingReaderRoutingDatabase(stgAttr, hashWheres, hashOrderbys));
            tableName.setValue(drouter.mappingReaderTable(drAttr, hashWheres, hashOrderbys));
        }
        return stgAttr;
    }


    protected StringBuilder makeSltCmdCols(String sessionId, IAlbianObjectAttribute objAttr, int dbStyle) {
        StringBuilder sbCols = new StringBuilder();
        for (String key : objAttr.getFields().keySet()) {
            IAlbianEntityFieldAttribute member = objAttr.getFields().get(key);
            if (null == member) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "albian-object:%s member:%s is not found.", objAttr.getType(), key);
            }
            if (!member.getIsSave())
                continue;
            if (member.getSqlFieldName().equals(member.getPropertyName())) {
                if (PersistenceDatabaseStyle.MySql == dbStyle) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getSqlFieldName()).append("]").append(",");
                }
            } else {
                if (PersistenceDatabaseStyle.MySql == dbStyle) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`")
                            .append(" AS ")
                            .append("`").append(member.getPropertyName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getSqlFieldName()).append("]")
                            .append(" AS ")
                            .append("[").append(member.getPropertyName()).append("]").append(",");
                }
            }
        }
        if (0 != sbCols.length())
            sbCols.deleteCharAt(sbCols.length() - 1);
        return sbCols;
    }

    protected StringBuilder makeSltCmdCount(int dbStyle) {
        StringBuilder sbCols = new StringBuilder();
        if (PersistenceDatabaseStyle.MySql == dbStyle) {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" `COUNT` ");
        } else {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" [COUNT] ");
        }
        return sbCols;
    }


    protected StringBuilder makeSltCmdOdrs(String sessionId, IAlbianObjectAttribute objAttr,
                                           LinkedList<IOrderByCondition> orderbys, int dbStyle) {
        StringBuilder sbOrderby = new StringBuilder();
        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                IMemberAttribute member = objAttr.getFields().get(
                        orderby.getFieldName().toLowerCase());
                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.",
                            objAttr.getType(), orderby.getFieldName());
                }

                if (PersistenceDatabaseStyle.MySql == dbStyle) {
                    sbOrderby.append("`").append(member.getSqlFieldName()).append("`");
                } else {
                    sbOrderby.append("[").append(member.getSqlFieldName()).append("]");
                }
                sbOrderby
                        .append(" ")
                        .append(EnumMapping.toSortOperation(orderby
                                .getSortStyle())).append(",");
            }
        }
        if (0 != sbOrderby.length())
            sbOrderby.deleteCharAt(sbOrderby.length() - 1);
        return sbOrderby;
    }

    protected StringBuilder makeSltCmdTxt(int sbStyle, StringBuilder sbCols, String tableName,
                                          StringBuilder sbWhere, StringBuilder sbOrderby,
                                          int start, int step, String idxName) {
        StringBuilder sbCmdTxt = new StringBuilder();
        sbCmdTxt.append("SELECT ").append(sbCols).append(" FROM ");
        if (PersistenceDatabaseStyle.MySql == sbStyle) {
            sbCmdTxt.append("`").append(tableName).append("`");
        } else {
            sbCmdTxt.append("[").append(tableName).append("]");
        }
        if (PersistenceDatabaseStyle.MySql == sbStyle
                && !Validate.isNullOrEmptyOrAllSpace(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbCmdTxt.append(" FORCE INDEX (").append(idxName).append(") ");
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sbWhere.toString())) {
            sbCmdTxt.append(" WHERE ").append(sbWhere);
        }
        if (0 != sbOrderby.length()) {
            sbCmdTxt.append(" ORDER BY ").append(sbOrderby);
        }
        if (0 <= start && 0 < step) {
            sbCmdTxt.append(" LIMIT ").append(start).append(", ")
                    .append(step);
        }
        if (0 > start && 0 < step) {
            sbCmdTxt.append(" LIMIT ").append(step);
        }
        return sbCmdTxt;
    }

    protected StringBuilder makeSltCmdWhrs(String sessionId, IAlbianObjectAttribute objAttr,
                                           int dbStyle, String implType,
                                           LinkedList<IFilterCondition> wheres,
                                           Map<String, ISqlParameter> paras) {
        StringBuilder sbWhrs = new StringBuilder();
        if (null != wheres) {
            for (IFilterCondition where : wheres) {
                if (where.isAddition()) continue;
                IMemberAttribute member = objAttr.getFields().get(
                        where.getFieldName().toLowerCase());

                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", implType, where.getFieldName());
                }

                sbWhrs.append(" ")
                        .append(EnumMapping.toRelationalOperators(where
                                .getRelationalOperator()))
                        .append(where.isBeginSub() ? "(" : " ");
                if (PersistenceDatabaseStyle.MySql == dbStyle) {
                    sbWhrs.append("`").append(member.getSqlFieldName()).append("`");
                } else {
                    sbWhrs.append("[").append(member.getSqlFieldName()).append("]");
                }
                sbWhrs.append(
                        EnumMapping.toLogicalOperation(where
                                .getLogicalOperation())).append("#")
                        .append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName())
                        //	.append(member.getSqlFieldName())
                        .append("#")
                        .append(where.isCloseSub() ? ")" : "");
                ISqlParameter para = new SqlParameter();
                para.setName(member.getSqlFieldName());
                para.setSqlFieldName(member.getSqlFieldName());
                if (null == where.getFieldClass()) {
                    para.setSqlType(member.getDatabaseType());
                } else {
                    para.setSqlType(Convert.toSqlType(where.getFieldClass()));
                }
                para.setValue(where.getValue());
                paras.put(String.format("#%1$s#", Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName()),
                        para);
            }
        }
        return sbWhrs;
    }



    /*

    protected IDataRouterAttribute parserReaderRouting(Class<?> itf, String sessionId, boolean isExact, String routingName,
                                                       Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
            throws AlbianDataServiceException {
        IAlbianObjectAttribute albianObject = AlbianEntityMetadata.getEntityMetadataByType(cls);
        String className = cls.getName();
        IDataRoutersAttribute routings = albianObject.getDataRouters();
//        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
//        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);
//
//        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
//        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);

        if (null == albianObject) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s attribute is not found.", className);
        }

        Map<String, IDataRouterAttribute> routers = null;
        if(null == routings) {
            routers = null;
        } else {
            routers = isExact ? routings.getWriterRouters() : routings.getReaderRouters();
        }

        if (null == routings || Validate.isNullOrEmpty(routers)) {
            IDataRouterAttribute dra = albianObject.getDefaultRouting();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "albian-object:%s reader-data-router is null or empty and use default:%s.",
                    className, dra.getName());
            return dra;
        }

        if (Validate.isNullOrEmptyOrAllSpace(routingName)) {
            if (isExact) {
                if (!routings.getWriterRouterEnable()) {
                    IDataRouterAttribute dra = albianObject.getDefaultRouting();
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Warn,
                            "the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.",
                            className, dra.getName());
                    return dra;
                }
            } else {
                if (!routings.getReaderRouterEnable()) {
                    IDataRouterAttribute dra = albianObject.getDefaultRouting();
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Warn,
                            "the reader-date-router is not appoint and the object:%s all reader router are disable. then use defaut:%s.",
                            className, dra.getName());
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
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Warn,
                            "the reader-date-router is not appoint and the object:%s not found router. then use defaut:%s.",
                            className, dra.getName());
                    return dra;
                }
                if (!routing.getEnable()) {
                    IDataRouterAttribute dra = albianObject.getDefaultRouting();
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Warn,
                            "the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.",
                            className, routing.getName(), dra.getName());
                    return dra;
                }
                return routing;
            }
            IDataRouterAttribute dra = albianObject.getDefaultRouting();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "the reader-date-router is not appoint and the object:%s reader-date-router arithmetic is null. then use defaut:%s.",
                    className, dra.getName());
            return dra;
        }

        IDataRouterAttribute routing = routers.get(routingName);

        if (null == routing) {
            IDataRouterAttribute dra = albianObject.getDefaultRouting();

            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "albian-object:%s reader-data-router is not found and use default:%s.",
                    className, dra.getName());
            return dra;
        }
        if (!routing.getEnable()) {
            IDataRouterAttribute dra = albianObject.getDefaultRouting();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "the reader-date-router is not appoint and the object:%s found router:%s but it disable. then use defaut:%s.",
                    className, routing.getName(), dra.getName());
            return dra;
        }
        return routing;
    }

    protected String parserRoutingStorage(Class<?> cls, String sessionId, boolean isExact, IDataRouterAttribute readerRouting,
                                          Map<String, IFilterCondition> hashWheres, Map<String, IOrderByCondition> hashOrderbys)
            throws AlbianDataServiceException {
//        String className = cls.getName();
        IAlbianObjectAttribute albianObject = AlbianEntityMetadata.getEntityMetadataByType(cls);
        String className = cls.getName();
        IDataRoutersAttribute routings = albianObject.getDataRouters();
//
//        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
//        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);


        if (null == readerRouting) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the reader data router of object:%s is null.", className);
        }

        if (null == routings) {
            String name = readerRouting.getStorageName();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    "albian-object:%s reader-data-router is not found and use default storage:%s.",
                    className, name);
            return name;
        }
        IAlbianObjectDataRouter router = routings.getDataRouter();
        if (null == router) {
            String name = readerRouting.getStorageName();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    IAlbianLoggerService.AlbianSqlLoggerName,
                    "albian-object:%s reader-data-router arithmetic is not found and use default storage:%s.",
                    className, name);
            return name;
        }

        String name = isExact ? router.mappingExactReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys)
                : router.mappingReaderRoutingStorage(readerRouting, hashWheres, hashOrderbys);
        if (Validate.isNullOrEmpty(name)) {
            String dname = readerRouting.getStorageName();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Warn,
                    IAlbianLoggerService.AlbianSqlLoggerName,
                    IAlbianLoggerService.AlbianSqlLoggerName,
                    "albian-object:%s reader-data-router is not found by arithmetic and use default storage:%s.",
                    className, dname);
            return dname;
        } else {
            return name;
        }
    }

    */

}
