package org.albianj.persistence.impl.mapping;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.impl.object.AlbianObjectAttribute;
import org.albianj.persistence.impl.object.CacheAttribute;
import org.albianj.persistence.impl.object.DataRouterAttribute;
import org.albianj.persistence.impl.object.MemberAttribute;
import org.albianj.persistence.impl.routing.AlbianDataRouterParserService;
import org.albianj.persistence.impl.storage.AlbianStorageParserService;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.object.AlbianObjectMemberAttribute;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.object.ICacheAttribute;
import org.albianj.persistence.object.IDataRouterAttribute;
import org.albianj.persistence.object.IMemberAttribute;
import org.albianj.persistence.service.MappingAttributeException;
import org.albianj.reflection.AlbianReflect;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;
import org.dom4j.Node;

public class AlbianMappingParserService extends FreeAlbianMappingParserService {

	private static final String cacheTagName = "Cache";
	private static final String memberTagName = "Members/Member";

	
	@Override
	protected void parserAlbianObjects(@SuppressWarnings("rawtypes") List nodes) throws AlbianParserException{
		if (Validate.isNullOrEmpty(nodes)) {
			throw new IllegalArgumentException("nodes");
		}
		String inter = null;
		for (Object node : nodes) {
			IAlbianObjectAttribute albianObjectAttribute = null;
			Element ele = (Element) node;
			try {
				albianObjectAttribute = parserAlbianObject(ele);
			} catch (Exception e) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianParserException.class, e, 
						"PersistenService is error.", "parser persisten node is fail,xml:%s", ele.asXML());
			}
			if (null == albianObjectAttribute) {
				AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, AlbianParserException.class,  
						"PersistenService is error.", "parser persisten node is fail,the node attribute is null,xml:%s", ele.asXML());
			}
			inter = albianObjectAttribute.getInterface();
			addAlbianObjectAttribute(inter, albianObjectAttribute);
			//AlbianObjectsMap.insert(inter, albianObjectAttribute);
		}

	}

	@Override
	protected IAlbianObjectAttribute parserAlbianObject(Element node) throws AlbianParserException{
		String type = XmlParser.getAttributeValue(node, "Type");
		if (Validate.isNullOrEmptyOrAllSpace(type)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"The albianObject's type is empty or null.");
			return null;
		}

		String inter = XmlParser.getAttributeValue(node, "Interface");
		if (Validate.isNullOrEmptyOrAllSpace(inter)) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"The albianObject's Interface is empty or null.");
			return null;
		}

