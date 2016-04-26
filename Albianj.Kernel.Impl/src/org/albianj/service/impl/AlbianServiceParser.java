package org.albianj.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.albianj.logger.IAlbianLoggerService;
import org.albianj.logger.impl.AlbianLoggerService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.FreeAlbianServiceParser;
import org.albianj.service.IAlbianServiceAttribute;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

public class AlbianServiceParser extends FreeAlbianServiceParser {

	private final static String ID_ATTRBUITE_NAME = "Id";
	private final static String TYPE_ATTRBUITE_NAME = "Type";

	@Override
	protected Map<String, IAlbianServiceAttribute> parserServices(
			String tagName, @SuppressWarnings("rawtypes") List nodes) {
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
					IllegalArgumentException.class, "Kernel is error.", 
					"parser the nodes for service is null or empty.the tagname:%s",tagName);
		}
		Map<String, IAlbianServiceAttribute> map = new LinkedHashMap<String, IAlbianServiceAttribute>(
				nodes.size());
		String name = null;
		for (Object node : nodes) {
			Element elt = XmlParser.toElement(node);
			name = null == name ? "tagName" : name;
			IAlbianServiceAttribute serviceAttr = parserService(name, elt);
			if (null == serviceAttr) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
						NullPointerException.class, "Kernel is error.", 
						"parser the node for service is null or empty.the tagname:%s,xml:%s.",tagName,elt.asXML());
			}
			name = serviceAttr.getId();
			map.put(name, serviceAttr);
		}
		return 0 == map.size() ? null : map;
	}

	@Override
	protected IAlbianServiceAttribute parserService(String name, Element elt) {
		if (null == elt) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
					IllegalArgumentException.class, "Kernel is error.", 
					"parser the node for service is null or empty.the service id:%s",name);
			
		}
		IAlbianServiceAttribute serviceAttr = new AlbianServiceAttribute();
		String id = XmlParser.getAttributeValue(elt, ID_ATTRBUITE_NAME);
		if (Validate.isNullOrEmptyOrAllSpace(id)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,"parser the node is fail.the node is is null or empty,the node next id:%1$s.",
							name);
			return null;
		}
		serviceAttr.setId(id);
		String type = XmlParser.getAttributeValue(elt, TYPE_ATTRBUITE_NAME);
		if (Validate.isNullOrEmptyOrAllSpace(type)) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,
					"The type for service:%s is null or empty.", serviceAttr.getId());
			return null;
		}
		serviceAttr.setType(type);
		return serviceAttr;
	}

}
