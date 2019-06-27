/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.persistence.impl.mapping;

import org.albianj.boot.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.ILoggerService2;
import org.albianj.persistence.impl.object.AlbianObjectAttribute;
import org.albianj.persistence.impl.object.DataRouterAttribute;
import org.albianj.persistence.impl.object.MemberAttribute;
import org.albianj.persistence.impl.rant.AlbianEntityRantScaner;
import org.albianj.persistence.impl.routing.DataRouterParserService;
import org.albianj.persistence.impl.storage.StorageParserService;
import org.albianj.persistence.impl.toolkit.Convert;
import org.albianj.persistence.object.*;
import org.albianj.persistence.service.AlbianEntityMetadata;
import org.albianj.persistence.service.IMappingParserService;
import org.albianj.persistence.service.MappingAttributeException;
import org.albianj.reflection.AlbianReflect;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.BuiltinNames;
import org.albianj.service.ServiceTag;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ServiceTag(Id = IMappingParserService.Name, Interface = IMappingParserService.class)
public class MappingParserService extends FreeMappingParserService {

    private static final String cacheTagName = "Cache";
    private static final String memberTagName = "Members/Member";

    private static void parserEntityFields(String type, @SuppressWarnings("rawtypes") List nodes,
                                           Map<String, IAlbianEntityFieldAttribute> map) throws AlbianParserException {
        for (Object node : nodes) {
            parserEntityField(type, (Element) node, map);
        }
    }

//    private static ICacheAttribute parserAlbianObjectCache(Node node) {
//        String enable = XmlParser.getAttributeValue(node, "Enable");
//        String lifeTime = XmlParser.getAttributeValue(node, "LifeTime");
//        String name = XmlParser.getAttributeValue(node, "Name");
//        ICacheAttribute cache = new CacheAttribute();
//        cache.setEnable(Validate.isNullOrEmptyOrAllSpace(enable) ? true : new Boolean(enable));
//        cache.setLifeTime(Validate.isNullOrEmptyOrAllSpace(lifeTime) ? 300 : new Integer(lifeTime));
//        cache.setName(Validate.isNullOrEmptyOrAllSpace(name) ? "Default" : name);
//        return cache;
//    }

    private static void parserEntityField(String type, Element elt, Map<String, IAlbianEntityFieldAttribute> map)
            throws AlbianParserException {
        String name = XmlParser.getAttributeValue(elt, "Name");
        if (Validate.isNullOrEmpty(name)) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the persisten node name is null or empty.type:%s,node xml:%s.", type, elt.asXML());
        }
        IAlbianEntityFieldAttribute fieldAttr = map.get(name.toLowerCase());
//        IMemberAttribute member = (IMemberAttribute) map.get(name.toLowerCase());
        if (null == fieldAttr) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the field: %s is not found in the %s.", name, type);
        }

        String fieldName = XmlParser.getAttributeValue(elt, "FieldName");
        String allowNull = XmlParser.getAttributeValue(elt, "AllowNull");
        String length = XmlParser.getAttributeValue(elt, "Length");
        String primaryKey = XmlParser.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlParser.getAttributeValue(elt, "DbType");
        String isSave = XmlParser.getAttributeValue(elt, "IsSave");

        if (!Validate.isNullOrEmpty(fieldName)) {
            fieldAttr.setSqlFieldName(fieldName);
        }
        if (!Validate.isNullOrEmpty(allowNull)) {
            fieldAttr.setAllowNull(new Boolean(allowNull));
        }
        if (!Validate.isNullOrEmpty(length)) {
            fieldAttr.setLength(new Integer(length));
        }
        if (!Validate.isNullOrEmpty(primaryKey)) {
            fieldAttr.setPrimaryKey(new Boolean(primaryKey));
        }
        if (!Validate.isNullOrEmpty(dbType)) {
            fieldAttr.setDatabaseType(Convert.toSqlType(dbType));
        }
        if (!Validate.isNullOrEmpty(isSave)) {
            fieldAttr.setIsSave(new Boolean(isSave));
        }
