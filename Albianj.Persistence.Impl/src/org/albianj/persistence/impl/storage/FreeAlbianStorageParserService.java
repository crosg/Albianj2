package org.albianj.persistence.impl.storage;

import java.util.HashMap;
import java.util.List;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.object.IRunningStorageAttribute;
import org.albianj.persistence.object.IStorageAttribute;
import org.albianj.persistence.object.PersistenceDatabaseStyle;
import org.albianj.persistence.service.IAlbianStorageParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class FreeAlbianStorageParserService extends FreeAlbianParserService  implements IAlbianStorageParserService
		 {
	private  String file = "storage.xml";
	private final static String tagName = "Storages/Storage";
	
	private HashMap<String,IStorageAttribute> cached = null; 

	public void setConfigFileName(String fileName) {
			this.file = fileName;
	}
	@Override
	public void init() throws AlbianParserException {
		Document doc = null;
		cached = new HashMap<String,IStorageAttribute>();
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting
					.getAlbianConfigFilePath() + file));
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,
					"StorageService is error",
					"loading the storage.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"StorageService is error",
					"loading the storage.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"StorageService is error",
					"parser the node tags:%s in the storage.xml is error. the node of the tags is null or empty.",
					tagName);
		}
		parserStorages(nodes);
		return;
	}

	protected abstract void parserStorages(
			@SuppressWarnings("rawtypes") List nodes)
			throws AlbianParserException;

	protected abstract IStorageAttribute parserStorage(Element node);

//	public static String generateConnectionUrl(IRunningStorageAttribute rsa) {
//		if (null == rsa) {
//			AlbianServiceRouter.getLogger()
//					.warn(IAlbianLoggerService.AlbianRunningLoggerName,
//							"the argument storageName is null or empty.");
//			return null;
//		}
//		return generateConnectionUrl(rsa);
//	}

	public static String generateConnectionUrl(
			IRunningStorageAttribute rsa) {
		if (null == rsa) {
			AlbianServiceRouter.getLogger().warn(IAlbianLoggerService.AlbianRunningLoggerName,
					"The argument storageAttribute is null.");
			return null;
		}

		IStorageAttribute storageAttribute = rsa.getStorageAttribute();
		StringBuilder sb = new StringBuilder();
		sb.append("jdbc:");
		// String url =
		// "jdbc:mysql://localhost/baseinfo?useUnicode=true&characterEncoding=8859_1";
		switch (storageAttribute.getDatabaseStyle()) {
		case (PersistenceDatabaseStyle.Oracle): {
			sb.append("oracle:thin:@").append(storageAttribute.getServer());
			if (0 != storageAttribute.getPort()) {
				sb.append(":").append(storageAttribute.getPort());
			}
			sb.append(":").append(rsa.getDatabase());
		}
		case (PersistenceDatabaseStyle.SqlServer): {
			sb.append("microsoft:sqlserver://").append(
					storageAttribute.getServer());
			if (0 != storageAttribute.getPort()) {
				sb.append(":").append(storageAttribute.getPort());
			}
			sb.append(";").append(rsa.getDatabase());
		}
		case (PersistenceDatabaseStyle.MySql):
		default: {
			sb.append("mysql://").append(storageAttribute.getServer());
			if (0 != storageAttribute.getPort()) {
				sb.append(":").append(storageAttribute.getPort());
			}
			sb.append("/").append(rsa.getDatabase());
			if (null != storageAttribute.getCharset()) {
				sb.append("?useUnicode=true&characterEncoding=").append(
						storageAttribute.getCharset());
			}
			sb.append("&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull");
		}
		}
		return sb.toString();
	}
	
	public void addStorageAttribute(String name,IStorageAttribute sa){
			cached.put(name, sa);
	}
	
	public IStorageAttribute getStorageAttribute(String name){
			return cached.get(name);
	}
}
