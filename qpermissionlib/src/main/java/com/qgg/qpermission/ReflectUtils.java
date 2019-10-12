package com.qgg.qpermission;

import com.qgg.qpermission.anntation.PermissionDenied;
import com.qgg.qpermission.anntation.PermissionGranted;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ReflectUtils {
    private ReflectUtils() {
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
            throw new RuntimeException(clazz.getSimpleName() + " must has method add annotation " + annotation.getSimpleName()+" requestCode");
        }
        return temp;
    }

    private static boolean isEqualRequestCodeFromAnnotation(Method m, Class clazz, int requestCode) {
        if (clazz.equals(PermissionGranted.class)) {
            return requestCode == m.getAnnotation(PermissionGranted.class).requestCode();
        } else if (clazz.equals(PermissionDenied.class)) {
            return requestCode == m.getAnnotation(PermissionDenied.class).requestCode();
        } else {
            return false;
        }
    }

    static void executeMethod(Object object, Method executeMethod) {
        if (executeMethod != null) {
            try {
                if (!executeMethod.isAccessible()) {
                    executeMethod.setAccessible(true);
                }
                executeMethod.invoke(object, null);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
