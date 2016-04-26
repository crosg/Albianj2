package org.albianj.service;

import java.util.List;
import java.util.Map;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class FreeAlbianServiceParser extends FreeAlbianParserService {

	private String file = "service.xml";
	private final static String tagName = "Services/Service";

	public final static String ALBIANJSERVICEKEY = "@$#&ALBIANJ_ALL_SERVICE&#$@";

	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	public void init() throws AlbianParserException {

		Document doc = null;
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting.getAlbianConfigFilePath() + file));
		} catch (Exception e) {

			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianParserException.class, e,
					"Kernel is error", "loading the service.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianParserException.class,
					"Kernel is error", "loading the service.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianParserException.class,
					"Kernel is error",
					"parser the node tags:%s in the service.xml is error. the node of the tags is null or empty.",
					tagName);
		}
		Map<String, IAlbianServiceAttribute> map = parserServices(tagName, nodes);
		if (null == map) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName, "The albian services is empty.");
			return;
		}
		ServiceAttributeMap.insert(ALBIANJSERVICEKEY, map);
		return;
	}

	protected abstract Map<String, IAlbianServiceAttribute> parserServices(String tarName,
			@SuppressWarnings("rawtypes") List nodes) throws NullPointerException, AlbianServiceException;

	protected abstract IAlbianServiceAttribute parserService(String name, Element node)
			throws NullPointerException, AlbianServiceException;
}
