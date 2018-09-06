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

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.db.PersistenceCommandType;
import org.albianj.persistence.impl.db.PersistenceCommand;
import org.albianj.persistence.impl.db.SqlParameter;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.impl.toolkit.EnumMapping;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.object.*;
import org.albianj.persistence.object.filter.IChainExpression;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class FreeReaderJobAdapter implements IReaderJobAdapter {

    @Deprecated
    public IReaderJob buildReaderJob(String sessionId, Class<?> cls, boolean isExact, String routingName,
                                     int start, int step, LinkedList<IFilterCondition> wheres,
                                     LinkedList<IOrderByCondition> orderbys,String idxName) throws AlbianDataServiceException {
        IReaderJob job = new ReaderJob(sessionId);
        String className = cls.getName();
        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);

        if (null == albianObject) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s attribute is not found.", className);
        }

        Map<String, IFilterCondition> hashWheres = ListConvert
                .toLinkedHashMap(wheres);
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        IDataRouterAttribute readerRouting = parserReaderRouting(cls, job.getId(), isExact, routingName,
                hashWheres, hashOrderbys);
        if (null == readerRouting) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s reader-data-router is not found.", className);
        }
        String storageName = parserRoutingStorage(cls, job.getId(), isExact, readerRouting,
                hashWheres, hashOrderbys);

        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IStorageAttribute storage = asps.getStorageAttribute(storageName);

        StringBuilder sbCols = new StringBuilder();
        StringBuilder sbWhere = new StringBuilder();
        StringBuilder sbOrderby = new StringBuilder();
        StringBuilder sbStatement = new StringBuilder();
        Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();
        for (String key : albianObject.getMembers().keySet()) {
            IMemberAttribute member = albianObject.getMembers().get(key);
            if (null == member) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "albian-object:%s member:%s is not found.", className, key);
            }
            if (!member.getIsSave())
                continue;
            if (member.getSqlFieldName().equals(member.getName())) {
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getSqlFieldName()).append("]").append(",");
                }
            } else {
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`")
                            .append(" AS ")
                            .append("`").append(member.getName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getSqlFieldName()).append("]")
                            .append(" AS ")
                            .append("[").append(member.getName()).append("]").append(",");
                }
            }
        }
        if (0 != sbCols.length())
            sbCols.deleteCharAt(sbCols.length() - 1);
        if (null != wheres) {
            for (IFilterCondition where : wheres) {
                if (where.isAddition()) continue;
                IMemberAttribute member = albianObject.getMembers().get(
                        where.getFieldName().toLowerCase());

                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", className, where.getFieldName());
                }

                sbWhere.append(" ")
                        .append(EnumMapping.toRelationalOperators(where
                                .getRelationalOperator()))
                        .append(where.isBeginSub() ? "(" : " ");
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbWhere.append("`").append(member.getSqlFieldName()).append("`");
                } else {
                    sbWhere.append("[").append(member.getSqlFieldName()).append("]");
                }
                sbWhere.append(
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
        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                IMemberAttribute member = albianObject.getMembers().get(
                        orderby.getFieldName().toLowerCase());
                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", className, orderby.getFieldName());
                }

                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
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
        String tableName = null;
        if (null == routings || null == routings.getDataRouter()) {
            tableName = readerRouting.getTableName();
        } else {

            tableName = isExact ? routings.getDataRouter().mappingExactReaderTable(
                    readerRouting, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderTable(
                    readerRouting, hashWheres, hashOrderbys);

            tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
                    .getTableName() : tableName;
        }

        String database = null;
        if (null == routings || null == routings.getDataRouter()) {
            database = storage.getDatabase();
        } else {

            database = isExact ? routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

            database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
        }

        sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbStatement.append("`").append(tableName).append("`");
        } else {
            sbStatement.append("[").append(tableName).append("]");
        }
        if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()
            && !Validate.isNullOrEmptyOrAllSpace(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbStatement.append(" FORCE INDEX (").append(idxName).append(") ");
        }
        sbStatement.append(" WHERE 1=1 ").append(sbWhere);
        if (0 != sbOrderby.length()) {
            sbStatement.append(" ORDER BY ").append(sbOrderby);
        }
        if (0 <= start && 0 < step) {
            sbStatement.append(" LIMIT ").append(start).append(", ")
                    .append(step);
        }
        if (0 > start && 0 < step) {
            sbStatement.append(" LIMIT ").append(step);
        }

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbStatement.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);


        job.setCommand(cmd);
        job.setStorage(new RunningStorageAttribute(storage, database));
        return job;
    }

    @Deprecated
    public IReaderJob buildReaderJob(String sessionId, Class<?> cls, boolean isExact, String routingName,
                                     LinkedList<IFilterCondition> wheres, LinkedList<IOrderByCondition> orderbys,String idxName) throws AlbianDataServiceException {
        IReaderJob job = new ReaderJob(sessionId);
        String className = cls.getName();
        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);

        if (null == albianObject) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s attribute is not found.", className);
        }

        Map<String, IFilterCondition> hashWheres = ListConvert
                .toLinkedHashMap(wheres);
        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        IDataRouterAttribute readerRouting = parserReaderRouting(cls, job.getId(), isExact, routingName,
                hashWheres, hashOrderbys);
        if (null == readerRouting) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s reader-data-router is not  found.", className);
        }
        String storageName = parserRoutingStorage(cls, job.getId(), isExact, readerRouting,
                hashWheres, hashOrderbys);

        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);

        IStorageAttribute storage = asps.getStorageAttribute(storageName);


        StringBuilder sbCols = new StringBuilder();
        StringBuilder sbWhere = new StringBuilder();
        StringBuilder sbStatement = new StringBuilder();
        Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();

        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" `COUNT` ");
        } else {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" [COUNT] ");
        }

        if (null != wheres) {
            for (IFilterCondition where : wheres) {
                if (where.isAddition()) continue;
                IMemberAttribute member = albianObject.getMembers().get(
                        where.getFieldName().toLowerCase());

                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", className, where.getFieldName());
                }

                sbWhere.append(" ")
                        .append(EnumMapping.toRelationalOperators(where
                                .getRelationalOperator()))
                        .append(where.isBeginSub() ? "(" : " ");
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbWhere.append(" `").append(member.getSqlFieldName()).append("` ");
                } else {
                    sbWhere.append(" [").append(member.getSqlFieldName()).append("] ");
                }
                sbWhere.append(
                        EnumMapping.toLogicalOperation(where
                                .getLogicalOperation())).append("#")
                        .append(Validate.isNullOrEmptyOrAllSpace(where.getAliasName()) ? member.getSqlFieldName() : where.getAliasName())
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
        String tableName = null;
        if (null == routings || null == routings.getDataRouter()) {
            tableName = readerRouting.getTableName();
        } else {

            tableName = isExact ? routings.getDataRouter().mappingExactReaderTable(
                    readerRouting, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderTable(
                    readerRouting, hashWheres, hashOrderbys);

            tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
                    .getTableName() : tableName;
        }

        String database = null;
        if (null == routings || null == routings.getDataRouter()) {
            database = storage.getDatabase();
        } else {

            database = isExact ? routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

            database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
        }

        sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbStatement.append("`").append(tableName).append("`");
        } else {
            sbStatement.append("[").append(tableName).append("]");
        }
        if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()
                && !Validate.isNullOrEmptyOrAllSpace(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbStatement.append(" FORCE INDEX (").append(idxName).append(") ");
        }
        sbStatement.append(" WHERE 1=1 ").append(sbWhere);

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbStatement.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);


        job.setCommand(cmd);
        job.setStorage(new RunningStorageAttribute(storage, database));
        return job;

    }

    protected abstract IDataRouterAttribute parserReaderRouting(Class<?> cls, String jobId, boolean isExact,
                                                                String routingName, Map<String, IFilterCondition> hashWheres,
                                                                Map<String, IOrderByCondition> hashOrderbys) throws AlbianDataServiceException;

    protected abstract String parserRoutingStorage(Class<?> cls, String jobId, boolean isExact,
                                                   IDataRouterAttribute readerRouting,
                                                   Map<String, IFilterCondition> hashWheres,
                                                   Map<String, IOrderByCondition> hashOrderbys) throws AlbianDataServiceException;

    public IReaderJob buildReaderJob(String sessionId, Class<?> cls, boolean isExact, String routingName,
                                     int start, int step, IChainExpression f,
                                     LinkedList<IOrderByCondition> orderbys,String idxName) throws AlbianDataServiceException {
        IReaderJob job = new ReaderJob(sessionId);
        String className = cls.getName();
        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);

        if (null == albianObject) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s attribute is not found.", className);
        }


        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = new HashMap<>();

        ChainExpressionParser.toFilterConditionMap(f, hashWheres);

        IDataRouterAttribute readerRouting = parserReaderRouting(cls, job.getId(), isExact, routingName,
                hashWheres, hashOrderbys);
        if (null == readerRouting) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s reader-data-router is not found.", className);
        }
        String storageName = parserRoutingStorage(cls, job.getId(), isExact, readerRouting,
                hashWheres, hashOrderbys);

        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IStorageAttribute storage = asps.getStorageAttribute(storageName);

        StringBuilder sbCols = new StringBuilder();
        StringBuilder sbWhere = new StringBuilder();
        StringBuilder sbOrderby = new StringBuilder();
        StringBuilder sbStatement = new StringBuilder();
        Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();
        for (String key : albianObject.getMembers().keySet()) {
            IMemberAttribute member = albianObject.getMembers().get(key);
            if (null == member) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "albian-object:%s member:%s is not found.", className, key);
            }
            if (!member.getIsSave())
                continue;
            if (member.getSqlFieldName().equals(member.getName())) {
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getName()).append("]").append(",");
                }
            } else {
                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
                    sbCols.append("`").append(member.getSqlFieldName()).append("`")
                            .append(" AS ")
                            .append("`").append(member.getName()).append("`").append(",");
                } else {
                    sbCols.append("[").append(member.getSqlFieldName()).append("]")
                            .append(" AS ")
                            .append("[").append(member.getName()).append("]").append(",");
                }
            }
        }
        if (0 != sbCols.length())
            sbCols.deleteCharAt(sbCols.length() - 1);

        ChainExpressionParser.toConditionText(sessionId, cls, albianObject, storage, f, sbWhere, paras);

        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                IMemberAttribute member = albianObject.getMembers().get(
                        orderby.getFieldName().toLowerCase());
                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", className, orderby.getFieldName());
                }

                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
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
        String tableName = null;
        if (null == routings || null == routings.getDataRouter()) {
            tableName = readerRouting.getTableName();
        } else {

            tableName = isExact ? routings.getDataRouter().mappingExactReaderTable(
                    readerRouting, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderTable(
                    readerRouting, hashWheres, hashOrderbys);

            tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
                    .getTableName() : tableName;
        }

        String database = null;
        if (null == routings || null == routings.getDataRouter()) {
            database = storage.getDatabase();
        } else {

            database = isExact ? routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

            database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
        }

        sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbStatement.append("`").append(tableName).append("`");
        } else {
            sbStatement.append("[").append(tableName).append("]");
        }
        if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()
                && !Validate.isNullOrEmptyOrAllSpace(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbStatement.append(" FORCE INDEX (").append(idxName).append(") ");
        }
        if(!Validate.isNullOrEmptyOrAllSpace(sbWhere.toString())) {
            sbStatement.append(" WHERE ").append(sbWhere);
        }
        if (0 != sbOrderby.length()) {
            sbStatement.append(" ORDER BY ").append(sbOrderby);
        }
        if (0 <= start && 0 < step) {
            sbStatement.append(" LIMIT ").append(start).append(", ")
                    .append(step);
        }
        if (0 > start && 0 < step) {
            sbStatement.append(" LIMIT ").append(step);
        }

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbStatement.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);


        job.setCommand(cmd);
        job.setStorage(new RunningStorageAttribute(storage, database));
        return job;
    }

    public IReaderJob buildReaderJob(String sessionId, Class<?> cls, boolean isExact, String routingName,
                                     IChainExpression f,
                                     LinkedList<IOrderByCondition> orderbys,String idxName) throws AlbianDataServiceException {
        IReaderJob job = new ReaderJob(sessionId);
        String className = cls.getName();
        IAlbianDataRouterParserService adrps = AlbianServiceRouter.getSingletonService(IAlbianDataRouterParserService.class, IAlbianDataRouterParserService.Name);
        IDataRoutersAttribute routings = adrps.getDataRouterAttribute(className);

        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute albianObject = amps.getAlbianObjectAttribute(className);

        if (null == albianObject) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s attribute is not found.", className);
        }


        Map<String, IOrderByCondition> hashOrderbys = ListConvert
                .toLinkedHashMap(orderbys);

        Map<String, IFilterCondition> hashWheres = new HashMap<>();

        ChainExpressionParser.toFilterConditionMap(f, hashWheres);

        IDataRouterAttribute readerRouting = parserReaderRouting(cls, job.getId(), isExact, routingName,
                hashWheres, hashOrderbys);
        if (null == readerRouting) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "albian-object:%s reader-data-router is not found.", className);
        }
        String storageName = parserRoutingStorage(cls, job.getId(), isExact, readerRouting,
                hashWheres, hashOrderbys);

        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        IStorageAttribute storage = asps.getStorageAttribute(storageName);

        StringBuilder sbCols = new StringBuilder();
        StringBuilder sbWhere = new StringBuilder();
        StringBuilder sbOrderby = new StringBuilder();
        StringBuilder sbStatement = new StringBuilder();
        Map<String, ISqlParameter> paras = new HashMap<String, ISqlParameter>();

        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" `COUNT` ");
        } else {
            sbCols.append(" COUNT(1) ")
                    .append(" AS ")
                    .append(" [COUNT] ");
        }

        ChainExpressionParser.toConditionText(sessionId, cls, albianObject, storage, f, sbWhere, paras);

        if (null != orderbys) {
            for (IOrderByCondition orderby : orderbys) {
                IMemberAttribute member = albianObject.getMembers().get(
                        orderby.getFieldName().toLowerCase());
                if (null == member) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId,AlbianLoggerLevel.Error,null,AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "albian-object:%s member:%s is not found.", className, orderby.getFieldName());
                }

                if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
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
        String tableName = null;
        if (null == routings || null == routings.getDataRouter()) {
            tableName = readerRouting.getTableName();
        } else {

            tableName = isExact ? routings.getDataRouter().mappingExactReaderTable(
                    readerRouting, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderTable(
                    readerRouting, hashWheres, hashOrderbys);

            tableName = Validate.isNullOrEmptyOrAllSpace(tableName) ? readerRouting
                    .getTableName() : tableName;
        }

        String database = null;
        if (null == routings || null == routings.getDataRouter()) {
            database = storage.getDatabase();
        } else {

            database = isExact ? routings.getDataRouter().mappingExactReaderRoutingDatabase(storage, hashWheres, hashOrderbys)
                    : routings.getDataRouter().mappingReaderRoutingDatabase(storage, hashWheres, hashOrderbys);

            database = Validate.isNullOrEmptyOrAllSpace(database) ? storage.getDatabase() : database;
        }

        sbStatement.append("SELECT ").append(sbCols).append(" FROM ");
        if (PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()) {
            sbStatement.append("`").append(tableName).append("`");
        } else {
            sbStatement.append("[").append(tableName).append("]");
        }

        if(PersistenceDatabaseStyle.MySql == storage.getDatabaseStyle()
                && !Validate.isNullOrEmptyOrAllSpace(idxName)) { //按照木木的要求，增加强行执行索引行为，只为mysql使用
            sbStatement.append(" FORCE INDEX (").append(idxName).append(") ");
        }

        if(!Validate.isNullOrEmptyOrAllSpace(sbWhere.toString())) {
            sbStatement.append(" WHERE ").append(sbWhere);
        }

        if (0 != sbOrderby.length()) {
            sbStatement.append(" ORDER BY ").append(sbOrderby);
        }

        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(sbStatement.toString());
        cmd.setParameters(paras);
        cmd.setCommandType(PersistenceCommandType.Text);


        job.setCommand(cmd);
        job.setStorage(new RunningStorageAttribute(storage, database));
        return job;
    }

    public IReaderJob buildReaderJob(String sessionId, Class<?> cls, IRunningStorageAttribute storage,
                                     PersistenceCommandType cmdType,String text,Map<String, ISqlParameter> paras) throws AlbianDataServiceException {
        IReaderJob job = new ReaderJob(sessionId);
        IPersistenceCommand cmd = new PersistenceCommand();
        cmd.setCommandText(text);
        cmd.setParameters(paras);
        cmd.setCommandType(cmdType);
        job.setCommand(cmd);
        job.setStorage(storage);
        return job;
    }


}
