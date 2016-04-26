package org.albianj.persistence.impl.storage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.albianj.kernel.AlbianLevel;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.impl.object.StorageAttribute;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.security.IAlbianSecurityService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Element;

public class AlbianStorageParserService extends FreeAlbianStorageParserService {

	public final static String DEFAULT_STORAGE_NAME = "!@#$%Albianj_Default_Storage%$#@!";
	public final static String DRIVER_CLASSNAME = "com.mysql.jdbc.Driver";
	
	private HashMap<String, DataSource> _dataSource = null;

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
	// </Storag
	
	@Override
	public void init() throws AlbianParserException {
		_dataSource = new HashMap<String, DataSource>();
		super.init();
	}

	@Override
	protected void parserStorages(@SuppressWarnings("rawtypes") List nodes)
			throws AlbianParserException {
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
					"Storage node is null or size is 0.");
			return;
		}
		for (int i = 0; i < nodes.size(); i++) {
			IStorageAttribute storage = parserStorage((Element) nodes.get(i));
			if (null == storage) {
				AlbianServiceRouter.getLogger()
				.errorAndThrow(
						IAlbianLoggerService.AlbianRunningLoggerName,
						AlbianParserException.class,
						"StorageService is error",
						"parser storage in the storage.xml is fail.xml:%s.",
						((Element) nodes.get(i)).asXML());
			}
			addStorageAttribute(storage.getName(), storage);
//			StorageAttributeMap.insert(storage.getName(), storage);
			if (i == 0) {
				addStorageAttribute(DEFAULT_STORAGE_NAME, storage);
//				StorageAttributeMap.insert(DEFAULT_STORAGE_NAME, storage);
			}
	//		DataSource ds = setupDataSource(storage);
		//	DataSourceMap.insert(storage.getName(), ds);
		}
	}

	@Override
	protected IStorageAttribute parserStorage(Element node) {
		String name = XmlParser.getSingleChildNodeValue(node, "Name");
		if (null == name) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"There is no name attribute in the storage node.");
			return null;
		}
		String databaseStyle = XmlParser.getSingleChildNodeValue(node,
				"DatabaseStyle");
		String server = XmlParser.getSingleChildNodeValue(node, "Server");
		if (null == server) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"There is no server attribute in the storage node.");
			return null;
		}
		String database = XmlParser.getSingleChildNodeValue(node, "Database");
		if (null == database) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"There is no database attribute in the storage node.");
			return null;
		}
		String user = XmlParser.getSingleChildNodeValue(node, "User");
		if (null == user) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"There is no uid attribute in the storage node.");
			return null;
		}
		String password = XmlParser.getSingleChildNodeValue(node, "Password");
		String pooling = XmlParser.getSingleChildNodeValue(node, "Pooling");
		String minPoolSize = XmlParser.getSingleChildNodeValue(node,
				"MinPoolSize");
		String maxPoolSize = XmlParser.getSingleChildNodeValue(node,
				"MaxPoolSize");
		String timeout = XmlParser.getSingleChildNodeValue(node, "Timeout");
		String charset = XmlParser.getSingleChildNodeValue(node, "Charset");
		String transactional = XmlParser.getSingleChildNodeValue(node,
				"Transactional");
		String transactionLevel = XmlParser.getSingleChildNodeValue(node,
				"TransactionLevel");
		String port = XmlParser.getSingleChildNodeValue(node,
				"Port");
		
		String options =  XmlParser.getSingleChildNodeValue(node,
				"Options");


		IStorageAttribute storage = new StorageAttribute();
		storage.setName(name);
		if (null == databaseStyle) {
			storage.setDatabaseStyle(PersistenceDatabaseStyle.MySql);
		} else {
			String style = databaseStyle.trim().toLowerCase();
			storage.setDatabaseStyle("sqlserver".equalsIgnoreCase(style) ? PersistenceDatabaseStyle.SqlServer
					: "oracle".equalsIgnoreCase(style) ? PersistenceDatabaseStyle.Oracle
							: PersistenceDatabaseStyle.MySql);
		}
		storage.setServer(server);
		storage.setDatabase(database);
		storage.setUser(user);
		storage.setPassword(Validate.isNullOrEmptyOrAllSpace(password) ? ""
				: password);
		storage.setPooling(Validate.isNullOrEmptyOrAllSpace(pooling) ? true
				: new Boolean(pooling));
		storage.setMinSize(Validate.isNullOrEmptyOrAllSpace(minPoolSize) ? 5
				: new Integer(minPoolSize));
		storage.setMaxSize(Validate.isNullOrEmptyOrAllSpace(maxPoolSize) ? 20
				: new Integer(maxPoolSize));
		storage.setTimeout(Validate.isNullOrEmptyOrAllSpace(timeout) ? 30
				: new Integer(timeout));
		storage.setCharset(Validate.isNullOrEmptyOrAllSpace(charset) ? null
				: charset);
		storage.setTransactional(Validate
				.isNullOrEmptyOrAllSpace(transactional) ? true : new Boolean(
				transactional));
		
		storage.setOptions(options);
		
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
		
		if(!Validate.isNullOrEmptyOrAllSpace(port)) {
			storage.setPort(new Integer(port));
		}

		return storage;
	}

	public static DataSource setupDataSource(IRunningStorageAttribute rsa) {
		BasicDataSource ds = null;
		try {
			ds = new BasicDataSource();
		} catch (Exception e) {
			System.err.println(e);
		}
		try {
			IStorageAttribute storageAttribute = rsa.getStorageAttribute();
			String url = FreeAlbianStorageParserService
					.generateConnectionUrl(rsa);
			ds.setDriverClassName(DRIVER_CLASSNAME);
			ds.setUrl(url);

			if (AlbianLevel.Debug == KernelSetting.getAlbianLevel()) {
				ds.setUsername(storageAttribute.getUser());
				ds.setPassword(storageAttribute.getPassword());
			} else {
				IAlbianSecurityService ass = AlbianServiceRouter.getService(IAlbianSecurityService.class, IAlbianSecurityService.Name,false);
				if(null != ass) {
					ds.setUsername(ass.decryptDES(storageAttribute.getUser()));
					ds.setPassword(ass.decryptDES(storageAttribute.getPassword()));
				} else {
					IAlbianLoggerService als =  AlbianServiceRouter.getLogger();
					if(null != als){
						als.warn(IAlbianLoggerService.AlbianSqlLoggerName, 
								"the run level is release in the kernel config but security is null,so not use security service.");
					}
					ds.setUsername(storageAttribute.getUser());
					ds.setPassword(storageAttribute.getPassword());
				}
			}

			if (storageAttribute.getTransactional()) {
				ds.setDefaultAutoCommit(false);
				if (Connection.TRANSACTION_NONE != storageAttribute
						.getTransactionLevel()) {
					ds.setDefaultTransactionIsolation(storageAttribute
							.getTransactionLevel());
				}
			}
			ds.setDefaultReadOnly(false);
			if (storageAttribute.getPooling()) {
				ds.setInitialSize(storageAttribute.getMinSize());
				ds.setMaxIdle(storageAttribute.getMaxSize());
				ds.setMinIdle(storageAttribute.getMinSize());
			} else {
				ds.setInitialSize(5);
				ds.setMaxIdle(10);
				ds.setMinIdle(5);
			}
			ds.setMaxWait(storageAttribute.getTimeout() * 1000);
			ds.setRemoveAbandoned(true);
			ds.setRemoveAbandonedTimeout(storageAttribute.getTimeout());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return ds;
	}

	public synchronized Connection getConnection(IRunningStorageAttribute rsa) {
		IStorageAttribute sa = rsa.getStorageAttribute();
		String key = sa.getName() + rsa.getDatabase();
		DataSource ds = null;
		if(_dataSource.containsKey(key)){
			ds = _dataSource.get(key);
		} else {
			ds = setupDataSource(rsa);
			_dataSource.put(key, ds);
		}
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e,
					"Get the connection with storage:%s and database:%s form connection pool is error.",
					sa.getName(),rsa.getDatabase());
			return null;
		}
	}

}
