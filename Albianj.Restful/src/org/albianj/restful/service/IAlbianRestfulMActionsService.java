package org.albianj.restful.service;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.service.IAlbianService;

public interface IAlbianRestfulMActionsService extends IAlbianService {
	boolean verify(IAlbianRestfulActionContext ctx);

	Method getAction(String name);
	public Method getActionVerify(String actionName);
	public Method getActionBefore(String action) ;
	public Method getActionAfter(String action);
	public int getActionMethod(String action);
}
