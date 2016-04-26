package org.albianj.persistence.impl.context;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.albianj.persistence.context.IReaderJob;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;

public class ReaderJob implements IReaderJob {
	private IRunningStorageAttribute storage = null;
	private IPersistenceCommand command = null;
	private Connection connection = null;
	private Statement statement = null;
	private ResultSet result = null;
	private String id =  null;
	
	public ReaderJob(String sessionId){
		if(Validate.isNullOrEmptyOrAllSpace(sessionId)) {
			this.id = AlbianServiceRouter.getLogIdService().makeLoggerId();
		} else {
			this.id = sessionId;
		}
	}

	public IRunningStorageAttribute getStorage() {
		return storage;
	}

	public void setStorage(IRunningStorageAttribute storage) {
		this.storage = storage;
	}

	public IPersistenceCommand getCommand() {
		return command;
	}

	public void setCommand(IPersistenceCommand command) {
		this.command = command;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public ResultSet getResult() {
		return this.result;
	}

	public void setResult(ResultSet result) {
		this.result = result;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return this.id;
	}
}
