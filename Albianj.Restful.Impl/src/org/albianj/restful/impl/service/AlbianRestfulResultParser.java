package org.albianj.restful.impl.service;

import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.restful.service.IAlbianRestfulResultParser;

import com.thoughtworks.xstream.XStream;

public class AlbianRestfulResultParser implements IAlbianRestfulResultParser {

	@Override
	public String parserToXml(IAlbianRestfulResult result) {
		// 字符串转XML
		XStream xstream = new XStream();
		return xstream.toXML(result.getResult());
	}
}
