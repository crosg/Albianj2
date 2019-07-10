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

import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.context.IWriterJob;
import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.persistence.db.AlbianDataServiceException;
import org.albianj.persistence.db.IDataBasePool;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.db.ISqlParameter;
import org.albianj.persistence.impl.toolkit.ListConvert;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class PersistenceTransactionClusterScope extends FreePersistenceTransactionClusterScope
        implements IPersistenceTransactionClusterScope {
    protected void preExecute(IWriterJob writerJob) throws AlbianDataServiceException {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Opening);
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    writerJob.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the task for the job is null or empty.");
        }

        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            writerJob.setCurrentStorage(task.getKey());
            IWriterTask t = task.getValue();
            IRunningStorageAttribute rsa = t.getStorage();
            IStorageAttribute storage = rsa.getStorageAttribute();
            if (null == storage) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "The storage for task is null.");
            }
            try {
                IAlbianStorageParserService asps = AlbianServiceRouter.getSingletonService(IAlbianStorageParserService.class, IAlbianStorageParserService.Name);
                IDataBasePool dbp = asps.getDatabasePool(writerJob.getId(), rsa);
                t.setDatabasePool(dbp);
                t.setConnection(asps.getConnection(writerJob.getId(), dbp, rsa,false));
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "get the connect to storage:%s is error.",
                        storage.getName());
            }
            List<IPersistenceCommand> cmds = t.getCommands();
            if (Validate.isNullOrEmpty(cmds)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "The commands for task is empty or null");
            }
            List<Statement> statements = new Vector<Statement>();
            try {
                for (IPersistenceCommand cmd : cmds) {
                    PreparedStatement prepareStatement = t
                            .getConnection().prepareStatement(cmd.getCommandText());
                    Map<Integer, String> map = cmd.getParameterMapper();
                    if (Validate.isNullOrEmpty(map)) {
                        continue;
                    } else {
                        for (int i = 1; i <= map.size(); i++) {
                            String paraName = map.get(i);
                            ISqlParameter para = cmd.getParameters().get(paraName);
                            if (null == para.getValue()) {
                                prepareStatement.setNull(i, para.getSqlType());
                            } else {
                                prepareStatement.setObject(i, para.getValue(),
                                        para.getSqlType());
                            }
                        }
                    }
                    statements.add(prepareStatement);
                }
            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "make sql command for task is empty or null");
            }
            t.setStatements(statements);
        }
    }

    protected void executeHandler(IWriterJob writerJob) throws AlbianDataServiceException {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Running);
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }

        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            writerJob.setCurrentStorage(task.getKey());
            List<Statement> statements = t.getStatements();
            List<IPersistenceCommand> cmds = t.getCommands();
            for (int i = 0; i < statements.size(); i++) {
                try {
                    IPersistenceCommand cmd = cmds.get(i);
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            writerJob.getId(), AlbianLoggerLevel.Info,
                            "storage:%s,sqltext:%s,parars:%s.",
                            task.getKey(), cmd.getCommandText(), ListConvert.toString(cmd.getParameters()));
                    ((PreparedStatement) statements.get(i)).executeUpdate();
                } catch (SQLException e) {
                    IRunningStorageAttribute rsa = t.getStorage();
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "execute to storage:%s dtabase:%s is fail.",
                            rsa.getStorageAttribute().getName(), rsa.getDatabase());
                }
            }
        }
    }

    protected void commit(IWriterJob writerJob) throws AlbianDataServiceException {
        writerJob.setWriterJobLifeTime(WriterJobLifeTime.Commiting);
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            try {
                writerJob.setCurrentStorage(task.getKey());
                t.getConnection().commit();
                t.setIsCommited(true);
                writerJob.setNeedManualRollbackIfException(true);
            } catch (SQLException e) {
                IRunningStorageAttribute rsa = t.getStorage();
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "commit to storage:%s database:%s is fail.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
            }
        }
    }

    protected void exceptionHandler(IWriterJob writerJob) throws AlbianDataServiceException {
        boolean isThrow = false;
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            try {
                if (!t.getIsCommited()) {
                    t.getConnection().rollback();
                }
            } catch (Exception e) {
                IRunningStorageAttribute rsa = t.getStorage();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e,
                        "rollback to storage:%s database:%s is error.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
                isThrow = true;
            }
        }
        if (isThrow)
            throw new AlbianDataServiceException("DataService is error.");
    }

    protected boolean exceptionManualRollback(IWriterJob writerJob) throws AlbianDataServiceException {
        try {
            manualRollbackPreExecute(writerJob);
            manualRollbackExecuteHandler(writerJob);
            manualRollbackCommit(writerJob);
            return true;
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                    writerJob.getId(), AlbianLoggerLevel.Error, e,
                    "manual rollback is fail.");
            return false;
        }
    }


    protected void unLoadExecute(IWriterJob writerJob) throws AlbianDataServiceException {
        boolean isThrow = false;
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }
        IWriterTask t = null;
        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            try {
                t = task.getValue();
                IRunningStorageAttribute rsa = t.getStorage();
                IDataBasePool dbp = t.getDatabasePool();
                dbp.returnConnection(writerJob.getId(), rsa.getStorageAttribute().getName(), rsa.getDatabase(),
                        t.getConnection(), t.getStatements());
            }catch (Exception e){
                isThrow = true;
                IRunningStorageAttribute rsa = t.getStorage();
                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error,e,
                        "close the connect to storage:%s database:%s is fail.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
            }finally {
                t = null;
            }
