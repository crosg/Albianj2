package org.albianj.persistence.impl.routing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.impl.object.DataRouterAttribute;
import org.albianj.persistence.object.DataRoutersAttribute;
import org.albianj.persistence.object.IAlbianObjectDataRouter;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IDataRoutersAttribute;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;
import org.dom4j.Node;

public class AlbianDataRouterParserService extends FreeAlbianDataRouterParserService {

	public static final String DEFAULT_ROUTING_NAME = "!@#$%Albianj_Default_DataRouter%$#@!";

	protected Map<String, IDataRouterAttribute> parserRoutings(
			@SuppressWarnings("rawtypes") List nodes) {
		for (Object node : nodes) {
			IDataRoutersAttribute routingsAttribute = getRoutingsAttribute((Element) node);
			if (null == routingsAttribute)
				return null;
			addDataRouterAttribute(routingsAttribute.getInterface(),
					routingsAttribute);
		}
		return null;
	}

	private static IDataRoutersAttribute getRoutingsAttribute(Element elt) {
		IDataRoutersAttribute routing = new DataRoutersAttribute();
		String inter = XmlParser.getAttributeValue(elt, "Interface");
		if (Validate.isNullOrEmptyOrAllSpace(inter)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"The albianObject's interface is empty or null.");
			return null;
		}
		routing.setInterface(inter);

		String type = XmlParser.getAttributeValue(elt, "Type");

		if (Validate.isNullOrEmptyOrAllSpace(type)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"The albianObject's type is empty or null.");
			return null;
		}

		routing.setType(type);
		String hashMapping = XmlParser.getAttributeValue(elt, "Router");
		if (Validate.isNullOrEmptyOrAllSpace(hashMapping)) {
			AlbianServiceRouter.getLogger()
			.warn(IAlbianLoggerService.AlbianRunningLoggerName,
					"The albianObject's datarouter is null or empty,then use default router:org.albianj.persistence.impl.object.AlbianObjectDataRouter.");
			hashMapping = "org.albianj.persistence.impl.object.AlbianObjectDataRouter";
		} 
		
		try {
			Class<?> cls = AlbianClassLoader.getInstance().loadClass(hashMapping);
			routing.setDataRouter((IAlbianObjectDataRouter) cls
					.newInstance());

		} catch (ClassNotFoundException e) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							e, "fail in find class for %s.", type);
			return null;
			
		} catch (InstantiationException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e,
					"init the hash mapping for the %s is error.", type);
			return null;
		} catch (IllegalAccessException e) {
			AlbianServiceRouter.getLogger().error(IAlbianLoggerService.AlbianRunningLoggerName,e,
					"There is no access for %s with init the instance.",
					type);
			return null;
		}

		

		Node writer = elt.selectSingleNode("WriterRouters");
		if (null != writer) {
			String hash = XmlParser.getAttributeValue(writer, "Enable");
			if (!Validate.isNullOrEmptyOrAllSpace(hash)) {
				routing.setWriterRouterEnable(new Boolean(hash));
			}
		}
		Node reader = elt.selectSingleNode("ReaderRouters");
		if (null != reader) {
			String hash = XmlParser.getAttributeValue(reader, "Enable");
			if (!Validate.isNullOrEmptyOrAllSpace(hash)) {
				routing.setReaderRouterEnable(new Boolean(hash));
			}
		}

		List<?> writers = elt.selectNodes("WriterRouters/WriterRouter");
		if (!Validate.isNullOrEmpty(writers)) {
			Map<String, IDataRouterAttribute> map = parserRouting(writers);
			if (null != map)
				routing.setWriterRouters(map);
		}

		List<?> readers = elt.selectNodes("ReaderRouters/ReaderRouter");
		if (!Validate.isNullOrEmpty(readers)) {
			Map<String, IDataRouterAttribute> map = parserRouting(readers);
			if (null != map)
				routing.setReaderRouters(map);
		}
		return routing;
	}

	private static Map<String, IDataRouterAttribute> parserRouting(
			@SuppressWarnings("rawtypes") List nodes) {
		Map<String, IDataRouterAttribute> map = new HashMap<String, IDataRouterAttribute>();
		for (Object node : nodes) {
			IDataRouterAttribute routingAttribute = getroutingAttribute((Element) node);
			if (null == routingAttribute)
				return null;
			map.put(routingAttribute.getName(), routingAttribute);
		}
		return map;
	}

	private static IDataRouterAttribute getroutingAttribute(Element elt) {
		String name = XmlParser.getAttributeValue(elt, "Name");
		if (Validate.isNullOrEmptyOrAllSpace(name)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"this routing attribute is null or empty.");
			return null;
		}

		String storageName = XmlParser.getAttributeValue(elt, "StorageName");
		if (Validate.isNullOrEmptyOrAllSpace(storageName)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"this storage name for the %s routing attribute is null or empty.",
							name);
			return null;
		}
		IDataRouterAttribute routing = new DataRouterAttribute();
		routing.setName(name);
		routing.setStorageName(storageName);
		String tableName = XmlParser.getAttributeValue(elt, "TableName");
		if (!Validate.isNullOrEmptyOrAllSpace(tableName)) {
			routing.setTableName(tableName);
		}
		String enable = XmlParser.getAttributeValue(elt, "Enable");
		if (!Validate.isNullOrEmptyOrAllSpace(enable)) {
			routing.setEnable(new Boolean(enable));
		}
		String owner = XmlParser.getAttributeValue(elt, "Owner");
		if (!Validate.isNullOrEmptyOrAllSpace(owner)) {
			routing.setOwner(owner);
		}

		return routing;

	}

}
