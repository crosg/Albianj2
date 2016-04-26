package org.albianj.persistence.impl.object;

import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;

public class StorageAttribute implements IStorageAttribute {

	private String name = null;
	private int databaseStyle = PersistenceDatabaseStyle.MySql;
	private String database = null;
	private String user = null;
	private String password = null;
	private boolean pooling = true;
	private int minSize = 5;
	private int maxSize = 10;
	private int timeout = 30;
	private String charset = null;
	private boolean transactional = true;
	private String server = null;
	private int port = 0;
	private int transactionLevel = 0;
	private String options = null;

	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	public int getDatabaseStyle() {
		// TODO Auto-generated method stub
		return this.databaseStyle;
	}

	public void setDatabaseStyle(int databaseStyle) {
		// TODO Auto-generated method stub
		this.databaseStyle = databaseStyle;
	}

	public String getDatabase() {
		// TODO Auto-generated method stub
		return this.database;
	}

	public void setDatabase(String database) {
		// TODO Auto-generated method stub
		this.database = database;
	}

	public String getUser() {
		// TODO Auto-generated method stub
		return this.user;
	}

	public void setUser(String user) {
		// TODO Auto-generated method stub
		this.user = user;
	}

	public String getPassword() {
		// TODO Auto-generated method stub
		return this.password;
	}

	public void setPassword(String password) {
		// TODO Auto-generated method stub
		this.password = password;
	}

	public boolean getPooling() {
		// TODO Auto-generated method stub
		return this.pooling;
	}

	public void setPooling(boolean pooling) {
		// TODO Auto-generated method stub
		this.pooling = pooling;
	}

	public int getMinSize() {
		// TODO Auto-generated method stub
		return this.minSize;
	}

	public void setMinSize(int minSize) {
		// TODO Auto-generated method stub
		this.minSize = minSize;
	}

	public int getMaxSize() {
		// TODO Auto-generated method stub
		return this.maxSize;
	}

	public void setMaxSize(int maxSize) {
		// TODO Auto-generated method stub
		this.maxSize = maxSize;
	}

	public int getTimeout() {
		// TODO Auto-generated method stub
		return this.timeout;
	}

	public void setTimeout(int timeout) {
		// TODO Auto-generated method stub
		this.timeout = timeout;
	}

	public String getCharset() {
		// TODO Auto-generated method stub
		return this.charset;
	}

	public void setCharset(String charset) {
		// TODO Auto-generated method stub
		this.charset = charset;
	}

	public boolean getTransactional() {
		// TODO Auto-generated method stub
		return this.transactional;
	}

	public void setTransactional(boolean transactional) {
		// TODO Auto-generated method stub
		this.transactional = transactional;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getTransactionLevel() {
		return this.transactionLevel;
	}

	public void setTransactionLevel(int level) {
		this.transactionLevel = level;
	}

	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return this.port;
	}

	@Override
	public void setPort(int port) {
		// TODO Auto-generated method stub
		this.port = port;
	}
	
	public void setOptions(String options){
		this.options = options;
	}
	
	public String getOptions(){
		return this.options;
	}

}
