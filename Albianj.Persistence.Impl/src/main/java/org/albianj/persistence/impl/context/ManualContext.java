package org.albianj.persistence.impl.context;

import org.albianj.persistence.context.IInternalManualCommand;
import org.albianj.persistence.context.IManualCommand;
import org.albianj.persistence.context.IManualContext;
import org.albianj.persistence.context.WriterJobLifeTime;
import org.albianj.persistence.object.IRunningStorageAttribute;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuhaifeng on 17/8/31.
 */
public class ManualContext implements IManualContext {

    private String sessionId;
    private List<IManualCommand> cmds;
    private List<IInternalManualCommand> internalCmds;
    private Connection conn;
    private List<Statement> statements;
    private String storageName;
    private IRunningStorageAttribute rsa;
    private String dbName;
    private List<Integer> rcs;
    private WriterJobLifeTime  lifeTime = WriterJobLifeTime.Normal;

    @Override
    public String getSessionId() {
        return this.sessionId;
    }

    @Override
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public List<IManualCommand> getCommands() {
        return this.cmds;
    }

    @Override
    public void setCommands(List<IManualCommand> cmds) {
        this.cmds = cmds;
    }

    @Override
    public List<IInternalManualCommand> getInternalCommands() {
        return this.internalCmds;
    }

    @Override
    public void setInternelCommands(List<IInternalManualCommand> cmds) {
        this.internalCmds = cmds;
    }

    @Override
    public Connection getConnection() {
        return this.conn;
    }

    @Override
    public void setConnection(Connection connection) {
        this.conn = connection;
    }

    @Override
    public List<Statement> getStatements() {
        return this.statements;
    }

    @Override
    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String getStorageName() {
        return this.storageName;
    }

    @Override
    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    @Override
    public IRunningStorageAttribute getRunningStorage() {
        return this.rsa;
    }

    @Override
    public void setRunningStorage(IRunningStorageAttribute rsa) {
        this.rsa = rsa;
    }

    @Override
    public String getDatabaseName() {
        return this.dbName;
    }

    @Override
    public void setDatabaseName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public List<Integer> getResults() {
        return this.rcs;
    }

    @Override
    public void setResults(List<Integer> rcs) {
        this.rcs = rcs;
    }

    /**
     * 得到写操作的生命周期
     *
     * @return
     */
    @Override
    public WriterJobLifeTime getLifeTime() {
        return this.lifeTime;
    }

    /**
     * 设置写操作事务的生命周期
     *
     * @param lifeTime
     */
    @Override
    public void setLifeTime(WriterJobLifeTime lifeTime) {
        this.lifeTime = lifeTime;
    }
}
