package org.albianj.restful.impl.service;

import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.albianj.restful.object.IAlbianRestfulActionContext;
import org.albianj.restful.service.AlbianRestfulActionMethodContext;
import org.albianj.restful.service.IAlbianRestfulActionMethodContext;
import org.albianj.restful.service.IAlbianRestfulMActionsService;
import org.albianj.service.FreeAlbianService;
import org.albianj.verify.Validate;

public abstract class FreeAlbianRestfulMActionsService extends FreeAlbianService
		implements IAlbianRestfulMActionsService {

	// private HashMap<String, Method> methods = null;
	// private HashMap<String,Method> verifyMethods = null;

	private HashMap<String, IAlbianRestfulActionMethodContext> methods = null;

	@Override
	public void afterLoading() throws RuntimeException {
		methods = new HashMap<String, IAlbianRestfulActionMethodContext>();
		// verifyMethods = new HashMap<String, Method>();
		Method[] ms = this.getClass().getMethods();
		for (Method m : ms) {
			IAlbianRestfulActionMethodContext action = null;

			if (m.isAnnotationPresent(AlbianRestfulActionAttribute.class)) {
				AlbianRestfulActionAttribute araa = m.getAnnotation(AlbianRestfulActionAttribute.class);
				String name = null;
				if (Validate.isNullOrEmptyOrAllSpace(araa.Name())) {
					name = m.getName();
					action = methods.get(name);
				} else {
					name = araa.Name();
					action = methods.get(name);
				}
				
				if (null == action) {
					action = new AlbianRestfulActionMethodContext();
					action.setAction(m);
					methods.put(name, action);
				} else {
					action.setAction(m);
				}
				
				action.setMethod(araa.Method());
			}

			if (m.isAnnotationPresent(AlbianRestfulActionVerifyAttribute.class)) {
				AlbianRestfulActionVerifyAttribute araa = m.getAnnotation(AlbianRestfulActionVerifyAttribute.class);

				String name = null;
				if (!Validate.isNullOrEmptyOrAllSpace(araa.ServiceName())) {
					name = m.getName();
					if (null == action) {
						action = new AlbianRestfulActionMethodContext();
						action.setVerfiy(m);
						methods.put(name, action);
					} else {
						action.setVerfiy(m);
					}
					action = methods.get(name);

				}
			}

			if (m.isAnnotationPresent(AlbianRestfulActionAOPAttribute.class)) {
				AlbianRestfulActionAOPAttribute araa = m.getAnnotation(AlbianRestfulActionAOPAttribute.class);

				String name = null;
				if (!Validate.isNullOrEmptyOrAllSpace(araa.ServiceName())) {
					name = m.getName();
					if (null == action) {
						action = new AlbianRestfulActionMethodContext();
						if (TriggerPointStyle.before == araa.TriggerPoint()) {
							action.setBefore(m);
							methods.put(name, action);
						}
						if (TriggerPointStyle.after == araa.TriggerPoint()) {
							action.setAfter(m);
							methods.put(name, action);
						}
					} else {
						if (TriggerPointStyle.before == araa.TriggerPoint()) {
							action.setBefore(m);
						}
						if (TriggerPointStyle.after == araa.TriggerPoint()) {
							action.setAfter(m);
						}
					}
					action = methods.get(name);
				}
			}
		}
		super.afterLoading();
	}

	@Override
	public boolean verify(IAlbianRestfulActionContext ctx) {
		return true;
	}

	@Override
	public Method getAction(String name) {
		if (Validate.isNullOrEmptyOrAllSpace(name))
			return null;
		if (Validate.isNullOrEmpty(methods)) {
			return null;
		}
		IAlbianRestfulActionMethodContext am = methods.get(name);
		if (null == am)
			return null;
		return am.getAction();
	}

	public Method getActionVerify(String action) {
		if (Validate.isNullOrEmptyOrAllSpace(action))
			return null;
		if (Validate.isNullOrEmpty(methods)) {
			return null;
		}
		IAlbianRestfulActionMethodContext am = methods.get(action);
		if (null == am)
			return null;
		return am.getVerfiy();
	}

	public Method getActionBefore(String action) {
		if (Validate.isNullOrEmptyOrAllSpace(action))
			return null;
		if (Validate.isNullOrEmpty(methods)) {
			return null;
		}
		IAlbianRestfulActionMethodContext am = methods.get(action);
		if (null == am)
			return null;
		return am.getBefore();
	}

	public Method getActionAfter(String action) {
		if (Validate.isNullOrEmptyOrAllSpace(action))
			return null;
		if (Validate.isNullOrEmpty(methods)) {
			return null;
		}
		IAlbianRestfulActionMethodContext am = methods.get(action);
		if (null == am)
			return null;
		return am.getAfter();
	}
	public int getActionMethod(String action){
		if (Validate.isNullOrEmptyOrAllSpace(action))
			return 0;
		if (Validate.isNullOrEmpty(methods)) {
			return 0;
		}
		IAlbianRestfulActionMethodContext am = methods.get(action);
		if (null == am)
			return 0;
		return am.getMethod();
	}
}
