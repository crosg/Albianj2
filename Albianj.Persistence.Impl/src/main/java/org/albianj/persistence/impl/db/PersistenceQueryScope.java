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
package org.albianj.persistence.impl.db;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.*;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.impl.toolkit.ResultConvert;
import org.albianj.persistence.object.IAlbianObject;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.beans.PropertyDescriptor;
import java.sql.*;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PersistenceQueryScope extends FreePersistenceQueryScope implements IPersistenceQueryScope {
    protected void perExecute(IReaderJob job) throws AlbianDataServiceException {
        String sessionId = job.getId();
        PersistenceNamedParameter.parseSql(job.getCommand());
        IRunningStorageAttribute rsa = job.getStorage();
        IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
        long begin1 =System.currentTimeMillis();
        IDataBasePool dbp = asps.getDatabasePool(sessionId,rsa);
        Connection conn = dbp.getConnection(sessionId,rsa);
//        Connection conn = asps.getConnection(sessionId, rsa);
        if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
            long end1 =System.currentTimeMillis();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Mark,
                    "SpxLog get connection from pools use times:%d.",
                    end1 - begin1);
        }
        job.setConnection(conn);
        job.setDatabasePool(dbp);
        IPersistenceCommand cmd = job.getCommand();
        PreparedStatement statement = null;
        try {
            statement = job.getConnection().prepareStatement(cmd.getCommandText());
        } catch (SQLException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "get the statement is fail.");
        }
        Map<Integer, String> map = cmd.getParameterMapper();
        if (!Validate.isNullOrEmpty(map)) {
            for (int i = 1; i <= map.size(); i++) {
                String paraName = map.get(i);
                ISqlParameter para = cmd.getParameters().get(paraName);
                try {
                    if (null == para.getValue()) {

                        statement.setNull(i, para.getSqlType());
                    } else {
                        statement.setObject(i, para.getValue(), para.getSqlType());
                    }
                } catch (SQLException e) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            job.getId(), AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "set the sql paras is error.para name:%s,para value:%s.",
                            para.getName(), ResultConvert.sqlValueToString(para.getSqlType(), para.getValue()));
                }
            }
        }
        job.setStatement(statement);
        return;
    }

    protected void executing(IReaderJob job) throws AlbianDataServiceException {
        String text = job.getCommand().getCommandText();
        Map<String, ISqlParameter> map = job.getCommand().getParameters();
        IRunningStorageAttribute st = job.getStorage();
        String sessionId = job.getId();

        ResultSet result = null;
        try {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Info,
                    "Storage:%s,database:%s,SqlText:%s,paras:%s.",
                    st.getStorageAttribute().getName(), st.getDatabase(), text, ListConvert.toString(map));
            long begin1 = System.currentTimeMillis();
            result = ((PreparedStatement) job.getStatement()).executeQuery();

            if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
                long end1 = System.currentTimeMillis();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        job.getId(), AlbianLoggerLevel.Mark,
                        "SpxLog execute query use times:%d.",
                        end1 - begin1);
            }
        } catch (SQLException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "execute the reader job is fail.");
        }
        job.setResult(result);
    }

    protected <T extends IAlbianObject> List<T> executed(Class<T> cls, IReaderJob job)
            throws AlbianDataServiceException {
        long begin1 =System.currentTimeMillis();
        String sessionId = job.getId();
        List<T> list = executed(cls, job.getId(), job.getResult());
        if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
            long end1 =System.currentTimeMillis();
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    job.getId(), AlbianLoggerLevel.Mark,
                    "SpxLog executed query and make data result use times:%d.",
                    end1 - begin1);
        }
        return list;
    }

    protected void unloadExecute(IReaderJob job) throws AlbianDataServiceException {
        String sessionId = job.getId();
        IRunningStorageAttribute rsa = job.getStorage();
        IDataBasePool dbp = job.getDatabasePool();
        dbp.returnConnection(sessionId,rsa.getStorageAttribute().getName(),rsa.getDatabase(),
                job.getConnection(),job.getStatement(),job.getResult());
//        try {
//            long begin1 = Calendar.getInstance().getTimeInMillis();
//            job.getResult().close();
//            job.setResult(null);
//            long end1 = Calendar.getInstance().getTimeInMillis();
//            if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                        job.getId(), AlbianLoggerLevel.Mark,
//                        "SpxLog close data result with connection use times:%d.",
//                        end1 - begin1);
//            }
//        } catch (SQLException e) {
//            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
//                    job.getId(), AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
//                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
//                    "unload job is fail.");
//        } finally {
//            try {
//                long begin1 = Calendar.getInstance().getTimeInMillis();
//                ((PreparedStatement) job.getStatement()).clearParameters();
//                job.getStatement().close();
//                job.setStatement(null);
//                long end1 = Calendar.getInstance().getTimeInMillis();
//                if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
//                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                            job.getId(), AlbianLoggerLevel.Mark,
//                            "SpxLog close sql statement with connection use times:%d.",
//                            end1 - begin1);
//                }
//            } catch (SQLException e) {
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                        job.getId(), AlbianLoggerLevel.Error,e,
//                        "close the statement when unload exec job is fail");
//            } finally {
//                try {
//                    long begin1 = Calendar.getInstance().getTimeInMillis();
//                    job.getConnection().close();
//                    long end1 = Calendar.getInstance().getTimeInMillis();
//                    if(!Validate.isNullOrEmptyOrAllSpace(sessionId) && sessionId.endsWith("_SPX_LOG")){
//                        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                                job.getId(), AlbianLoggerLevel.Mark,
//                                "SpxLog close connection use times:%d.",
//                                end1 - begin1);
//                    }
//                } catch (SQLException e) {
//                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                            job.getId(), AlbianLoggerLevel.Error,e,
//                            "close the connection when unload exec job is fail");
//                }
//            }
//        }
    }

    protected ResultSet executing(String sessionId, PersistenceCommandType cmdType, Statement statement)
            throws AlbianDataServiceException {
        try {
            if (PersistenceCommandType.Text == cmdType) {
                return ((PreparedStatement) statement).executeQuery();
            }
            return ((CallableStatement) statement).executeQuery();
        } catch (SQLException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "execute the reader job fail.");
        }

        return null;
    }

    protected <T extends IAlbianObject> List<T> executed(Class<T> cli, String sessionId, ResultSet result)
            throws AlbianDataServiceException {
        String inter = cli.getName();

        IAlbianMappingParserService amps = AlbianServiceRouter.getSingletonService(IAlbianMappingParserService.class, IAlbianMappingParserService.Name);
        IAlbianObjectAttribute attr = amps.getAlbianObjectAttribute(inter);
        String className = attr.getType();
        PropertyDescriptor[] propertyDesc = amps.getAlbianObjectPropertyDescriptor(className);
        Map<String, IMemberAttribute> members = attr.getMembers();
        Class<?> cls = null;
        try {
            cls = AlbianClassLoader.getInstance().loadClass(className);
        } catch (ClassNotFoundException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "class:%s is not found.",className);
        }
        List<T> list = new Vector<T>();
        try {
            while (result.next()) {
                try {
                    @SuppressWarnings("unchecked")
                    T obj = (T) cls.newInstance();
                    for (PropertyDescriptor desc : propertyDesc) {
                        String name = desc.getName();
                        IMemberAttribute ma = members.get(name.toLowerCase());
                        if (null == ma)
                            continue;
                        if (!ma.getIsSave()) {
                            if (name.equals("isAlbianNew")) {
                                desc.getWriteMethod().invoke(obj, false);
                            }
                            continue;
                        }

                        Object v = result.getObject(name);
                        if (null != v) {
                            Object rc = ResultConvert.toBoxValue(desc.getPropertyType(), v);
                            desc.getWriteMethod().invoke(obj, rc);
                            obj.setOldAlbianObject(name, rc);
                        }
                    }
                    list.add(obj);
                } catch (Exception e) {
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "create object from class:%s is  fail.",className);
                }
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "loop the result from database for class:%s is error.",className);
        }

        return list;
    }

    @Override
    protected Object executed(String sessionId, IReaderJob job) throws AlbianDataServiceException {
        Object v = null;
        ResultSet result = job.getResult();
        try {
            if (result.next()) {
                v = result.getObject("COUNT");
            }
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    sessionId, AlbianLoggerLevel.Error,e, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "get pagesize is null.");
        }

        return v;
    }
}