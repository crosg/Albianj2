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
package org.albianj.service.impl;

import org.albianj.aop.IAlbianServiceAopAttribute;
import org.albianj.aop.impl.AlbianServiceAopAttribute;
import org.albianj.loader.AlbianClassLoader;
import org.albianj.logger.AlbianLoggerLevel;
import org.albianj.logger.IAlbianLoggerService2;
import org.albianj.runtime.AlbianModuleType;
import org.albianj.service.*;
import org.albianj.service.parser.IAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AlbianServiceRant(Id = AlbianBuiltinServiceNamePair.AlbianServiceParserName, Interface = IAlbianParserService.class)
public class AlbianServiceParser extends FreeAlbianServiceParser {

    private final static String ID_ATTRBUITE_NAME = "Id";
    private final static String TYPE_ATTRBUITE_NAME = "Type";

    public String getServiceName() {
        return AlbianBuiltinServiceNamePair.AlbianServiceParserName;
    }

    @Override
    protected void parserServices(Map<String, IAlbianServiceAttribute> map,
                                  String tagName, @SuppressWarnings("rawtypes") List nodes) {
        if (Validate.isNullOrEmpty(nodes)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName,
                    AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianKernel, "kernel is error.",
                    "parser the nodes for service is null or empty.the tagname:%s", tagName);
        }
        String name = null;
        for (Object node : nodes) {
            Element elt = XmlParser.toElement(node);
            name = null == name ? "tagName" : name;
            IAlbianServiceAttribute serviceAttr = parserService(name, elt);
            if (null == serviceAttr) {
                AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                        IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                        null, AlbianModuleType.AlbianKernel, "Kernel is error.",
                        "parser the node for service is null or empty.the tagname:%s,xml:%s.", tagName, elt.asXML());
            }
            name = serviceAttr.getId();
            map.put(name, serviceAttr);
        }
        return;
    }

    @Override
    protected IAlbianServiceAttribute parserService(String name, Element elt) {
        if (null == elt) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, null,
                    AlbianModuleType.AlbianKernel, "Kernel is error.",
                    "parser the node for service is null or empty.the service id:%s", name);
        }
        IAlbianServiceAttribute serviceAttr = new AlbianServiceAttribute();
        String id = XmlParser.getAttributeValue(elt, ID_ATTRBUITE_NAME);
        if (Validate.isNullOrEmptyOrAllSpace(id)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "parser the node is fail.the node is is null or empty,the node next id:%s.",
                    name);
            return null;
        }
        serviceAttr.setId(id);
        String type = XmlParser.getAttributeValue(elt, TYPE_ATTRBUITE_NAME);
        if (Validate.isNullOrEmptyOrAllSpace(type)) {
            AlbianServiceRouter.getLogger2().log(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    "The type for service:%s is null or empty.", serviceAttr.getId());
            return null;
        }
        serviceAttr.setType(type);

        Class clzz = null;
        try {
            clzz = AlbianClassLoader.getInstance().loadClass(type);

        } catch (Exception e) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, e,
                    AlbianModuleType.AlbianKernel, "Kernel is error.",
                    "service -> %s type -> %s is not load.",
                    id, type);
        }

        serviceAttr.setServiceClass(clzz);

        String sitf = XmlParser.getAttributeValue(elt, "Interface");
        if (!Validate.isNullOrEmptyOrAllSpace(sitf)) {
            serviceAttr.setInterface(sitf);
        }

        String enable = XmlParser.getAttributeValue(elt, "Enable");
        if (!Validate.isNullOrEmptyOrAllSpace(enable)) {
            serviceAttr.setEnable(Boolean.parseBoolean(enable));
        }

        List nodes = elt.selectNodes("Properties/Property");
        if (!Validate.isNullOrEmpty(nodes)) {
            Map<String, IAlbianServiceFieldAttribute> ps = parserAlbianServiceFieldsAttribute(clzz, id, nodes);
            if (!Validate.isNullOrEmpty(ps)) {
                serviceAttr.setServiceFields(ps);
            }

        }

        List aopNodes = elt.selectNodes("Aop/Aspect");
        if (!Validate.isNullOrEmpty(aopNodes)) {
            Map<String, IAlbianServiceAopAttribute> aas = parserAlbianServiceAopAttribute(id, aopNodes);
            if (!Validate.isNullOrEmpty(aas)) {
                serviceAttr.setAopAttributes(aas);
            }

        }

        return serviceAttr;
    }

    protected Map<String, IAlbianServiceFieldAttribute> parserAlbianServiceFieldsAttribute(Class<?> clzz, String id, List nodes) {
        Map<String, IAlbianServiceFieldAttribute> pas = new HashMap<>();
        for (Object node : nodes) {
            IAlbianServiceFieldAttribute pa = parserAlbianServiceFieldAttribute(clzz, id, (Element) node);
            pas.put(pa.getName(), pa);
        }
        return pas;
    }

    protected IAlbianServiceFieldAttribute parserAlbianServiceFieldAttribute(Class<?> clzz, String id, Element e) {
        String name = XmlParser.getAttributeValue(e, "Name");
        IAlbianServiceFieldAttribute pa = new AlbianServiceFieldAttribute();
        if (Validate.isNullOrEmptyOrAllSpace(name)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    null, AlbianModuleType.AlbianKernel, "Kernel is error.",
                    "the service:%s's name of property is null or empty.",
                    id);
        }
        pa.setName(name);
        String type = XmlParser.getAttributeValue(e, "Type");
        if (Validate.isNullOrEmptyOrAllSpace(type)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error,
                    null, AlbianModuleType.AlbianKernel, "Kernel is error.",
                    "the service:%s's type of property is null or empty.",
                    id);
        }
        pa.setType(type);
        String value = XmlParser.getAttributeValue(e, "Value");
        if (!Validate.isNullOrEmptyOrAllSpace(name)) {
            pa.setValue(value);
        }

        String allowNull = XmlParser.getAttributeValue(e, "AllowNull");
        if (!Validate.isNullOrEmptyOrAllSpace(allowNull)) {
            pa.setAllowNull(Boolean.parseBoolean(allowNull));
        }

        try {
            Field f = clzz.getDeclaredField(name);
            f.setAccessible(true);
            pa.setField(f);
        } catch (NoSuchFieldException exc) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName, AlbianLoggerLevel.Error, exc,
                    AlbianModuleType.AlbianKernel, "Kernel is error.",
                    "service -> %s is not exist field -> %s.",
                    id, name);
        }

        return pa;

    }

    protected Map<String, IAlbianServiceAopAttribute> parserAlbianServiceAopAttribute(String id, List nodes) {
        Map<String, IAlbianServiceAopAttribute> aas = new HashMap<>();
        for (Object node : nodes) {
            IAlbianServiceAopAttribute pa = parserAlbianServiceAopAttribute(id, (Element) node);
            aas.put(pa.getProxyName(), pa);
        }
        return aas;
    }

    protected IAlbianServiceAopAttribute parserAlbianServiceAopAttribute(String id, Element e) {


        IAlbianServiceAopAttribute aa = new AlbianServiceAopAttribute();
        String beginWith = XmlParser.getAttributeValue(e, "BeginWith");
        if (!Validate.isNullOrEmptyOrAllSpace(beginWith)) {
            aa.setBeginWith(beginWith);
        }

        String notBeginWith = XmlParser.getAttributeValue(e, "NotBeginWith");
        if (!Validate.isNullOrEmptyOrAllSpace(notBeginWith)) {
            aa.setNotBeginWith(notBeginWith);
        }

        String endWith = XmlParser.getAttributeValue(e, "EndWith");
        if (!Validate.isNullOrEmptyOrAllSpace(endWith)) {
            aa.setEndWith(endWith);
        }

        String notEndWith = XmlParser.getAttributeValue(e, "NotEndWith");
        if (!Validate.isNullOrEmptyOrAllSpace(notEndWith)) {
            aa.setNotEndWith(notEndWith);
        }

        String contain = XmlParser.getAttributeValue(e, "Contain");
        if (!Validate.isNullOrEmptyOrAllSpace(contain)) {
            aa.setContain(contain);
        }

        String notContain = XmlParser.getAttributeValue(e, "NotContain");
        if (!Validate.isNullOrEmptyOrAllSpace(notContain)) {
            aa.setNotContain(notContain);
        }

        String fullname = XmlParser.getAttributeValue(e, "FullName");
        if (!Validate.isNullOrEmptyOrAllSpace(fullname)) {
            aa.setFullName(fullname);
        }

        String sIsAll = XmlParser.getAttributeValue(e, "IsAll");
        if (!Validate.isNullOrEmptyOrAllSpace(sIsAll)) {
            aa.setIsAll(Boolean.parseBoolean(sIsAll));
        }

        String proxy = XmlParser.getAttributeValue(e, "Proxy");
        if (Validate.isNullOrEmptyOrAllSpace(proxy)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName,
                    AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianKernel,
                    "Kernel is error.",
                    "the service:%s's proxy of aop is null or empty.",
                    id);
        }
        aa.setServiceName(proxy);


        String proxyName = XmlParser.getAttributeValue(e, "ProxyName");
        if (Validate.isNullOrEmptyOrAllSpace(proxyName)) {
            AlbianServiceRouter.getLogger2().logAndThrow(IAlbianLoggerService2.AlbianRunningLoggerName,
                    IAlbianLoggerService2.InnerThreadName,
                    AlbianLoggerLevel.Error, null, AlbianModuleType.AlbianKernel,
                    "Kernel is error.",
                    "the service:%s's proxyName of aop is null or empty.",
                    id);
        }
        aa.setProxyName(proxyName);


        return aa;

    }

}
