package org.albianj.restful.impl.object;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.restful.object.AlbianRestfulResultStyle;
import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.restful.object.IAlbianRestfulResult;
import org.albianj.restful.service.IAlbianRestfulResultParser;

public class AlbianRestfulActionContext implements IAlbianRestfulActionContext {
	HttpServletRequest req = null;
	HttpServletResponse resp  = null;
	ServletContext sc  = null;
	String sessionId = null;
	String serviceName  = null;
	String actionName = null;
	String sp = null;
	Map<String, String> paras = null;
	String body  = null;
	IAlbianRestfulResultParser parser = null;
	
	AlbianRestfulResultStyle style = AlbianRestfulResultStyle.Json;
	IAlbianRestfulResult rc = null;

	public AlbianRestfulActionContext(HttpServletRequest req, HttpServletResponse resp, ServletContext sc,
			String serviceName, String actionName, String sessionId, String sp, Map<String, String> paras,
			String body) {
		super();
		this.req = req;
		this.resp = resp;
		this.sc = sc;
		this.serviceName = serviceName;
		this.actionName = actionName;
		this.sessionId = sessionId;
		this.sp = sp;
		this.paras = paras;
		this.body = body;
	}

	@Override
	public HttpServletRequest getCurrentRequest() {
		// TODO Auto-generated method stub
		return this.req;
	}

	@Override
	public HttpServletResponse getCurrentResponse() {
		// TODO Auto-generated method stub
		return this.resp;
	}

	@Override
	public ServletContext getCurrentServletContext() {
		// TODO Auto-generated method stub
		return this.sc;
	}

	@Override
	public String getCurrentSessionId() {
		// TODO Auto-generated method stub
		return this.sessionId;
	}

	@Override
	public String getCurrentServiceName() {
		// TODO Auto-generated method stub
		return this.serviceName;
	}

	@Override
	public String getCurrentActionName() {
		// TODO Auto-generated method stub
		return this.actionName;
	}

	@Override
	public String getCurrentSP() {
		// TODO Auto-generated method stub
		return this.sp;
	}

	@Override
	public Map<String, String> getCurrentParameters() {
		// TODO Auto-generated method stub
		return this.paras;
	}

	@Override
	public String getCurrentRequestBody() {
		// TODO Auto-generated method stub
		return this.body;
	}

	@Override
	public AlbianRestfulResultStyle getResultStyle() {
		// TODO Auto-generated method stub
		return this.style;
	}

	@Override
	public void setResultStyle(AlbianRestfulResultStyle style) {
		// TODO Auto-generated method stub
		this.style = style;
	}

	@Override
	public IAlbianRestfulResult getResult() {
		// TODO Auto-generated method stub
		return this.rc;
	}

	@Override
	public void setResult(IAlbianRestfulResult rc) {
		// TODO Auto-generated method stub
		this.rc = rc;
	}

	@Override
	public void setResult(AlbianRestfulResultStyle style,IAlbianRestfulResultParser parser, IAlbianRestfulResult rc) {
		// TODO Auto-generated method stub
		this.style = style;
		this.rc = rc;
		this.parser = parser;
	}

	@Override
	public IAlbianRestfulResultParser getParser() {
		// TODO Auto-generated method stub
		return this.parser;
	}

	@Override
	public void setParser(IAlbianRestfulResultParser parser) {
		// TODO Auto-generated method stub
		this.parser = parser;
	}

}
