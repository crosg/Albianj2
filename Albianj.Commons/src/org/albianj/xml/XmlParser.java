package org.albianj.xml;

import java.util.Iterator;
import java.util.List;

import org.albianj.verify.Validate;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

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

	@SuppressWarnings("rawtypes")
	public static List analyze(Document doc, String tagName) {
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

	public static Element analyzeSingle(Document doc, String tagName) {
		if (null == doc) {
			throw new IllegalArgumentException("doc");
		}
		if (Validate.isNullOrEmptyOrAllSpace(tagName)) {
			throw new IllegalArgumentException("tagName");
		}
		@SuppressWarnings("rawtypes")
		List nodes = analyze(doc, tagName);
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
		return getAttributeValue(analyzeSingle(doc, tagName), attributeName);
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
		Element ele = analyzeSingle(doc, tagName);
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
}
