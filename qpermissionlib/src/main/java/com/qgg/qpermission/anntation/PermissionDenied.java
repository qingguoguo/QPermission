package com.qgg.qpermission.anntation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者：王青 wangqing
 * 创建日期：2019/10/11 on 21:35
 * 描述：有权限被拒绝
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionDenied {
    int requestCode();
}