//		AlbianObjectInheritMap.insert(type, inter);
		addAlbianObjectClassToInterface(type, inter);
		Map<String, IMemberAttribute> map = reflexAlbianObjectMembers(type);

		Node cachedNode = node.selectSingleNode(cacheTagName);
		ICacheAttribute cached;
		if (null == cachedNode) {
			cached = new CacheAttribute();
			cached.setEnable(false);
			cached.setLifeTime(300);
		} else {
			cached = parserAlbianObjectCache(cachedNode);
		}

		IDataRouterAttribute defaultRouting = new DataRouterAttribute();
		defaultRouting.setName(AlbianDataRouterParserService.DEFAULT_ROUTING_NAME);
		defaultRouting.setOwner("dbo");
		defaultRouting
				.setStorageName(AlbianStorageParserService.DEFAULT_STORAGE_NAME);
		String csn = null;
		try {
			csn = AlbianReflect.getClassSimpleName(AlbianClassLoader.getInstance(), type);
		} catch (ClassNotFoundException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,"not found type.", "the type:%s is not found", type);
		}
		if(null != csn) {
			defaultRouting.setTableName(csn);
		}

		@SuppressWarnings("rawtypes")
		List nodes = node.selectNodes(memberTagName);
		if (!Validate.isNullOrEmpty(nodes)) {
			parserAlbianObjectMembers(type, nodes, map);
		}
		IAlbianObjectAttribute albianObjectAttribute = new AlbianObjectAttribute();
		albianObjectAttribute.setCache(cached);
		albianObjectAttribute.setMembers(map);
		albianObjectAttribute.setType(type);
		albianObjectAttribute.setInterface(inter);
		albianObjectAttribute.setDefaultRouting(defaultRouting);
		return albianObjectAttribute;
	}

	private static ICacheAttribute parserAlbianObjectCache(Node node) {
		String enable = XmlParser.getAttributeValue(node, "Enable");
		String lifeTime = XmlParser.getAttributeValue(node, "LifeTime");
		String name = XmlParser.getAttributeValue(node, "Name");
		ICacheAttribute cache = new CacheAttribute();
		cache.setEnable(Validate.isNullOrEmptyOrAllSpace(enable) ? true
				: new Boolean(enable));
		cache.setLifeTime(Validate.isNullOrEmptyOrAllSpace(lifeTime) ? 300
				: new Integer(lifeTime));
		cache.setName(Validate.isNullOrEmptyOrAllSpace(name) ? "Default" : name);
		return cache;
	}

	private static void parserAlbianObjectMembers(String type,
			@SuppressWarnings("rawtypes") List nodes,
			Map<String, IMemberAttribute> map) throws AlbianParserException {
		for (Object node : nodes) {
			parserAlbianObjectMember(type, (Element) node, map);
		}
	}

	private static void parserAlbianObjectMember(String type, Element elt,
			Map<String, IMemberAttribute> map) throws AlbianParserException {
		String name = XmlParser.getAttributeValue(elt, "Name");
		if (Validate.isNullOrEmpty(name)) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
					AlbianParserException.class, 
					"PersistenService is error.", 
					"the persisten node name is null or empty.type:%s,node xml:%s.", type,elt.asXML());
		}
		IMemberAttribute member = (IMemberAttribute) map
				.get(name.toLowerCase());
		if (null == member) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName, 
					AlbianParserException.class, 
					"PersistenService is error.", 
					"the field: %1$s is not found in the %2$s.", name, type);
		}

		String fieldName = XmlParser.getAttributeValue(elt, "FieldName");
		String allowNull = XmlParser.getAttributeValue(elt, "AllowNull");
		String length = XmlParser.getAttributeValue(elt, "Length");
		String primaryKey = XmlParser.getAttributeValue(elt, "PrimaryKey");
		String dbType = XmlParser.getAttributeValue(elt, "DbType");
		String isSave = XmlParser.getAttributeValue(elt, "IsSave");
		if (!Validate.isNullOrEmpty(fieldName)) {
			member.setSqlFieldName(fieldName);
		}
		if (!Validate.isNullOrEmpty(allowNull)) {
			member.setAllowNull(new Boolean(allowNull));
		}
		if (!Validate.isNullOrEmpty(length)) {
			member.setLength(new Integer(length));
		}
		if (!Validate.isNullOrEmpty(primaryKey)) {
			member.setPrimaryKey(new Boolean(primaryKey));
		}
		if (!Validate.isNullOrEmpty(dbType)) {
			member.setDatabaseType(Convert.toSqlType(dbType));
		}
		if (!Validate.isNullOrEmpty(isSave)) {
			member.setIsSave(new Boolean(isSave));
		}
	}

	private Map<String, IMemberAttribute> reflexAlbianObjectMembers (
			String type) throws AlbianParserException {
		Map<String, IMemberAttribute> map = new LinkedHashMap<String, IMemberAttribute>();
		PropertyDescriptor[] propertyDesc = null;
		try {
			propertyDesc = AlbianReflect.getBeanPropertyDescriptor(AlbianClassLoader.getInstance(),type);
		} catch (ClassNotFoundException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,"not found type.", "the type:%s is not found", type);
		} catch (IntrospectionException e) {
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,"not found type.", "the type:%s is not found", type);
		}
		if(null == propertyDesc)
			AlbianServiceRouter.getLogger().errorAndThrow(IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,"not found type.", "the type:%s is not found", type);
		addAlbianObjectPropertyDescriptor(type, propertyDesc);
		for (PropertyDescriptor p : propertyDesc) {
			IMemberAttribute member = reflexAlbianObjectMember(type, p);
			if(null == member){
				throw new MappingAttributeException(
						String.format("reflx albianobject:%s is fail.",type));
			}
			map.put(member.getName().toLowerCase(), member);
		}
		return map;
	}

	private static IMemberAttribute reflexAlbianObjectMember(String type,
			PropertyDescriptor propertyDescriptor) {
		Method mr = propertyDescriptor.getReadMethod();
		Method mw = propertyDescriptor.getWriteMethod();
		if (null == mr || null == mw) {
			AlbianServiceRouter.getLogger()
					.error(IAlbianLoggerService.AlbianRunningLoggerName,
							"property:%s of type:%s is not exist readerMethod or writeMethon.",
							propertyDescriptor.getName(), type);
			return null;
		}
		AlbianObjectMemberAttribute attr = null;
		if (mr.isAnnotationPresent(AlbianObjectMemberAttribute.class))
			attr = mr.getAnnotation(AlbianObjectMemberAttribute.class);
		if (mw.isAnnotationPresent(AlbianObjectMemberAttribute.class))
			attr = mw.getAnnotation(AlbianObjectMemberAttribute.class);

		IMemberAttribute member = new MemberAttribute();
		if (null != attr) {
			member.setName(propertyDescriptor.getName());

			if (Validate.isNullOrEmptyOrAllSpace(attr.FieldName())) {
				member.setSqlFieldName(propertyDescriptor.getName());
			} else {
				member.setSqlFieldName(attr.FieldName());
			}
			member.setAllowNull(attr.IsAllowNull());
			if (0 == attr.DbType()) {
				member.setDatabaseType(Convert.toSqlType(propertyDescriptor
						.getPropertyType()));
			} else {
				member.setDatabaseType(attr.DbType());
			}
			member.setIsSave(attr.IsSave());
			member.setLength(attr.Length());
			member.setPrimaryKey(attr.IsPrimaryKey());
			return member;
		}

		if ("isAlbianNew".equals(propertyDescriptor.getName())) {
			member.setIsSave(false);
			member.setName(propertyDescriptor.getName());
			return member;
		}
		member.setAllowNull(true);
		member.setDatabaseType(Convert.toSqlType(propertyDescriptor
				.getPropertyType()));
		member.setSqlFieldName(propertyDescriptor.getName());
		member.setIsSave(true);
		member.setLength(-1);
		member.setPrimaryKey(false);
		member.setName(propertyDescriptor.getName());
		return member;
	}
}
