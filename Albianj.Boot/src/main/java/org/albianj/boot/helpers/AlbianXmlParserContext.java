package org.albianj.boot.helpers;

import org.albianj.boot.logging.AlbianLogServant;
import org.albianj.boot.logging.AlbianLoggerLevel;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AlbianXmlParserContext {

    private Document doc;
    private String filename;
    private String sessionId;

    protected AlbianXmlParserContext(String sessionId,String filename) {
        this.filename = filename;
        this.sessionId = sessionId;
    }

    public AlbianXmlParserContext(){

    }

    public static AlbianXmlParserContext makeXmlParserContext(String sessionId,Class<AlbianXmlParserContext> clzz,String filename){
        AlbianXmlParserContext ctx = null;
        try {
            Constructor constructor = clzz.getConstructor(String.class,String.class);
            constructor.setAccessible(true);
            ctx = (AlbianXmlParserContext) constructor.newInstance(sessionId,filename);
        }catch (Exception e){

        }
        return ctx;
    }

    /**
     * 加载xml文档
     * @return
     */
    public AlbianXmlParserContext load(){
       if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(this.filename)) {
           AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                   this.getClass(),null,"XmlParser Error.", null,
                   "The xml filename is null or empty.");
       }
       if(AlbianFileServant.Instance.isFileOrPathNotExist(this.filename)) {
           AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                   this.getClass(),null,"XmlParser Error.", null,
                   "The xml filename -> {0} is not exist.",this.filename);
       }
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(this.filename);
        } catch (Exception e) {
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.", null,
                    "Load and parser xml filename -> {0} is error.",this.filename);
        }
        this.doc = doc;
        return this;
    }

    /**
     * 在文档中根据指定的名字选择节点集合
     * @param tagName
     * @return
     */
    public List<Node> selectNodes(String tagName,boolean throwIfNotExist) {
        if (AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(tagName)) {
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.", null,
                    "Argument tagName -> {0} for parsering conf -> {1} is null or empty.",tagName,this.filename);
        }
        NodeList nodes = doc.getElementsByTagName(tagName);
        if(null == nodes || 0 == nodes.getLength()) {
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(),null,"XmlParser Error.", null,
                        "Nodes by tagName -> {0} not in XmlDoc file -> {1}.", tagName,this.filename);

            }
            return null;
        }
        List<Node> list = new ArrayList<>();
        Node son = null;
        for(int i = 0;i < nodes.getLength();i++){
            son = nodes.item(i);
            list.add(son);
        }
        return list;
    }

    /**
     * 在文档中根据指定的名字选择节点
     * @param tagName
     * @return
     */
    public Node selectNode( String tagName,boolean throwIfNotExist) {
        List<Node> nodes = selectNodes(tagName,false);
        if(null == nodes){
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(),null,"XmlParser Error.", null,
                        "Nodes by tagName -> {0} not in file -> {1}.", tagName,this.filename);

            }
            return null;
        }
        if(2 <= nodes.size()) {
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.", null,
                    "TagName -> {0} node count ->{1} in file -> {2} is not single.", tagName,nodes.size(),this.filename);
        }
        return nodes.get(0);
    }

    /**
     * 从节点中根据指定的名字选定子节点集合
     * @param parent
     * @param tagName
     * @return
     */
    public List<Node> selectNodes(Node parent, String tagName,boolean throwIfNotExist){
        if(!parent.hasChildNodes()){
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(),null,"XmlParser Error.",null,
                        "Nodes by tagName -> {0} not inside parentNode -> {1} (By the way parentNode has no one childNode.) in file -> {2}.",
                                tagName,parent.getNodeName(),this.filename);

            }
            return null;
        }

        NodeList nodes = parent.getChildNodes();
        List<Node> list = new ArrayList<>();
        Node son = null;
        for(int i = 0;i < nodes.getLength();i++){
            son = nodes.item(i);
            if(son.getNodeName().equalsIgnoreCase(tagName)) {
                list.add(son);
            }
        }
       if(0 != list.size()) {
           return list;
       }
        if(throwIfNotExist){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.",null,
                    "Nodes by tagName -> {0} not inside parentNode -> {1} in file -> {2}.",
                            tagName,parent.getNodeName(),this.filename);

        }
        return null;
    }

    /**
     * 从节点中根据指定的名字选定单独子节点
     * @param parent
     * @param tagName
     * @return
     */
    public Node selectNode(Node parent, String tagName,boolean throwIfNotExist){
        List<Node> nodes = selectNodes(parent,tagName,false);
        if(null == nodes){
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(),null,"XmlParser Error.",null,
                        "Nodes by tagName -> {0} not inside parentNode -> {1} in file -> {2}.",
                                tagName,parent.getNodeName(),this.filename);

            }
            return null;
        }
        if(2 <= nodes.size()) {
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.",null,
                    "TagName -> {0} node count ->{1} is not single.",tagName,nodes.size());
        }
        return nodes.get(0);
    }

    /**
     * 得到xml节点的属性或者是子节点的值
     * 节点分大小写
     * 如果没有属性或者没有节点，可以设置是否抛出异常
     * @param node
     * @param attrOrCNodeName
     * @param throwIfNotExist
     * @return
     */
    public String findValueByAttrOrChildNode(Node node, String attrOrCNodeName,boolean allowValueNullOrEmpty,boolean throwIfNotExist){
        String val = findAttributeValue(node,attrOrCNodeName,allowValueNullOrEmpty,false);
        if(null == val) {
            val = findChildNodeValue(node,attrOrCNodeName,allowValueNullOrEmpty,false);
        }

        if(null == val) {
            if (throwIfNotExist) {
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(), null, "XmlParser Error.",null,
                        "Attribute or childNode -> {0} not in parent -> {1}.",
                                attrOrCNodeName, node.getNodeName());

            }
        }
        return val;
    }

    /**
     * 得到xml节点的属性或者是子节点的值
     * 节点分大小写
     * 没有节点返回默认值
     * @param node
     * @param attrOrCNodeName
     * @param defer
     * @return
     */
    public String findValueByAttrOrChildNode(Node node, String attrOrCNodeName,String defer){
        String val =  findValueByAttrOrChildNode(node,attrOrCNodeName,true,false);
        return AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) ? defer : val;
    }

    public String findAttributeValue(Node node, String attrName,boolean allowValueNullOrEmpty,boolean throwIfNotExist){
        if(!node.hasAttributes() && throwIfNotExist){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.",null,
                    "Node -> {0} has not any attribute in file -> {1}。So attribute -> {2} is not exist.",
                            node.getNodeName(),this.filename,attrName);
        }

        Node son = null;
        NamedNodeMap nodeMap =  node.getAttributes();
        son = nodeMap.getNamedItem(attrName);
        if(null == son){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(),null,"XmlParser Error.",null,
                    "Attribute -> {0} not in Node -> {1} in file -> {3}.",
                            attrName,node.getNodeName(),this.filename);
        }
        String val = son.getNodeValue();
        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) && !allowValueNullOrEmpty){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(), null, "XmlParser Error.",null,
                    "Attribute -> {0} value is null or empry in Node -> {1} in file -> {3}.",
                            attrName,node.getNodeName(),this.filename);
        }
        return val;
    }
    public String findAttributeValue(Node node, String attrName,String defer) {
        String val =  findAttributeValue(node,attrName,true,false);
        return AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) ? defer : val;
    }

    public String findChildNodeValue(Node parent,String childNodeName,boolean allowValueNullOrEmpty,boolean throwIfNotExist){
        if(!parent.hasChildNodes()) {
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(), null, "XmlParser Error.",null,
                        "Parent node -> {0} has no one child node in the file -> {1}.",
                                parent.getNodeName(),this.filename);
            }
            return null;
        }

        Node node = selectNode(parent,childNodeName,false);
        if(null == node) {
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(), null, "XmlParser Error.",null,
                        "Parent node -> {0} has no one child node named -> {1} in the file -> {2}.",
                                parent.getNodeName(), childNodeName,this.filename);
            }
            return null;
        }

        String val =  node.getNodeValue();
        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) && !allowValueNullOrEmpty){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(), null, "XmlParser Error.",null,
                    "Node -> {0} inside in parent node -> {1} in the file -> {2} that value is null or empty,but not allow.",
                            childNodeName,parent.getNodeName(),this.filename);
        }
        return val;

    }
    public String findChildNodeValue(Node parent,String childNodeName,String defer){
        String val = findChildNodeValue(parent,childNodeName,true,false);
        return AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) ? defer : val;
    }

    public  String findNodeValue(Node node,boolean allowValueNullOrEmpty, boolean throwIfNotExist){
        String val =  node.getNodeValue();
        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) && !allowValueNullOrEmpty){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(), null, "XmlParser Error.",null,
                    "Node -> {0}  in the file -> {2} that value is null or empty,but not allow.",
                            node.getNodeName(),this.filename);
        }
        return val;
    }
    public String findNodeValue(Node node,String defer) {
        String val =  node.getNodeValue();
        return AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) ? defer : val;
    }

    public  String findNodeValue(String nodeName,boolean allowValueNullOrEmpty, boolean throwIfNotExist){
        Node node = selectNode(nodeName,false);
        if(null == node){
            if(throwIfNotExist){
                AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                        this.getClass(),null,"XmlParser Error.",null,
                        "Nodes by tagName -> {0} not in file -> {1}.",
                                nodeName,this.filename);

            }
            return null;
        }
        String val =  node.getNodeValue();
        if(AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) && !allowValueNullOrEmpty){
            AlbianLogServant.Instance.addRuntimeLogAndThrow(sessionId, AlbianLoggerLevel.Error,
                    this.getClass(), null, "XmlParser Error.",null,
                    "Node -> {0}  in the file -> {2} that value is null or empty,but not allow.",
                            node.getNodeName(),this.filename);
        }
        return val;
    }
    public String findNodeValue(String nodeName,String defer) {
        String val = findNodeValue(nodeName,true,false);
        return AlbianStringServant.Instance.isNullOrEmptyOrAllSpace(val) ? defer : val;
    }
}
