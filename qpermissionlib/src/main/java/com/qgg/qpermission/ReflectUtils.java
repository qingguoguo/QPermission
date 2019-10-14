package com.qgg.qpermission;

import com.qgg.qpermission.anntation.PermissionDenied;
import com.qgg.qpermission.anntation.PermissionGranted;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

final class ReflectUtils {
    private ReflectUtils() {
        throw new RuntimeException("ReflectUtils is not support");
    }

    static Method findDeniedMethod(Class clazz, int requestCode) {
        return findMethodWithClass(clazz, PermissionDenied.class, requestCode);
    }

    static Method findGrantedMethod(Class clazz, int requestCode) {
        return findMethodWithClass(clazz, PermissionGranted.class, requestCode);
    }

    private static <A extends Annotation> Method findMethodWithClass(Class clazz,
                                                                     Class<A> annotation, int requestCode) {
        Method temp = null;
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(annotation)) {
                if (isEqualRequestCodeFromAnnotation(method, annotation, requestCode)) {
                    temp = method;
                }
            }
        }
        if (temp == null) {
            throw new RuntimeException(clazz.getSimpleName() + " must has method add annotation " + annotation.getSimpleName() + " requestCode");
        }
        return temp;
    }

    private static boolean isEqualRequestCodeFromAnnotation(Method method, Class clazz, int requestCode) {
        if (clazz.equals(PermissionGranted.class)) {
            return requestCode == method.getAnnotation(PermissionGranted.class).requestCode();
        } else if (clazz.equals(PermissionDenied.class)) {
            Type[] parameters = method.getGenericParameterTypes();
            if (parameters.length != 1) {
                throw new RuntimeException("PermissionDenied method must one parameter");
            }
            return requestCode == method.getAnnotation(PermissionDenied.class).requestCode();
        } else {
            return false;
        }
    }

    static void executeMethod(Object object, Method executeMethod, List<String> permissions) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                if (permissions == null) {
                    executeMethod.invoke(object, null);
                } else {
                    executeMethod.invoke(object, permissions);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
