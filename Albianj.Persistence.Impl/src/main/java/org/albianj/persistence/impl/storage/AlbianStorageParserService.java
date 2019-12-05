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
package org.albianj.persistence.impl.storage;

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.impl.object.StorageAttribute;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.persistence.service.IAlbianConnectionMonitorService;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.albianj.persistence.object.DatabasePoolStyle.DBCP;
import static org.albianj.persistence.object.DatabasePoolStyle.valueOf;

@AlbianServiceRant(Id = IAlbianStorageParserService.Name, Interface = IAlbianStorageParserService.class)
public class AlbianStorageParserService extends FreeAlbianStorageParserService {

    public final static String DEFAULT_STORAGE_NAME = "!@#$%Albianj_Default_Storage%$#@!";

    private ConcurrentMap<String, IDataBasePool> pools = null;

    // <Storage>
    // <Name>1thStorage</Name>
    // <DatabaseStyle>MySql</DatabaseStyle>
    // <Server>localhost</Server>
    // <Database>BaseInfo</Database>
    // <Uid>root</Uid>
    // <Password>xuhf</Password>
    // <Pooling>false</Pooling>
    // <MinPoolSize>10</MinPoolSize>
    // <MaxPoolSize>20</MaxPoolSize>
    // <Timeout>60</Timeout>
    // <Charset>gb2312</Charset>
    // <Transactional>true</Transactional>
    // <TransactionLevel>0</TransactinLevel>
    // <L5>L5:mid:cmdId:0.3</>

    public String getServiceName() {
        return Name;
    }

    @Override
    public void init() throws AlbianParserException {
        pools = new ConcurrentHashMap<>(64);
        super.init();
    }

