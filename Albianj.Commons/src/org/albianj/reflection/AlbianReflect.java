package org.albianj.reflection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public class AlbianReflect {
	
	public static BeanInfo getBeanInfo(ClassLoader cl, String className) 
	throws ClassNotFoundException, IntrospectionException {
	Class cls = cl.loadClass(className);
	BeanInfo info = Introspector.getBeanInfo(cls, Object.class);
	return info;
}

public static PropertyDescriptor[] getBeanPropertyDescriptor(ClassLoader cl,String className) 
			throws ClassNotFoundException, IntrospectionException {
	BeanInfo beanInfo;
	beanInfo = getBeanInfo(cl,className);
return beanInfo.getPropertyDescriptors();
}

public static String getClassSimpleName(ClassLoader cl,String className) 
		throws ClassNotFoundException {
		Class cls = cl.loadClass(className);
		return cls.getSimpleName();
}

	public static String getClassName(Class<?> cls) {
		return cls.getName();
	}

	public static String getSimpleName(Class<?> cls) {
		return cls.getSimpleName();
	}
	
	public static <T> T newInstance(Class<T> cls,Class<?>[] parameterTypes,Object[] initArgs) 
			throws InstantiationException, IllegalAccessException, 
			NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
			Constructor<T> cons = null;
			if(null == parameterTypes || 0 == parameterTypes.length) {
				return cls.newInstance();
			}
			cons = cls.getConstructor(parameterTypes);
			T instance = cons.newInstance(initArgs);
			return instance;
	}
}