//            try {
//                List<Statement> statements = t.getStatements();
//                for (Statement statement : statements) {
//                    try {
//                        ((PreparedStatement) statement).clearParameters();
//                        statement.close();
//                    } catch (Exception e) {
//                        isThrow = true;
//                        IRunningStorageAttribute rsa = t.getStorage();
//                        AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                                writerJob.getId(), AlbianLoggerLevel.Error,e,
//                                "clear the statement to storage:%s database:%s is fail.",
//                                rsa.getStorageAttribute().getName(), rsa.getDatabase());
//                    }
//                }
//                t.getConn().close();
//            } catch (Exception exc) {
//                isThrow = true;
//                IRunningStorageAttribute rsa = t.getStorage();
//                AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
//                        writerJob.getId(), AlbianLoggerLevel.Error,exc,
//                        "close the connect to storage:%s database:%s is fail.",
//                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
//            }
        }
//        if (isThrow)
//            throw new AlbianDataServiceException(
//                    "there is error in the unload trancation scope.");
    }


    private void manualRollbackPreExecute(IWriterJob writerJob) throws AlbianDataServiceException {
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                    writerJob.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the task for the job is null or empty when manual rollbacking.");
        }

        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            if (!t.getIsCommited()) continue;// not commit then use auto rollback

            List<IPersistenceCommand> cmds = t.getCommands();
            if (Validate.isNullOrEmpty(cmds)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "The commands for task is empty or null when manual rollbacking.");
            }
            List<Statement> statements = new Vector<Statement>();
            List<IPersistenceCommand> rbkCmds = new Vector<>();
            try {
                for (IPersistenceCommand cmd : cmds) {
                    if (!cmd.getCompensating()) continue;
                    PreparedStatement prepareStatement = t
                            .getConnection().prepareStatement(cmd.getRollbackCommandText());
                    Map<Integer, String> map = cmd.getRollbackParameterMapper();
                    if (Validate.isNullOrEmpty(map)) {
                        continue;
                    } else {
                        for (int i = 1; i <= map.size(); i++) {
                            String paraName = map.get(i);
                            ISqlParameter para = cmd.getRollbackParameters().get(paraName);
                            if (null == para.getValue()) {
                                prepareStatement.setNull(i, para.getSqlType());
                            } else {
                                prepareStatement.setObject(i, para.getValue(),
                                        para.getSqlType());
                            }
                        }
                    }
                    statements.add(prepareStatement);
                    rbkCmds.add(cmd);
                }
            } catch (SQLException e) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "make sql command for task is empty or null when maunal rollbacking.");
            }
            if (!Validate.isNullOrEmpty(statements)) {
                t.setRollbackStatements(statements);
                t.setRollbackCommands(rbkCmds);
                t.setCompensating(true);
            }
        }
    }

    private void manualRollbackExecuteHandler(IWriterJob writerJob) throws AlbianDataServiceException {
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }

        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            if (!t.getIsCommited()) continue;
            if (!t.getCompensating()) continue;

            List<Statement> statements = t.getRollbackStatements();
            List<IPersistenceCommand> cmds = t.getRollbackCommands();
            if (Validate.isNullOrEmpty(statements)) continue;
            ;
            for (int i = 0; i < statements.size(); i++) {
                try {
                    IPersistenceCommand cmd = cmds.get(i);
                    AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianSqlLoggerName,
                            writerJob.getId(), AlbianLoggerLevel.Info,
                            "manual-rollback job,storage:%s,sqltext:%s,parars:%s.",
                            task.getKey(), cmd.getRollbackCommandText(),
                            ListConvert.toString(cmd.getRollbackParameters()));
                    ((PreparedStatement) statements.get(i)).executeUpdate();
                } catch (SQLException e) {
                    IRunningStorageAttribute rsa = t.getStorage();
                    AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                            writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                            AlbianModuleType.AlbianPersistence.getThrowInfo(),
                            "execute to storage:%s database:%s is error when manual rollbacking.",
                            rsa.getStorageAttribute().getName(), rsa.getDatabase());
                }
            }
        }
    }

    private void manualRollbackCommit(IWriterJob writerJob) throws AlbianDataServiceException {
        Map<String, IWriterTask> tasks = writerJob.getWriterTasks();
        if (Validate.isNullOrEmpty(tasks)) {
            throw new RuntimeException("The task is null or empty.");
        }
        for (Map.Entry<String, IWriterTask> task : tasks.entrySet()) {
            IWriterTask t = task.getValue();
            if (!t.getIsCommited()) continue;
            try {
                if (t.getCompensating()) {
                    t.getConnection().commit();
                }
            } catch (SQLException e) {
                IRunningStorageAttribute rsa = t.getStorage();
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianSqlLoggerName,
                        writerJob.getId(), AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "commit to storage:%s database:%s is error when manual rollbacking.",
                        rsa.getStorageAttribute().getName(), rsa.getDatabase());
            }
        }
    }


}
