package org.albianj.boot;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class FinalAlbianReflectService {

    public static FinalAlbianReflectService Instance = null;

    static {
        Instance = new FinalAlbianReflectService();
    }

    /**
     * Checks whether can control member accessible.
     *
     * @return If can control member accessible, it return {@literal true}
     * @since 3.5.0
     */
    public static boolean canControlMemberAccessible() {
        try {
            SecurityManager securityManager = System.getSecurityManager();
            if (null != securityManager) {
                securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
            }
        } catch (SecurityException e) {
            return false;
        }
        return true;
    }

    /**
     * 得到所有的方法，包括父类和接口。
     * 如果子类重写父类的方法，只保留子类的方法签名
     */
    public Method[] getAllMethod(Class<?> cls) {
        Map<String, Method> uniqueMethods = new HashMap<String, Method>();
        Class<?> currentClass = cls;
        while (currentClass != null && currentClass != Object.class) {
            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());

            //获取接口中的所有方法
            Class<?>[] interfaces = currentClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                addUniqueMethods(uniqueMethods, anInterface.getMethods());
            }
            //获取父类，继续while循环
            currentClass = currentClass.getSuperclass();
        }

        Collection<Method> methods = uniqueMethods.values();

        return methods.toArray(new Method[methods.size()]);
    }

    private void addUniqueMethods(Map<String, Method> uniqueMethods, Method[] methods) {
        for (Method currentMethod : methods) {
            if (!currentMethod.isBridge()) {
                //获取方法的签名，格式是：返回值类型#方法名称:参数类型列表
                String signature = getMethodSignature(currentMethod);
                //检查是否在子类中已经添加过该方法，如果在子类中已经添加过，则表示子类覆盖了该方法，无须再向uniqueMethods集合中添加该方法了
                if (!uniqueMethods.containsKey(signature)) {
                    if (canControlMemberAccessible()) {
                        try {
                            currentMethod.setAccessible(true);
                        } catch (Exception e) {
                            // Ignored. This is only a final precaution, nothing we can do.
                        }
                    }
                    uniqueMethods.put(signature, currentMethod);
                }
            }
        }
    }

    /**
     * 对于方法签名
     * 签名：返回值类型#方法名称:参数类型列表
     *
     * @param method
     * @return
     */
    public String getMethodSignature(Method method) {
        StringBuilder sb = new StringBuilder();
        Class<?> returnType = method.getReturnType();
        if (returnType != null) {
            sb.append(returnType.getName()).append('#');
        }
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    /**
     * get all field,include parent class or interface
     *
     * @param cls
     * @return
     */
    public Map<String, Field> getAllField(Class<?> cls) {
        Class tempClass = cls;
        Map<String, Field> uniqueFields = new HashMap<>();
        while (null != tempClass && !tempClass.getName().toLowerCase().equals("java.lang.object")) {//当父类为null的时候说明到达了最上层的父类(Object类).
            addUniqueFields(uniqueFields, tempClass.getDeclaredFields());
            tempClass = tempClass.getSuperclass();
        }
        return uniqueFields;
    }

    private void addUniqueFields(Map<String, Field> uniqueFields, Field[] fields) {
        for (Field f : fields) {
            //检查是否在子类中已经添加过该方法，如果在子类中已经添加过，则表示子类覆盖了该方法，无须再向uniqueMethods集合中添加该方法了
            if (!uniqueFields.containsKey(f.getName())) {
                try {
                    f.setAccessible(true);
                } catch (Exception e) {
                    // Ignored. This is only a final precaution, nothing we can do.
                }
            }
            uniqueFields.put(f.getName(), f);
        }
    }

//    public Method[] getAllVirtualMethod(Class<?> cls){
//        Map<String, Method> methods = new HashMap<String, Method>();
//        Class<?> currentClass = cls;
//        while (currentClass != null && currentClass != Object.class) {
//            addUniqueMethods(uniqueMethods, currentClass.getDeclaredMethods());
//
//            //获取接口中的所有方法
//            Class<?>[] interfaces = currentClass.getInterfaces();
//            for (Class<?> anInterface : interfaces) {
//                addUniqueMethods(uniqueMethods, anInterface.getMethods());
//            }
//            //获取父类，继续while循环
//            currentClass = currentClass.getSuperclass();
//        }
//
//        Collection<Method> methods = uniqueMethods.values();
//
//        return methods.toArray(new Method[methods.size()]);
//    }
}
