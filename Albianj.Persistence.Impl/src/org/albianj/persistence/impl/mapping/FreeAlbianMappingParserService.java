package org.albianj.persistence.impl.mapping;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.List;

import org.albianj.io.Path;
import org.albianj.kernel.KernelSetting;
import org.albianj.logger.IAlbianLoggerService;
import org.albianj.persistence.object.IAlbianObjectAttribute;
import org.albianj.persistence.service.IAlbianMappingParserService;
import org.albianj.service.AlbianServiceRouter;
import org.albianj.service.parser.AlbianParserException;
import org.albianj.service.parser.FreeAlbianParserService;
import org.albianj.verify.Validate;
import org.albianj.xml.XmlParser;
import org.dom4j.Document;
import org.dom4j.Element;

public abstract class FreeAlbianMappingParserService extends FreeAlbianParserService implements IAlbianMappingParserService
		 {

	private String file = "persistence.xml";
	private final static String tagName = "AlbianObjects/AlbianObject";
	
	private HashMap<String,IAlbianObjectAttribute> _objAttrs = null;
	private HashMap<String,String> _class2Inter = null;
	private HashMap<String,PropertyDescriptor[] > _bpd= null;

	public void setConfigFileName(String fileName) {
		this.file = fileName;
	}
	
	public void init() throws AlbianParserException {
		Document doc = null;
		_objAttrs = new HashMap<>();
		_class2Inter = new HashMap<>();
		_bpd = new HashMap<>();
		try {
			doc = XmlParser.load(Path.getExtendResourcePath(KernelSetting
					.getAlbianConfigFilePath() + file));
		} catch (Exception e) {					
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,e,
					"PersistenService is error",
					"loading the persisten.xml is error.");
		}
		if (null == doc) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"PersistenService is error",
					"loading the persisten.xml is error. the file is null.");
		}
		@SuppressWarnings("rawtypes")
		List nodes = XmlParser.analyze(doc, tagName);
		if (Validate.isNullOrEmpty(nodes)) {
			AlbianServiceRouter.getLogger()
			.errorAndThrow(
					IAlbianLoggerService.AlbianRunningLoggerName,
					AlbianParserException.class,
					"PersistenService is error",
					"parser the node tags:%s in the persisten.xml is error. the node of the tags is null or empty.",
					tagName);
		}
		parserAlbianObjects(nodes);
		return;
	}

	protected abstract void parserAlbianObjects (
			@SuppressWarnings("rawtypes") List nodes)
			throws AlbianParserException;

	protected abstract IAlbianObjectAttribute parserAlbianObject(Element node)
	throws AlbianParserException;
	
	public void addAlbianObjectAttribute(String name,IAlbianObjectAttribute aba){
		_objAttrs.put(name, aba);
	}
	public IAlbianObjectAttribute getAlbianObjectAttribute(String name){
		return _objAttrs.get(name);
	}
	
	public void addAlbianObjectClassToInterface(String type,String inter){
		_class2Inter.put(type, inter);
	}
	public String getAlbianObjectInterface(String type){
		return _class2Inter.get(type);
	}
	
	public void addAlbianObjectPropertyDescriptor(String type,PropertyDescriptor[] pds){
		_bpd.put(type, pds);
	}
	public PropertyDescriptor[] getAlbianObjectPropertyDescriptor(String type){
		return _bpd.get(type);
	}
	

}
