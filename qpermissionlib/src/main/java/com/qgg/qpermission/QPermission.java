package com.qgg.qpermission;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * 作者：wangqing
 * 创建日期：2019/10/11 on 16:40
 * 描述：权限请求单例类
 */
public class QPermission {
    static final String TAG = QPermission.class.getSimpleName();

    private QPermission() {
    }

    public static QPermission getInstance() {
        return QPermissionHelperHolder.instance;
    }

    public QPermissionHelper with(@NonNull final FragmentActivity activity) {
        return new QPermissionHelper(activity);
    }

    public QPermissionHelper with(@NonNull final Fragment fragment) {
        return new QPermissionHelper(fragment);
    }

    public <T> void requestPermission(@NonNull final T t, int requestCode, String... permissions) {
        new QPermissionHelper(t).addRequestCode(requestCode).request(permissions);
    }

    public <T> void requestPermission(@NonNull final T t, int requestCode, boolean openLog, String... permissions) {
        if (!openLog) {
            requestPermission(t, requestCode, permissions);
        } else {
            new QPermissionHelper(t).addRequestCode(requestCode).openLog().request(permissions);
        }
    }

    private static class QPermissionHelperHolder {
        private static QPermission instance = new QPermission();
    }
}
