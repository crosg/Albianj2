package org.albianj.config.parser;

import org.albianj.comment.Comments;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.albianj.xml.*;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Config2Object {
    private static Map<String, ConfigClassAttribute> configClasssAttr = new HashMap<>();

    public static ConfigClassAttribute clzz2Attr(Class<?> clzz, boolean isRootNode){
        if(null == clzz) return null;
        ConfigClassAttribute ccAttr = new ConfigClassAttribute();
        if(isRootNode) {
            if (!clzz.isAnnotationPresent(ConfigClass2NodeRant.class)) {
                return null;
            }
            ConfigClass2NodeRant c2nRoot = clzz.getAnnotation(ConfigClass2NodeRant.class);
            if(!c2nRoot.IsRoot()) {
                return null;
            }
            String rootNodeName = c2nRoot.XmlNodeName();
            ccAttr.setXmlNodeName(rootNodeName);
            ccAttr.setRealClass(clzz);
        }
        ccAttr.setRoot(isRootNode);
        Field[] fields = clzz.getDeclaredFields();
        for (Field f : fields){
            // must mark field with ConfigField2NodeRant if it is XmlNode
            ConfigFieldAttribute  cfAttr = new ConfigFieldAttribute();
            if(f.isAnnotationPresent(ConfigField2NodeRant.class)){
                f.setAccessible(true);
                ConfigField2NodeRant f2nRant = f.getAnnotation(ConfigField2NodeRant.class);
                String nodeName = null;
                boolean isNewIfNoConfig = false;
                if(Validate.isNullOrEmptyOrAllSpace(f2nRant.XmlNodeName())) {
                    nodeName = StringHelper.captureName(f.getName());
                } else {
                    nodeName = f2nRant.XmlNodeName();
                }
                cfAttr.setXmlNodeName(nodeName);

                isNewIfNoConfig = f2nRant.NewIfNoXmlNode();
                cfAttr.setMakeDefaultIfNoNode(isNewIfNoConfig);

                Class<?> fieldDefineClass = f.getType();
                // the field is Object or collection
                if(IAlbianXml2ObjectSigning.class.isAssignableFrom(fieldDefineClass)){
                    ConfigClassAttribute defClassAttr = null;
                    if(fieldDefineClass.isAnnotationPresent(ConfigGenericCollection2NodeRant.class)){
                        ConfigGenericCollection2NodeRant gc2nRant = fieldDefineClass.getAnnotation(ConfigGenericCollection2NodeRant.class);
                        Class<?> gClzz = gc2nRant.Clazz();
                        defClassAttr = clzz2Attr(gClzz,false);
                        if(IAlbianXmlListNode.class.isAssignableFrom(fieldDefineClass)){
                            cfAttr.setFieldStyle(ConfigFieldStyle.List);
                        }
                        if(IAlbianXmlPairNode.class.isAssignableFrom(fieldDefineClass)) {
                            cfAttr.setFieldStyle(ConfigFieldStyle.Map);
                        }
                    } else {
                        // field is a object
                        defClassAttr = clzz2Attr(fieldDefineClass,false);
                        cfAttr.setFieldStyle(ConfigFieldStyle.Object);
                    }
                    cfAttr.setField(f);
                    cfAttr.setType(fieldDefineClass);
                    cfAttr.setFieldName(f.getName());
                    cfAttr.setDefineClassAttr(defClassAttr);
                } else {
                    // field is simple type
                    cfAttr.setField(f);
                    cfAttr.setFieldStyle(ConfigFieldStyle.Simple);
                    cfAttr.setType(fieldDefineClass);
                    cfAttr.setFieldName(f.getName());
                }
                if(Validate.isNull(ccAttr.getFieldsAttribute())){
                    ccAttr.setFieldsAttribute(new LinkedHashMap<String, IConfigAttribute>());
                }
                ccAttr.getFieldsAttribute().put(cfAttr.getFieldName(),cfAttr);
            }
        }
        return ccAttr;
    }

    public static <T> T todo(Class<T> clzz,String cfFile) throws Exception {
        ConfigClassAttribute clzzAttr = clzz2Attr(clzz,true);
        T inst = clzz.newInstance();
        String rootNodeName = clzzAttr.getXmlNodeName();
        for(Map.Entry<String, IConfigAttribute> fAttrEntry : clzzAttr.getFieldsAttribute().entrySet()) {
            ConfigFieldAttribute fAttr = (ConfigFieldAttribute) fAttrEntry.getValue();
            Object fValue = mergerFieldAndConfig(fAttr,rootNodeName,null);
            fAttr.getField().set(inst,fValue);
        }
        return inst;
    }

    public static Object mergerFieldAndConfig(IConfigAttribute cAttr,String parentNodeName,Document doc){
        ConfigFieldAttribute fAttr = (ConfigFieldAttribute) cAttr;
        String xmlNodeName = fAttr.getXmlNodeName();
        switch (fAttr.getFieldStyle()){
            case Simple:{
                String sVal = XmlParser.getValueByAttrOrChileNode(doc,parentNodeName,xmlNodeName);
                if(!Validate.isNullOrEmptyOrAllSpace(sVal)){
                    Object val = toFieldValue(sVal,fAttr.getType());
                    return val;
                }
                return null;
            }
            case List: {
                String xmlFullTagName = parentNodeName +"/" + xmlNodeName;
                Element parentNode =  XmlParser.selectNode(doc,xmlFullTagName);
                if(null == parentNode) {
                    return null;
                }
                ConfigClassAttribute ccAttr = (ConfigClassAttribute) fAttr.getDefineClassAttr();
                List childNodes = XmlParser.getChildNodes(parentNode,ccAttr.getXmlNodeName());
                if(Validate.isNullOrEmpty(childNodes)){
                    return null;
                }
                for(Object c :childNodes){
                    Element cElt = (Element) c;

                }
                break;
            }
            case Map: {
                break;
            }
            case Object:{

            }
        }
        return null;
    }

    private static void parseFieldsAttribute(@Comments("实体javabean类") Class<?> cls) {
        if (null == cls) return;

        if (cls.isAnnotationPresent(ConfigGenericCollection2NodeRant.class)) {
            ConfigGenericCollection2NodeRant gv4c = cls.getAnnotation(ConfigGenericCollection2NodeRant.class);
            Class<?> c = gv4c.Clazz();
            parseFieldsAttribute(c);
        }

        String nodeName = null;
        if (cls.isAnnotationPresent(ConfigField2NodeRant.class)) {
            ConfigField2NodeRant f2nRant = cls.getAnnotation(ConfigField2NodeRant.class);
            nodeName = f2nRant.XmlNodeName();
        }
        if (Validate.isNullOrEmptyOrAllSpace(nodeName)) {
            nodeName = cls.getSimpleName();
            nodeName = StringHelper.captureName(nodeName);
        }

        if (configClasssAttr.containsKey(nodeName)) return;

        ConfigClassAttribute classAttr = new ConfigClassAttribute();
//        classAttr.setName(nodeName);
        classAttr.setRealClass(cls);

        Field[] fields = cls.getDeclaredFields();
        Map<String, ConfigFieldAttribute> fieldsAttr = new HashMap();
        for(Field f : fields){
            Class<?> type = f.getType();
            if (IAlbianXml2ObjectSigning.class.isAssignableFrom(type)) {
                parseFieldsAttribute(type);
            }

            f.setAccessible(true);
            String fieldName = f.getName();
            String xmlNodeName = StringHelper.captureName(fieldName);
            boolean isMakeDefaultIfNoNode = false;
            if(f.isAnnotationPresent(ConfigField2NodeRant.class)){
                ConfigField2NodeRant snodeName = f.getAnnotation(ConfigField2NodeRant.class);
//                xmlNodeName = nodeName.XmlNodeName();
//                isMakeDefaultIfNoNode = nodeName.NewIfNoXmlNode();
            }
            ConfigFieldAttribute fAttr = new ConfigFieldAttribute();
            fAttr.setField(f);
            fAttr.setType(type);
            fAttr.setFieldName(fieldName);
            fAttr.setXmlNodeName(xmlNodeName);
            fAttr.setMakeDefaultIfNoNode(isMakeDefaultIfNoNode);
            fieldsAttr.put(xmlNodeName,fAttr);
        }
        if (!Validate.isNullOrEmpty(fieldsAttr)) {
//            classAttr.setFieldsAttribute(fieldsAttr);
        }
        configClasssAttr.put(nodeName, classAttr);
    }

    public static Object convert(@Comments("xml content") String content,
                                 @Comments("xml structure with javabean class") Class<?> cls)
            throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (null == cls) return null;
        parseFieldsAttribute(cls);
        //将xml格式的字符串转换成Document对象
        Document doc = DocumentHelper.parseText(content);
        //获取根节点
        Element root = doc.getRootElement();
        return mergerXmlNodeAndField(root, configClasssAttr);
    }

    public static Object convertfile(@Comments("xml file dull path") String xmlfile,
                                     @Comments("xml structure with javabean class") Class<?> cls)
            throws IOException, InvocationTargetException,
            InstantiationException, DocumentException, IllegalAccessException {
        File f = new File(xmlfile);
        String content = FileUtils.readFileToString(f);
        return convert(content, cls);
    }

    public static Object mergerXmlNodeAndField(@Comments("xml root element") Element root,
                                               @Comments("javabeans metadata") Map<String, ConfigClassAttribute> fieldsAttr)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //获取根节点下的所有元素
        List children = root.elements();
        List<DefaultAttribute> attrs = root.attributes();

        ConfigClassAttribute classAttribute = configClasssAttr.get(root.getName());
        Class clazz = classAttribute.getRealClass();
        Object obj = clazz.newInstance();

        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                Element child = (Element) children.get(i);
                if (!child.isTextOnly()) {
                    Object ochild = mergerXmlNodeAndField(child, fieldsAttr);
                    if (IAlbianXmlListNode.class.isAssignableFrom(obj.getClass())) {
                        IAlbianXmlListNode axn = (IAlbianXmlListNode) obj;
                        axn.addNode(ochild);
                    } else if (IAlbianXmlPairNode.class.isAssignableFrom(obj.getClass())) {
                        IAlbianXmlPairNode axpn = (IAlbianXmlPairNode) obj;
                        IAlbianXmlPairObject oc = (IAlbianXmlPairObject) ochild;
                        axpn.addNode(oc.getKey(), oc);
                    } else {
                        String xmlNodeName = child.getName();
//                        ConfigFieldAttribute fAttr = classAttribute.getFieldsAttribute().get(xmlNodeName);
//                        if (null == fAttr) continue;
//                        Field f = fAttr.getField();
//                        Class<?> type = fAttr.getType();
//                        Object value = toFieldValue(ochild,type);
//                        f.set(obj,value);
                    }
                } else {
                    String xmlNodeName = child.getName();
//                    ConfigFieldAttribute fAttr = classAttribute.getFieldsAttribute().get(xmlNodeName);
//                    if (null == fAttr) continue;
//                    Field f = fAttr.getField();
//                    Class<?> type = fAttr.getType();
//                    Object value = toFieldValue(child.getTextTrim(),type);
//                    f.set(obj,value);
                }
            }
        }

        for (DefaultAttribute att : attrs) {
            String xmlAttrName = att.getName();
//            ConfigFieldAttribute fAttr = classAttributeAttribute.getFieldsAttribute().get(xmlAttrName);
//            if (null == fAttr) continue;
//            Field f = fAttr.getField();
//            Class<?> type = fAttr.getType();
//            Object value = toFieldValue(att.getText(),type);
//            f.set(obj,value);
        }

        return obj;

    }

    /**
     * 将Object类型的值，转换成bean对象属性里对应的类型值
     *
     * @return 转换后的值
     */
    private static Object toFieldValue(@Comments("value of property") Object value,
                                       @Comments("type of value") Class type) {
        Object retVal = null;
        if (Long.class.isAssignableFrom(type)
                || long.class.isAssignableFrom(type)) {
            retVal = Long.parseLong(value.toString());
        } else if (Integer.class.isAssignableFrom(type)
                || int.class.isAssignableFrom(type)) {
            retVal = Integer.parseInt(value.toString());
        } else if (float.class.isAssignableFrom(type)
                || Float.class.isAssignableFrom(type)) {
            retVal = Float.parseFloat(value.toString());
        } else if (double.class.isAssignableFrom(type)
                || Double.class.isAssignableFrom(type)) {
            retVal = Double.parseDouble(value.toString());
        } else {
            retVal = value;

        }
        return retVal;
    }
}
