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
package org.albianj.persistence.impl.routing;

import org.albianj.boot.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.persistence.impl.object.AlbianObjectAttribute;
import org.albianj.persistence.impl.object.DataRouterAttribute;
import org.albianj.persistence.object.*;
import org.albianj.persistence.service.AlbianEntityMetadata;
import org.albianj.persistence.service.IAlbianDataRouterParserService;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.AlbianBuiltinNames;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;
import org.dom4j.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AlbianServiceRant(Id = IAlbianDataRouterParserService.Name, Interface = IAlbianDataRouterParserService.class)
public class AlbianDataRouterParserService extends FreeAlbianDataRouterParserService {

    public static final String DEFAULT_ROUTING_NAME = "!AlbianDataRouterDefault";

    private void parserRoutingsAttribute(Element elt) throws AlbianParserException {
        String inter = XmlParser.getAttributeValue(elt, "Interface");
        if (Validate.isNullOrEmptyOrAllSpace(inter)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "The albianObject's interface is empty or null.");
            return ;
        }
        String type = XmlParser.getAttributeValue(elt, "Type");

        if (Validate.isNullOrEmptyOrAllSpace(type)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "The albianObject's type is empty or null.");

            return ;
        }
        AlbianEntityMetadata entityMetadata = this.getBundleContext().getModuleConfAndNewIfNotExist(AlbianBuiltinNames.Conf.Persistence,AlbianEntityMetadata.class);
        IAlbianObjectAttribute objAttr = entityMetadata.getEntityMetadata(inter);
        if (null == objAttr) {
            objAttr = new AlbianObjectAttribute();
            objAttr.setType(type);
            objAttr.setInterface(inter);
            entityMetadata.put(inter, objAttr);
        }
        IDataRoutersAttribute routing = objAttr.getDataRouters();
        if (null == routing) {
            routing = new DataRoutersAttribute();
            objAttr.setDataRouters(routing);
        }

        try {
            Class<?> cls = AlbianClassLoader.getInstance().loadClass(type);
            Class<?> itf = AlbianClassLoader.getInstance().loadClass(inter);
            if (!itf.isAssignableFrom(cls)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                        AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object class:%s is not implements from interface:%s.", type, inter);

            }

            if (!IAlbianObject.class.isAssignableFrom(cls)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                        AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object class:%s is not implements from interface: IAlbianObject.", type);

            }

            if (!IAlbianObject.class.isAssignableFrom(itf)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                        AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the albian-object interface:%s is not implements from interface: IAlbianObject.", inter);
            }

        } catch (ClassNotFoundException e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    AlbianModuleType.AlbianPersistence,
                    AlbianModuleType.AlbianPersistence.getThrowInfo(),
                    "the type:%s is not found", type);
        }

        String hashMapping = XmlParser.getAttributeValue(elt, "Router");
        if (Validate.isNullOrEmptyOrAllSpace(hashMapping)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Warn,
                    "The albianObject's datarouter is null or empty,then use default router:org.albianj.persistence.impl.object.AlbianObjectDataRouter.");
            hashMapping = "org.albianj.persistence.impl.object.AlbianObjectDataRouter";
        }

        try {
            Class<?> cls = AlbianClassLoader.getInstance().loadClass(hashMapping);
            if (!IAlbianObjectDataRouter.class.isAssignableFrom(cls)) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                        AlbianModuleType.AlbianPersistence,
                        AlbianModuleType.AlbianPersistence.getThrowInfo(),
                        "the datarouter class:%s is not implements from IAlbianObjectDataRouter.", type);
            }

            routing.setDataRouter((IAlbianObjectDataRouter) cls
                    .newInstance());

        } catch (ClassNotFoundException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    "fail in find class for %s.", type);
            return ;

        } catch (InstantiationException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    "init the hash mapping for the %s is error.", type);
            return ;
        } catch (IllegalAccessException e) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    "There is no access for %s with init the instance.",
                    type);
            return ;
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
            Map<String, IDataRouterAttribute> cfgWRouters = parserRouting(writers);
            if (null != cfgWRouters) {
                if (null == routing.getWriterRouters()) {
                    routing.setWriterRouters(cfgWRouters);
                } else {
                    Map<String, IDataRouterAttribute> pkgWRouters = routing.getWriterRouters();
                    pkgWRouters.putAll(cfgWRouters);
                    routing.setWriterRouters(pkgWRouters);
                }
            }

        }

        List<?> readers = elt.selectNodes("ReaderRouters/ReaderRouter");
        if (!Validate.isNullOrEmpty(readers)) {
            Map<String, IDataRouterAttribute> cfgRRouters = parserRouting(readers);
            if (null != cfgRRouters) {
                if (null == routing.getReaderRouters()) {
                    routing.setReaderRouters(cfgRRouters);
                } else {
                    Map<String, IDataRouterAttribute> pkgRRouters = routing.getReaderRouters();
                    pkgRRouters.putAll(cfgRRouters);
                    routing.setReaderRouters(pkgRRouters);
                }
            }
        }
        return ;
    }

    private Map<String, IDataRouterAttribute> parserRouting(
            @SuppressWarnings("rawtypes") List nodes) {
        Map<String, IDataRouterAttribute> map = new HashMap<String, IDataRouterAttribute>();
        for (Object node : nodes) {
            IDataRouterAttribute routingAttribute = getRoutingAttribute((Element) node);
            if (null == routingAttribute)
                return null;
            map.put(routingAttribute.getName(), routingAttribute);
        }
        return map;
    }

    private IDataRouterAttribute getRoutingAttribute(Element elt) {
        String name = XmlParser.getAttributeValue(elt, "Name");
        if (Validate.isNullOrEmptyOrAllSpace(name)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "this routing attribute is null or empty.");
            return null;
        }

        String storageName = XmlParser.getAttributeValue(elt, "StorageName");
        if (Validate.isNullOrEmptyOrAllSpace(storageName)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
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

    public String getServiceName() {
        return Name;
    }

    protected void parserRoutings(
            @SuppressWarnings("rawtypes") List nodes) throws AlbianParserException {
        for (Object node : nodes) {
             parserRoutingsAttribute((Element) node);
        }
    }

}
