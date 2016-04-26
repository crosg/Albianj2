package org.albianj.persistence.impl.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;


public abstract class FreeAlbianDataRouterParserService extends FreeAlbianParserService implements IAlbianDataRouterParserService
		 {
	private String file = "drouter.xml";
	private final static String tagName = "AlbianObjects/AlbianObject";
	private HashMap<String,IDataRoutersAttribute> _cached = null;

	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	@Override
	public void init() throws AlbianParserException {
		Document doc = null;
		_cached = new HashMap<>();
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting
					.getAlbianConfigFilePath() + file));
		} catch (Exception e) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,
					"DataRouterService is error",
					"loading the drouter.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"DataRouterService is error",
					"loading the drouter.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"DataRouterService is error",
					"parser the node tags:%s in the drouter.xml is error. the node of the tags is null or empty.",
					tagName);
		}
		parserRoutings(nodes);
		return;

	}

	protected abstract Map<String, IDataRouterAttribute> parserRoutings(
			@SuppressWarnings("rawtypes") List nodes);
	
	
	public void addDataRouterAttribute(String name,IDataRoutersAttribute dra){
		_cached.put(name, dra);
	}
	
	public IDataRoutersAttribute getDataRouterAttribute(String name){
		return _cached.get(name);
	}
	
}
