package org.albianj.persistence.context;

import org.albianj.persistence.object.IRunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public interface IManualContext {

    String getSessionId();
    void setSessionId(String sessionId);

    public List<IManualCommand> getCommands();
    public void setCommands(List<IManualCommand> cmds);

    List<IInternalManualCommand> getInternalCommands();
    void setInternelCommands(List<IInternalManualCommand> cmds);

    Connection getConnection();
    void setConnection(Connection connection);

    List<Statement> getStatements();
    void setStatements(List<Statement> statements);

    String getStorageName();
    void setStorageName(String storageName);

    IRunningStorageAttribute getRunningStorage();
    void setRunningStorage(IRunningStorageAttribute rsa);

    String getDatabaseName();
    void setDatabaseName(String dbName);

    List<Integer> getResults();
    void setResults(List<Integer> rcs);



    /**
     * 得到写操作的生命周期
     * @return
     */
    public WriterJobLifeTime getLifeTime();

    /** 设置写操作事务的生命周期
     * @param writerJobLifeTime 写事务的生命周期
     */
    public void setLifeTime(WriterJobLifeTime lifeTime);

}
