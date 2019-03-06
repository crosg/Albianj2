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
package org.albianj.xml;

import org.albianj.verify.Validate;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import java.util.Iterator;
import java.util.List;

public final class XmlParser {
    public static Document load(String path) throws DocumentException {
        if (Validate.isNullOrEmptyOrAllSpace(path)) {
            throw new IllegalArgumentException("path");
        }
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(path);
            // Document doc = reader.read(new File(path));
            return doc;
        } catch (DocumentException e) {
            throw e;
        }
    }

    @Deprecated
    public static List analyze(Document doc, String tagName){
        return selectNodes(doc,tagName);
    }

    @SuppressWarnings("rawtypes")
    public static List selectNodes(Document doc, String tagName) {
        if (null == doc) {
            throw new IllegalArgumentException("doc");
        }

        if (Validate.isNullOrEmptyOrAllSpace(tagName)) {
            throw new IllegalArgumentException("tagName");
        }
        return doc.selectNodes(tagName);
    }

    public static Element toElement(Object node) {
        if (null == node) {
            throw new IllegalArgumentException("node");
        }
        return (Element) node;
    }

    public static Element selectNode(Document doc, String tagName) {
        if (null == doc) {
            throw new IllegalArgumentException("doc");
        }
        if (Validate.isNullOrEmptyOrAllSpace(tagName)) {
            throw new IllegalArgumentException("tagName");
        }
        @SuppressWarnings("rawtypes")
        List nodes = selectNodes(doc, tagName);
        if (null == nodes || 0 == nodes.size())
            return null;
        @SuppressWarnings("rawtypes")
        Iterator it = nodes.iterator();
        return it.hasNext() ? (Element) it.next() : null;
    }

    public static String getAttributeValue(Element elt, String attributeName) {
        if (null == elt) {
            throw new IllegalArgumentException("elt");
        }
        if (Validate.isNullOrEmptyOrAllSpace(attributeName)) {
            throw new IllegalArgumentException("attributeName");
        }
        Attribute attr = elt.attribute(attributeName);
        if (null == attr)
            return null;
        return attr.getValue();
    }

    public static String getAttributeValue(Document doc, String tagName,
                                           String attributeName) {
        if (null == doc)
            throw new IllegalArgumentException("doc");
        if (Validate.isNullOrEmptyOrAllSpace(tagName))
            throw new IllegalArgumentException("tagName");
        if (Validate.isNullOrEmptyOrAllSpace(attributeName))
            throw new IllegalArgumentException("attributeName");
        Element elt = selectNode(doc, tagName);
        if (null == elt) return null;
        return getAttributeValue(elt, attributeName);
    }

    public static boolean hasAttributes(Element elt) {
        if (null == elt)
            throw new IllegalArgumentException("elt");
        return null != elt.attributes() && 0 != elt.attributes().size();
    }

    public static String getNodeValue(Element elt) {
        if (null == elt)
            throw new IllegalArgumentException("elt");
        return elt.getText();
    }

    public static String getNodeValue(Document doc, String tagName) {
        if (null == doc)
            throw new IllegalArgumentException("doc");
        if (Validate.isNullOrEmptyOrAllSpace(tagName))
            throw new IllegalArgumentException("tagName");
        Element ele = selectNode(doc, tagName);
        if (null == ele)
            return null;
        return ele.getText();
    }

    public static String getAttributeValue(Node node, String attributeName) {
        if (null == node)
            throw new IllegalArgumentException("node");
        if (Validate.isNullOrEmptyOrAllSpace(attributeName))
            throw new IllegalArgumentException("attributeName");
        return getAttributeValue((Element) node, attributeName);
    }

    public static String getSingleChildNodeValue(Element elt,
                                                 String childTagName) {
        if (null == elt)
            throw new IllegalArgumentException("elt");
        if (Validate.isNullOrEmptyOrAllSpace(childTagName))
            throw new IllegalArgumentException("childTagName");
        Node chird = elt.selectSingleNode(childTagName);
        if (null == chird)
            return null;
        return chird.getStringValue();
    }

    public static List getChildNodes(Element elt, String childTagName) {
        Validate.notNull(elt, "the element of xml-doc is null.");
        Validate.notBlank(childTagName, "the childTagName of element is blank.");
        List chirds = elt.selectNodes(childTagName);
        return chirds;
    }

    public static Node getChildNode(Element elt, String childTagName) {
        Validate.notNull(elt, "the element of xml-doc is null.");
        Validate.notBlank(childTagName, "the childTagName of element is blank.");
        List chirds = elt.selectNodes(childTagName);
        if(Validate.isNullOrEmpty(chirds)) return null;
        return (Node) chirds.get(0);
    }

    /*
     get xml value by attribute or childNode in the elt Element
     */
    public static String getValueByAttrOrChileNode(Element elt, String attrOrCNodeName){
        Attribute attr =  elt.attribute(attrOrCNodeName);
        if(null != attr){
            return attr.getStringValue();
        }
        Node node =  elt.selectSingleNode(attrOrCNodeName);
        if(null != node){
            return node.getStringValue();
        }
        return null;
    }

    /*
    get xml value by attribute or childNode in the elt Element by nodeTagName
    */
    public static String getValueByAttrOrChileNode(Document doc,String nodeTagName, String attrOrCNodeName){
         Element elt =XmlParser.selectNode(doc,nodeTagName);
         if(null == elt) {
             return null;
         }
        Attribute attr =  elt.attribute(attrOrCNodeName);
        if(null != attr){
            return attr.getStringValue();
        }
        Node node =  elt.selectSingleNode(attrOrCNodeName);
        if(null != node){
            return node.getStringValue();
        }
        return null;
    }


}
