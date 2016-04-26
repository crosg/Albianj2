package org.albianj.persistence.impl.context;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.albianj.persistence.context.IWriterTask;
import org.albianj.persistence.db.IPersistenceCommand;
import org.albianj.persistence.object.IRunningStorageAttribute;

public class WriterTask implements IWriterTask {
	private IRunningStorageAttribute storage = null;
	private List<IPersistenceCommand> commands = null;
	private Connection connection = null;
	private List<Statement> statements = null;
	private List<Statement> rollbackStatements = null;
	private boolean iscommited = false;

	public IRunningStorageAttribute getStorage() {
		// TODO Auto-generated method stub
		return this.storage;
	}

	public void setStorage(IRunningStorageAttribute storage) {
		// TODO Auto-generated method stub
		this.storage = storage;
	}

	public List<IPersistenceCommand> getCommands() {
		// TODO Auto-generated method stub
		return this.commands;
	}

	public void setCommands(List<IPersistenceCommand> commands) {
		// TODO Auto-generated method stub
		this.commands = commands;
	}

	public Connection getConnection() {
		// TODO Auto-generated method stub
		return this.connection;
	}

	public void setConnection(Connection connection) {
		// TODO Auto-generated method stub
		this.connection = connection;
	}

	public List<Statement> getStatements() {
		// TODO Auto-generated method stub
		return this.statements;
	}

	public void setStatements(List<Statement> statements) {
		// TODO Auto-generated method stub
		this.statements = statements;
	}

	@Override
	public boolean getIsCommited() {
		// TODO Auto-generated method stub
		return this.iscommited;
	}

	@Override
	public void setIsCommited(boolean iscommited) {
		// TODO Auto-generated method stub
		this.iscommited = iscommited;
	}

	@Override
	public List<Statement> getRollbackStatements() {
		// TODO Auto-generated method stub
		return this.rollbackStatements;
	}

	@Override
	public void setRollbackStatements(List<Statement> statements) {
		// TODO Auto-generated method stub
		this.rollbackStatements = statements;
	}

}
