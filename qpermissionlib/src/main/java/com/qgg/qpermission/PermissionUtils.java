package com.qgg.qpermission;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * 作者：wangqing
 * 创建日期：2019/10/14 on 14:19
 * 描述：权限工具类
 */
class PermissionUtils {
    private PermissionUtils() {
        throw new RuntimeException("PermissionUtils is not support");
    }

    static boolean isMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    @TargetApi(Build.VERSION_CODES.M)
    static boolean isGrantedPermission(Context context, String... permissions) {
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    static boolean isRevokedPermission(Context context, String permission) {
        return context.getPackageManager().isPermissionRevokedByPolicy(permission, context.getPackageName());
    }
}
