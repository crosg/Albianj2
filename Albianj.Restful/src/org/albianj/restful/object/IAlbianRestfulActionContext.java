package org.albianj.restful.object;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.restful.service.IAlbianRestfulResultParser;

public interface IAlbianRestfulActionContext {

	HttpServletRequest getCurrentRequest();

	HttpServletResponse getCurrentResponse();

	ServletContext getCurrentServletContext();

	String getCurrentSessionId();

	String getCurrentServiceName();

	String getCurrentActionName();

	String getCurrentSP();

	Map<String, String> getCurrentParameters();

	String getCurrentRequestBody();

	AlbianRestfulResultStyle getResultStyle();

	void setResultStyle(AlbianRestfulResultStyle style);

	IAlbianRestfulResult getResult();

	void setResult(IAlbianRestfulResult rc);

	void setResult(AlbianRestfulResultStyle style,
			IAlbianRestfulResultParser parser, IAlbianRestfulResult rc);

	IAlbianRestfulResultParser getParser();

	void setParser(IAlbianRestfulResultParser parser);

}