    @Override
    protected void parserStorages(@SuppressWarnings("rawtypes") List nodes) throws AlbianParserException {
        if (Validate.isNullOrEmpty(nodes)) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, "Storage node is null or size is 0.");
            return;
        }
        for (int i = 0; i < nodes.size(); i++) {
            IStorageAttribute storage = parserStorage((Element) nodes.get(i));
            if (null == storage) {
                AlbianServiceRouter.getLogger2()
                        .logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                                AlbianModuleType.AlbianPersistence.getThrowInfo(),
                                "parser storage in the storage.xml is fail.xml:%s.", ((Element) nodes.get(i)).asXML());
            }
            addStorageAttribute(storage.getName(), storage);
            if (i == 0) {
                addStorageAttribute(DEFAULT_STORAGE_NAME, storage);
            }
        }
    }

    @Override
    protected IStorageAttribute parserStorage(Element node) {
        String name = XmlParser.getSingleChildNodeValue(node, "Name");
        if (null == name) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, "There is no name attribute in the storage node.");
            return null;
        }
        String databaseStyle = XmlParser.getSingleChildNodeValue(node, "DatabaseStyle");
        String server = XmlParser.getSingleChildNodeValue(node, "Server");
        if (null == server) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, "There is no server attribute in the storage node.");
            return null;
        }
        String database = XmlParser.getSingleChildNodeValue(node, "Database");
        if (null == database) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, "There is no database attribute in the storage node.");
            return null;
        }
        String user = XmlParser.getSingleChildNodeValue(node, "User");
        if (null == user) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                            AlbianLoggerLevel.Error, "There is no uid attribute in the storage node.");
            return null;
        }
        String password = XmlParser.getSingleChildNodeValue(node, "Password");
        String pooling = XmlParser.getSingleChildNodeValue(node, "Pooling");
        String minPoolSize = XmlParser.getSingleChildNodeValue(node, "MinPoolSize");
        String maxPoolSize = XmlParser.getSingleChildNodeValue(node, "MaxPoolSize");
        String timeout = XmlParser.getSingleChildNodeValue(node, "Timeout");
        String charset = XmlParser.getSingleChildNodeValue(node, "Charset");
        String transactional = XmlParser.getSingleChildNodeValue(node, "Transactional");
        String transactionLevel = XmlParser.getSingleChildNodeValue(node, "TransactionLevel");
        String port = XmlParser.getSingleChildNodeValue(node, "Port");

        String options = XmlParser.getSingleChildNodeValue(node, "Options");

        String sidleTime = XmlParser.getSingleChildNodeValue(node, "AliveTime");

        String sDatabasePoolStyle = XmlParser.getSingleChildNodeValue(node, "PoolStyle");

        IStorageAttribute storage = new StorageAttribute();
        storage.setName(name);
        if (null == databaseStyle) {
            storage.setDatabaseStyle(PersistenceDatabaseStyle.MySql);
        } else {
            String style = databaseStyle.trim().toLowerCase();
            storage.setDatabaseStyle("sqlserver".equalsIgnoreCase(style) ? PersistenceDatabaseStyle.SqlServer :
                    "oracle".equalsIgnoreCase(style) ? PersistenceDatabaseStyle.Oracle : PersistenceDatabaseStyle.MySql);
        }
        storage.setServer(server);
        storage.setDatabase(database);
        storage.setUser(user);
        storage.setPassword(Validate.isNullOrEmptyOrAllSpace(password) ? "" : password);
        storage.setPooling(Validate.isNullOrEmptyOrAllSpace(pooling) ? true : new Boolean(pooling));
        int minsize = Validate.isNullOrEmptyOrAllSpace(minPoolSize) ? 2 : new Integer(minPoolSize);
        minsize = 2 < minsize ? 2 : minsize;
        storage.setMinSize(minsize);//固定数据库链接池最小的链接为2
        storage.setMaxSize(Validate.isNullOrEmptyOrAllSpace(maxPoolSize) ? 20 : new Integer(maxPoolSize));
        storage.setTimeout(Validate.isNullOrEmptyOrAllSpace(timeout) ? 30 : new Integer(timeout));
        storage.setCharset(Validate.isNullOrEmptyOrAllSpace(charset) ? null : charset);
        storage.setTransactional(Validate.isNullOrEmptyOrAllSpace(transactional) ? true : new Boolean(transactional));
        storage.setAliveTime(Validate.isNullOrEmptyOrAllSpace(sidleTime) ? 120 : new Integer(sidleTime));
        storage.setDatabasePoolStyle(
                Validate.isNullOrEmptyOrAllSpace(sDatabasePoolStyle) ? DBCP : valueOf(sDatabasePoolStyle));

        String sWaitTimeWhenGetMs = XmlParser.getSingleChildNodeValue(node, "WaitTimeWhenGetMs");
        String sLifeCycleTime = XmlParser.getSingleChildNodeValue(node, "LifeCycleTime");
        String sWaitInFreePoolMs = XmlParser.getSingleChildNodeValue(node, "WaitInFreePoolMs");
        String sMaxRemedyConnectionCount = XmlParser.getSingleChildNodeValue(node, "MaxRemedyConnectionCount");
        String sCleanupTimestampMs = XmlParser.getSingleChildNodeValue(node, "CleanupTimestampMs");
        String sMaxRequestTimeMs = XmlParser.getSingleChildNodeValue(node, "MaxRequestTimeMs");
        String sL5 = XmlParser.getSingleChildNodeValue(node,"L5");

        if (!Validate.isNullOrEmptyOrAllSpace(sWaitTimeWhenGetMs)) {
            storage.setWaitTimeWhenGetMs(new Integer(sWaitTimeWhenGetMs));
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sLifeCycleTime)) {
            storage.setLifeCycleTime(new Integer(sLifeCycleTime));
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sWaitInFreePoolMs)) {
            storage.setWaitInFreePoolMs(new Integer(sWaitInFreePoolMs));
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sMaxRemedyConnectionCount)) {
            storage.setMaxRemedyConnectionCount(new Integer(sMaxRemedyConnectionCount));
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sCleanupTimestampMs)) {
            storage.setCleanupTimestampMs(new Integer(sCleanupTimestampMs));
        }
        if (!Validate.isNullOrEmptyOrAllSpace(sMaxRequestTimeMs)) {
            storage.setMaxRequestTimeMs(new Integer(sMaxRequestTimeMs));
        }
        storage.setOptions(options);
        if(!Validate.isNullOrEmptyOrAllSpace(sL5)) {
            storage.setL5(sL5);
        }

        if (storage.getTransactional()) {
            if (Validate.isNullOrEmpty(transactionLevel)) {
                // default level and do not means no suppert tran
                storage.setTransactionLevel(Connection.TRANSACTION_NONE);
            } else {
                if (transactionLevel.equalsIgnoreCase("READ_UNCOMMITTED")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_READ_UNCOMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("READ_COMMITTED")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_READ_COMMITTED);
                } else if (transactionLevel.equalsIgnoreCase("REPEATABLE_READ")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_REPEATABLE_READ);
                } else if (transactionLevel.equalsIgnoreCase("SERIALIZABLE")) {
                    storage.setTransactionLevel(Connection.TRANSACTION_SERIALIZABLE);
                } else {
                    // default level and do not means no suppert tran
                    storage.setTransactionLevel(Connection.TRANSACTION_NONE);
                }
            }
        }

        if (!Validate.isNullOrEmptyOrAllSpace(port)) {
            storage.setPort(new Integer(port));
        }

        return storage;
    }

    public IDataBasePool getDatabasePool(String sessionId, IRunningStorageAttribute rsa) {
        final IStorageAttribute sa = rsa.getStorageAttribute();
        String key = sa.getName();
        IDataBasePool dbp = pools.get(key);
        if (dbp != null) {
            return dbp;
        }
        try {
            synchronized (rsa.getStorageAttribute()) {
                //double check
                dbp = pools.get(key);
                if (dbp != null) {
                    return dbp;
                }
                switch (sa.getDatabasePoolStyle()) {
                    case C3P0: {
                        dbp = new C3P0Wapper();
                        break;
                    }
                    case HIKARICP: {
                        dbp = new HikariCPWapper();
                        break;
                    }
                    case DBCP: {
                        dbp = new DBCPWapper();
                        break;
                    }
                    case SpxDBCP: {
                        dbp = new SpxWapper();
                        break;
                    }
                    default: {
                        dbp = new C3P0Wapper();
                        break;
                    }
                }
                IAlbianConnectionMonitorService connectionMonitorService = AlbianServiceRouter
                        .getSingletonService(IAlbianConnectionMonitorService.class, IAlbianConnectionMonitorService.Name,
                                false);
                if (connectionMonitorService != null) {
                    dbp = new MonitorWrapper(dbp, connectionMonitorService);
                }
                pools.putIfAbsent(key, dbp);
            }

            return dbp;
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, sessionId, AlbianLoggerLevel.Error, e,
                            "Get the database connection pool with storage:%s and database:%s  is error.", sa.getName(),
                            rsa.getDatabase());
            return null;
        }
    }

    public Connection getConnection(IRunningStorageAttribute rsa,boolean isAutoCommit) {
        return getConnection(IAlbianLoggerService2.InnerThreadName, rsa,isAutoCommit);
    }

    public Connection getConnection(String sessionId, IRunningStorageAttribute rsa,boolean isAutoCommit) {
        IStorageAttribute sa = rsa.getStorageAttribute();
        //            String key = sa.getName() + rsa.getDatabase();
        try {

            IDataBasePool dbp = getDatabasePool(sessionId, rsa);
            if (null == dbp) {
                AlbianServiceRouter.getLogger2()
                        .log(IAlbianLoggerService2.AlbianRunningLoggerName, sessionId, AlbianLoggerLevel.Error,
                                "Get the database connection pool with storage:%s and database:%s  is error.", sa.getName(),
                                rsa.getDatabase());
                return null;
            }
            return dbp.getConnection(sessionId, rsa, isAutoCommit);

        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, sessionId, AlbianLoggerLevel.Error, e,
                            "Get the connection with storage:%s and database:%s form connection pool is error.", sa.getName(),
                            rsa.getDatabase());
            return null;
        }

    }

    public Connection getConnection(String sessionId, IDataBasePool pool, IRunningStorageAttribute rsa,boolean isAutoCommit) {
        IStorageAttribute sa = rsa.getStorageAttribute();
        try {
            if (null == pool) {
                AlbianServiceRouter.getLogger2()
                        .log(IAlbianLoggerService2.AlbianRunningLoggerName, IAlbianLoggerService2.InnerThreadName,
                                AlbianLoggerLevel.Error,
                                "Get the database connection pool with storage:%s and database:%s  is error.", sa.getName(),
                                rsa.getDatabase());
                return null;
            }
            return pool.getConnection(sessionId, rsa, isAutoCommit);

        } catch (Exception e) {
            AlbianServiceRouter.getLogger2()
                    .log(IAlbianLoggerService2.AlbianRunningLoggerName, sessionId, AlbianLoggerLevel.Error, e,
                            "Get the connection with storage:%s and database:%s form connection pool is error.", sa.getName(),
                            rsa.getDatabase());
            return null;
        }
    }

    /**
     * @param sessionId
     * @param rsa
     * @param conn
     */
    public void returnConnection(String sessionId, IRunningStorageAttribute rsa, Connection conn) {
        IDataBasePool dbp = getDatabasePool(sessionId, rsa);
        dbp.returnConnection(sessionId, rsa.getStorageAttribute().getName(), rsa.getDatabase(), conn);
    }

}
