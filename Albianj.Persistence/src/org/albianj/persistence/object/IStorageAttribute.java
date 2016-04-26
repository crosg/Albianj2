package org.albianj.persistence.object;

public interface IStorageAttribute {
	public String getName();

	public void setName(String name);

	public int getDatabaseStyle();

	public void setDatabaseStyle(int databaseStyle);

	public String getServer();

	public void setServer(String server);
	
	public int getPort();
	
	public void setPort(int port);

	public String getDatabase();

	public void setDatabase(String database);

	public String getUser();

	public void setUser(String user);

	public String getPassword();

	public void setPassword(String password);

	public boolean getPooling();

	public void setPooling(boolean pooling);

	public int getMinSize();

	public void setMinSize(int minSize);

	public int getMaxSize();

	public void setMaxSize(int maxSize);

	public int getTimeout();

	public void setTimeout(int timeout);

	public String getCharset();

	public void setCharset(String charset);

	public boolean getTransactional();

	public void setTransactional(boolean transactional);

	public int getTransactionLevel();

	public void setTransactionLevel(int level);
	
	public void setOptions(String options);
	
	public String getOptions();
}