//        if(Validate.isNullOrEmptyOrAllSpace(varField)){
//            member.setVarField(StringHelper.lowercasingFirstLetter(name));
//        } else {
//            member.setVarField(varField);
//        }
    }

    private static void parserAlbianObjectMembers(String type, @SuppressWarnings("rawtypes") List nodes,
                                                  Map<String, IMemberAttribute> map) throws AlbianParserException {
        for (Object node : nodes) {
            parserAlbianObjectMember(type, (Element) node, map);
        }
    }

    private static void parserAlbianObjectMember(String type, Element elt, Map<String, IMemberAttribute> map)
            throws AlbianParserException {
        String name = XmlParser.getAttributeValue(elt, "Name");
        if (Validate.isNullOrEmpty(name)) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the persisten node name is null or empty.type:%s,node xml:%s.", type, elt.asXML());
        }
        IMemberAttribute member = (IMemberAttribute) map.get(name.toLowerCase());
        if (null == member) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the field: %s is not found in the %s.", name, type);
        }

        String fieldName = XmlParser.getAttributeValue(elt, "FieldName");
        String allowNull = XmlParser.getAttributeValue(elt, "AllowNull");
        String length = XmlParser.getAttributeValue(elt, "Length");
        String primaryKey = XmlParser.getAttributeValue(elt, "PrimaryKey");
        String dbType = XmlParser.getAttributeValue(elt, "DbType");
        String isSave = XmlParser.getAttributeValue(elt, "IsSave");
        String varField = XmlParser.getAttributeValue(elt, "VarField");
        String autoGenKey = XmlParser.getAttributeValue(elt, "AutoGenKey");
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
        if (Validate.isNullOrEmptyOrAllSpace(varField)) {
            member.setVarField(StringHelper.lowercasingFirstLetter(name));
        } else {
            member.setVarField(varField);
        }
        if (!Validate.isNullOrEmptyOrAllSpace(autoGenKey)) {
            member.setAutoGenKey(new Boolean(autoGenKey));
        }
    }

    private static IMemberAttribute reflexAlbianObjectMember(String type, PropertyDescriptor propertyDescriptor) {
        Method mr = propertyDescriptor.getReadMethod();
        Method mw = propertyDescriptor.getWriteMethod();
        if (null == mr || null == mw) {
            AlbianServiceRouter.getLogger2().log(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "property:%s of type:%s is not exist readerMethod or write Method.",
                    propertyDescriptor.getName(), type);
            return null;
        }
        AlbianObjectMemberAttribute attr = null;
        if (mr.isAnnotationPresent(AlbianObjectMemberAttribute.class))
            attr = mr.getAnnotation(AlbianObjectMemberAttribute.class);
        if (mw.isAnnotationPresent(AlbianObjectMemberAttribute.class))
            attr = mw.getAnnotation(AlbianObjectMemberAttribute.class);

        if (attr.Ignore()) return null;

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
                member.setDatabaseType(Convert.toSqlType(propertyDescriptor.getPropertyType()));
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
        member.setDatabaseType(Convert.toSqlType(propertyDescriptor.getPropertyType()));
        member.setSqlFieldName(propertyDescriptor.getName());
        member.setIsSave(true);
        member.setLength(-1);
        member.setPrimaryKey(false);
        member.setName(propertyDescriptor.getName());
        return member;
    }


    //unuseful

    public String getServiceName() {
        return Name;
    }

    @Override
    protected void parserAlbianObjects(@SuppressWarnings("rawtypes") List nodes) throws AlbianParserException {
        if (Validate.isNullOrEmpty(nodes)) {
            throw new IllegalArgumentException("nodes");
        }
        String inter = null;
        for (Object node : nodes) {
            Element ele = (Element) node;
            try {
                parserAlbianObject(ele);
            } catch (Exception e) {
                AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "parser persisten node is fail,xml:%s", ele.asXML());
            }
        }

    }

    protected void parserAlbianObject(Element node) throws AlbianParserException {
        String type = XmlParser.getAttributeValue(node, "Type");
        if (Validate.isNullOrEmptyOrAllSpace(type)) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "The AlbianObject's type is empty in persistence.xml");
            return;
        }

        String inter = XmlParser.getAttributeValue(node, "Interface");
        if (Validate.isNullOrEmptyOrAllSpace(inter)) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "The AlbianObject's type->%s is empty in persistence.xml", type);
            return;
        }

        IAlbianObjectAttribute pkgEntityAttr = null;
        AlbianEntityMetadata entityMetadata = this.getBundleContext().getModuleConfAndNewIfNotExist(BuiltinNames.Conf.Persistence,AlbianEntityMetadata.class);
        if (entityMetadata.exist(inter)) {
            pkgEntityAttr = entityMetadata.getEntityMetadata(inter);
            pkgEntityAttr.setType(type);
        } else {
            pkgEntityAttr = new AlbianObjectAttribute();
            pkgEntityAttr.setInterface(inter);
            pkgEntityAttr.setType(type);
            entityMetadata.put(inter, pkgEntityAttr);
        }

        Class<?> implClzz = null;
        try {
            implClzz = AlbianClassLoader.getInstance().loadClass(type);
            Class<?> itf = AlbianClassLoader.getInstance().loadClass(inter);
            if (!itf.isAssignableFrom(implClzz)) {
                AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object class:%s is not implements from interface:%s.", type, inter);
            }

            if (!IAlbianObject.class.isAssignableFrom(implClzz)) {
                AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object class:%s is not implements from interface: IAlbianObject.", type);
            }

            if (!IAlbianObject.class.isAssignableFrom(itf)) {
                AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                        ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                        AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object interface:%s is not implements from interface: IAlbianObject.", inter);
            }


        } catch (ClassNotFoundException e1) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                    AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the type:%s is not found", type);
        }

        pkgEntityAttr.setImplClzz(implClzz);

