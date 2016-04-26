package org.albianj.cached.impl.service;

import java.util.HashMap;
import java.util.List;

import org.albianj.cached.attribute.IAlbianCachedAttribute;
import org.albianj.cached.service.AlbianCachedAttributeException;
import org.albianj.cached.service.IAlbianCachedService;
import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class FreeAlbianCachedParser extends FreeAlbianParserService {
	
	private String file = "cached.xml";
	private final static String tagName = "CacheServers/CacheServer";
	protected final static String LocalCachedTypeDefault = "org.albianj.cached.impl.service.AlbianLocalCachedAdapter";
	protected final static String RedisCachedTypeDefault = "org.albianj.cached.impl.service.AlbianRedisCachedAdapter";
	protected final static String CachedDefaultName = "Default";
	protected HashMap<String,IAlbianCachedService> services = null;
	
	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	@Override
	public void init() {
		Document doc = null;
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting
					.getAlbianConfigFilePath() + file));
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianCachedAttributeException.class,e,
					"cached-service is error",
					"loading the cached.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianCachedAttributeException.class,
					"cached-service is error",
					"loading the cached.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianCachedAttributeException.class,
					"cached-service is error",
					"parser the node tags:%s in the cached.xml is error. the node of the tags is null or empty.",
					tagName);
		}
		services = new HashMap<String, IAlbianCachedService>();
		parserCacheds(nodes);
		return;
	}

	protected abstract void parserCacheds(
			@SuppressWarnings("rawtypes") List nodes);

	protected abstract IAlbianCachedAttribute parserCached(Element node);

	
}
