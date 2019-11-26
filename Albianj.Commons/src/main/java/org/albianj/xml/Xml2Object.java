package org.albianj.xml;

import org.albianj.comment.Comments;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 解析xml to javabean
 * 每个xml的子节点都是一个子类。
 * xml的配置节或者属性首字母默认为大小，若需要自定义，请使用XmlElementAttribute进行修饰
 * javabean 必须要实现签名接口： IAlbianXml2ObjectSigning，
 * 若该bean为list子类，必须实现签名接口 IAlbianXmlListNode<T>
 * <p>
 * <p>
 * Created by xuhaifeng on 17/2/2.
 */
public class Xml2Object {

    private static Map<String, BeanMetadata> beanMetadatas = new HashMap<>();

    private static void parseBeanMetadatas(@Comments("实体javabean类") Class<?> cls) throws IntrospectionException {
        if (null == cls) return;

        if (cls.isAnnotationPresent(XmlElementGenericAttribute.class)) {
            XmlElementGenericAttribute xega = cls.getAnnotation(XmlElementGenericAttribute.class);
            Class<?> c = xega.Clazz();
            parseBeanMetadatas(c);
        }

        String parentNodeName = null;
        if (cls.isAnnotationPresent(XmlElementAttribute.class)) {
            XmlElementAttribute xea = cls.getAnnotation(XmlElementAttribute.class);
            parentNodeName = xea.Name();
        }

        if (Validate.isNullOrEmptyOrAllSpace(parentNodeName)) {
            parentNodeName = cls.getSimpleName();
            parentNodeName = StringHelper.captureName(parentNodeName);
        }

        if (beanMetadatas.containsKey(parentNodeName)) return;

        BeanMetadata bmd = new BeanMetadata();
        bmd.setName(parentNodeName);
        bmd.setRealClass(cls);

        Class tempClass = cls;
        List<Field> fields = new ArrayList<>() ;
        while (tempClass !=null && !tempClass.getName().toLowerCase().equals("java.lang.object") ) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fields.addAll(Arrays.asList(tempClass .getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }

        Map<String, FieldMetadata> fms = new HashMap();
        for (Field f : fields) {
            FieldMetadata fm = new FieldMetadata();

            if(f.isAnnotationPresent(XmlElementIgnoreAttribute.class)) {
                continue;
            }
            f.setAccessible(true);
            Class<?> type = f.getType();
            if (IAlbianXml2ObjectSigning.class.isAssignableFrom(type)) {
                parseBeanMetadatas(type);
            }

            String nodeName = StringHelper.captureName(f.getName());

            if(f.isAnnotationPresent(XmlElementAttribute.class)){
                XmlElementAttribute xea = f.getAnnotation(XmlElementAttribute.class);
                nodeName = xea.Name();
            }

            fm.setField(f);
            fm.setFieldName(f.getName());
            fm.setNodeName(nodeName);
            fm.setType(type);

            fms.put(nodeName,fm);
        }
        if (!Validate.isNullOrEmpty(fms)) {
            bmd.setFieldMetadatas(fms);
        }
        beanMetadatas.put(parentNodeName, bmd);

//        BeanInfo info = Introspector.getBeanInfo(cls, Object.class);
//        PropertyDescriptor[] pds = info.getPropertyDescriptors();
//        Map<String, FieldMetadata> propertyMetadatas = new HashMap();
//        for (PropertyDescriptor pd : pds) {
//            FieldMetadata pm = new FieldMetadata();
//            Class<?> type = pd.getPropertyType();
//            if (IAlbianXml2ObjectSigning.class.isAssignableFrom(type)) {
////                System.out.println(type.getSimpleName());
//                parseBeanMetadatas(type);
//            }
//            pm.setType(type);
//            Method mr = pd.getReadMethod();
//            Method mw = pd.getWriteMethod();
//            if (null == mw) {
//                continue;
////                    throw new RuntimeException("no setter method.");
//            }
//            XmlElementAttribute xeap = null;
//            String pname = null;
//            if (mr.isAnnotationPresent(XmlElementAttribute.class)) {
//                xeap = mr.getAnnotation(XmlElementAttribute.class);
//                pname = xeap.Name();
//            }
//            if (mw.isAnnotationPresent(XmlElementAttribute.class)) {
//                xeap = mw.getAnnotation(XmlElementAttribute.class);
//                pname = xeap.Name();
//
//            }
//            if (Validate.isNullOrEmptyOrAllSpace(pname)) {
//                pname = pd.getName();
//                pname = StringHelper.captureName(pname);
//            }
//            pm.setName(pname);
//            pm.setField(f);
//            propertyMetadatas.put(pname, pm);
//        }
//        if (!Validate.isNullOrEmpty(propertyMetadatas)) {
//            bmd.setPropertyMetadatas(propertyMetadatas);
//        }
//        beanMetadatas.put(nodeName, bmd);
    }

    public static Object convert(@Comments("xml content") String content,
                                 @Comments("xml structure with javabean class") Class<?> cls)
            throws DocumentException, IllegalAccessException, InvocationTargetException, InstantiationException, IntrospectionException {
        if (null == cls) return null;
        parseBeanMetadatas(cls);
        //将xml格式的字符串转换成Document对象
        Document doc = DocumentHelper.parseText(content);
        //获取根节点
        Element root = doc.getRootElement();
        return nodeToBean(root, beanMetadatas);
    }

    public static Object convertfile(@Comments("xml file dull path") String xmlfile,
                                     @Comments("xml structure with javabean class") Class<?> cls)
            throws IOException, InvocationTargetException, IntrospectionException,
            InstantiationException, DocumentException, IllegalAccessException {
        File f = new File(xmlfile);
        String content = FileUtils.readFileToString(f);
        return convert(content, cls);
    }

    public static Object nodeToBean(@Comments("xml root element") Element root,
                                    @Comments("javabeans metadata") Map<String, BeanMetadata> beanMetadatas)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        //获取根节点下的所有元素
        List children = root.elements();
        List<DefaultAttribute> attrs = root.attributes();

        BeanMetadata bmd = beanMetadatas.get(root.getName());
        Class clazz = bmd.getRealClass();
        Object obj = clazz.newInstance();

        if (children != null && children.size() > 0) {
            for (int i = 0; i < children.size(); i++) {
                Element child = (Element) children.get(i);
                if (!child.isTextOnly()) {
                    Object ochild = nodeToBean(child, beanMetadatas);
                    if (IAlbianXmlListNode.class.isAssignableFrom(obj.getClass())) {
                        IAlbianXmlListNode axn = (IAlbianXmlListNode) obj;
                        axn.addNode(ochild);
                    } else if (IAlbianXmlPairNode.class.isAssignableFrom(obj.getClass())) {
                        IAlbianXmlPairNode axpn = (IAlbianXmlPairNode) obj;
                        IAlbianXmlPairObject oc = (IAlbianXmlPairObject) ochild;
                        axpn.addNode(oc.getKey(), oc);
                    } else {
                        String propertyName = child.getName();

                        FieldMetadata pm = bmd.getFieldMetadatas().get(propertyName);
                        if (null == pm) continue;

                        Class<?> type = pm.getType();
                        Object value = convertValType(ochild, type);
                        pm.getField().set(obj,value);
//                        m.invoke(obj, value);
                    }

                } else {
                    String propertyName = child.getName();
                    FieldMetadata pm = bmd.getFieldMetadatas().get(propertyName);
                    if (null == pm) continue;
//                    Method m = pm.getSetter();
                    Class<?> type = pm.getType();
                    Object value = convertValType(child.getTextTrim(), type);
                    pm.getField().set(obj,value);
                }

            }
        }

        for (DefaultAttribute att : attrs) {
            String propertyName = att.getName();
            FieldMetadata pm = bmd.getFieldMetadatas().get(propertyName);
            if (null == pm) continue;
//            Method m = pm.getSetter();
            Class<?> type = pm.getType();
            Object value = convertValType(att.getText(), type);
            pm.getField().set(obj,value);
        }

        return obj;
    }

    /**
     * 将Object类型的值，转换成bean对象属性里对应的类型值
     *
     * @return 转换后的值
     */
    private static Object convertValType(@Comments("value of property") Object value,
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
        } else if(boolean.class.isAssignableFrom(type)) {
            retVal = Boolean.parseBoolean(value.toString());
        }else {
            retVal = value;

        }
        return retVal;
    }
}