//        addAlbianObjectClassToInterface(type, inter);
//        Map<String, IMemberAttribute> map = reflexAlbianObjectMembers(type);
//        Node cachedNode = node.selectSingleNode(cacheTagName);
//        ICacheAttribute cached;
//        if (null == cachedNode) {
//            cached = new CacheAttribute();
//            cached.setEnable(false);
//            cached.setLifeTime(300);
//        } else {
//            cached = parserAlbianObjectCache(cachedNode);
//        }

        IDataRouterAttribute defaultRouting = new DataRouterAttribute();
        defaultRouting.setName(DataRouterParserService.DEFAULT_ROUTING_NAME);
        defaultRouting.setOwner("dbo");
        defaultRouting.setStorageName(StorageParserService.DEFAULT_STORAGE_NAME);
        String csn = null;
        try {
            csn = AlbianReflect.getClassSimpleName(AlbianClassLoader.getInstance(), type);
        } catch (ClassNotFoundException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the type:%s is not found", type);
        }
        if (null != csn) {
            defaultRouting.setTableName(csn);
        }

//        IAlbianObjectAttribute albianObjectAttribute = new AlbianObjectAttribute();
//        Node tnode = node.selectSingleNode("Transaction");
//        if (null != tnode) {
//            String sCompensating = XmlParser.getAttributeValue(node, "Compensating");
//            if (!Validate.isNullOrEmptyOrAllSpace(sCompensating)) {
//                pkgEntityAttr.setCompensating(new Boolean(sCompensating));
//            }
//        }

        Map<String, IAlbianEntityFieldAttribute> entityFieldAttr = null;
        if (Validate.isNullOrEmpty(pkgEntityAttr.getFields())) {
            entityFieldAttr = AlbianEntityRantScaner.scanFields(implClzz);
            pkgEntityAttr.setFields(entityFieldAttr);
        } else {
            entityFieldAttr = pkgEntityAttr.getFields();
        }
        @SuppressWarnings("rawtypes")
        List nodes = node.selectNodes(memberTagName);
        if (!Validate.isNullOrEmpty(nodes)) {
//            parserAlbianObjectMembers(type, nodes, map);
            parserEntityFields(type, nodes, entityFieldAttr);
        }

//        pkgEntityAttr.setCache(cached);
//        pkgEntityAttr.setMembers(map);
        pkgEntityAttr.setDefaultRouting(defaultRouting);
        return;
    }

    private Map<String, IMemberAttribute> reflexAlbianObjectMembers(String type) throws AlbianParserException {
        Map<String, IMemberAttribute> map = new LinkedHashMap<String, IMemberAttribute>();
        PropertyDescriptor[] propertyDesc = null;
        try {
            propertyDesc = AlbianReflect.getBeanPropertyDescriptor(AlbianClassLoader.getInstance(), type);
        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the type:%s is not found", type);
        }
        if (null == propertyDesc) {
            AlbianServiceRouter.getLogger2().logAndThrow(ILoggerService2.AlbianRunningLoggerName,
                    ILoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                    AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the type:%s is not found", type);
        }
//        addAlbianObjectPropertyDescriptor(type, propertyDesc);
        for (PropertyDescriptor p : propertyDesc) {
            IMemberAttribute member = reflexAlbianObjectMember(type, p);
            if (null == member) {
                throw new MappingAttributeException(String.format("reflx albianobject:%s is fail.", type));
            }
            map.put(member.getVarField(), member);
        }
        return map;
    }

}
