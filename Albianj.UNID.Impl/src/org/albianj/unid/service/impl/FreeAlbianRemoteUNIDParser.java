package org.albianj.unid.service.impl;

import java.util.List;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianService;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.service.parser.IAlbianParser;
import org.albianj.unid.service.AlbianRemoteUNIDAttributeException;
import org.albianj.unid.service.IAlbianRemoteUNIDAttribute;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class FreeAlbianRemoteUNIDParser extends FreeAlbianParserService 
		 {
	private  String file = "unid.xml";
	private final static String tagName = "UNID/Servers/Server";

	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	@Override
	public void init() throws AlbianParserException {
		Document doc = null;
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting
					.getAlbianConfigFilePath() + file));
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,
					"IdService client is error",
					"loading the unid.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"IdService client is error",
					"loading the unid.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"IdService client is error",
					"parser the node tags:%s in the unid.xml is error. the node of the tags is null or empty.",
					tagName);
		}

		try {
			parserServers(nodes);
		} catch (AlbianRemoteUNIDAttributeException e) {
			// TODO Auto-generated catch block
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"IdService client is error",
					"parser the nodes for remoteid server is fail.");
		}

		return;
	}

	protected abstract void parserServers(
			@SuppressWarnings("rawtypes") List nodes)
			throws AlbianRemoteUNIDAttributeException;

	protected abstract IAlbianRemoteUNIDAttribute parserServer(Element node);
}
