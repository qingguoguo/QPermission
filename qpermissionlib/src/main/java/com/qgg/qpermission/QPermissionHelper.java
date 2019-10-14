package com.qgg.qpermission;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.SparseArray;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：wangqing
 * 创建日期：2019/10/11 on 19:52
 * 描述：权限请求辅助类
 */
final public class QPermissionHelper {
    @VisibleForTesting
    private Lazy<PermissionsFragment> mPermissionsFragment;

    private int mRequestCode = -1;

    private Object object;

    private final static SparseArray<Method> DENIED_MAP = new SparseArray<>();
    private final static SparseArray<Method> GRANTED_MAP = new SparseArray<>();

    <T> QPermissionHelper(@NonNull final T t) {
        object = t;
        if (t instanceof FragmentActivity) {
            mPermissionsFragment = getLazySingleton(((FragmentActivity) t).getSupportFragmentManager());
        } else if (t instanceof Fragment) {
            mPermissionsFragment = getLazySingleton(((Fragment) t).getChildFragmentManager());

        } else {
            throw new RuntimeException("Qpermission not support " + t.getClass());
        }
    }

    @NonNull
    private Lazy<PermissionsFragment> getLazySingleton(@NonNull final FragmentManager fragmentManager) {
        return new Lazy<PermissionsFragment>() {
            private PermissionsFragment permissionsFragment;

            @Override
            public synchronized PermissionsFragment get() {
                if (permissionsFragment == null) {
                    permissionsFragment = getPermissionsFragment(fragmentManager);
                }
                return permissionsFragment;
            }
        };
    }

    private PermissionsFragment getPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        PermissionsFragment fragment = findPermissionsFragment(fragmentManager);
        boolean isNewInstance = fragment == null;
        if (isNewInstance) {
            fragment = new PermissionsFragment();
            fragmentManager
                    .beginTransaction()
                    .add(fragment, QPermission.TAG)
                    .commitNow();
        }
        return fragment;
    }

    private PermissionsFragment findPermissionsFragment(@NonNull final FragmentManager fragmentManager) {
        return (PermissionsFragment) fragmentManager.findFragmentByTag(QPermission.TAG);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestImplementation(final String... permissions) {
        List<Permission> list = new ArrayList<>(permissions.length);
        List<String> unrequestedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            mPermissionsFragment.get().log("Requesting permission " + permission);
            if (isGranted(permission)) {
                list.add(new Permission(permission, true, false));
                continue;
            }

            if (isRevoked(permission)) {
                list.add(new Permission(permission, false, false));
                continue;
            }

            unrequestedPermissions.add(permission);
            mPermissionsFragment.get().setSubjectForPermission(mRequestCode, this);
        }

        if (!unrequestedPermissions.isEmpty()) {
            String[] unrequestedPermissionsArray = unrequestedPermissions.toArray(new String[0]);
            requestPermissionsFromFragment(unrequestedPermissionsArray);
        } else {
            // unrequestedPermissions.isEmpty() 表示所有权限都已获得，直接执行方法
            onGranted();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean shouldShowRequestPermissionRationaleImplementation(final Activity activity, final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission) && !activity.shouldShowRequestPermissionRationale(permission)) {
                return false;
            }
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissionsFromFragment(String[] permissions) {
        mPermissionsFragment.get().log("requestPermissionsFromFragment " + TextUtils.join(", ", permissions));
        mPermissionsFragment.get().requestPermissions(permissions, mRequestCode);
    }

    private boolean isGranted(String... permissions) {
        return !PermissionUtils.isMarshmallow() || PermissionUtils.isGrantedPermission(mPermissionsFragment.get().getActivity(), permissions);
    }

    private boolean isRevoked(String permission) {
        return PermissionUtils.isMarshmallow() && PermissionUtils.isRevokedPermission(mPermissionsFragment.get().getActivity(), permission);
    }

    public void request(String... permissions) {
        // 检查requestCode
        checkRequestCode();
        // 检查权限
        boolean granted = isGranted(permissions);
        saveMethod(object);
        // 如果已经有权限就直接回调出去
        if (granted) {
            onGranted();
        } else {
            requestImplementation(permissions);
        }
    }

    private void saveMethod(Object activity) {
        // 获取带注解的方法
        DENIED_MAP.put(mRequestCode, ReflectUtils.findDeniedMethod(activity.getClass(), mRequestCode));
        GRANTED_MAP.put(mRequestCode, ReflectUtils.findGrantedMethod(activity.getClass(), mRequestCode));
    }

    private void checkRequestCode() {
        if (mRequestCode == -1) {
            throw new RuntimeException("requestCode = -1，must invoke method addRequestCode()");
        }
    }

    public QPermissionHelper addRequestCode(int mRequestCode) {
        this.mRequestCode = mRequestCode;
        return this;
    }

    int getRequestCode() {
        return mRequestCode;
    }

    public QPermissionHelper openLog() {
        mPermissionsFragment.get().setLogging(true);
        return this;
    }

    void onDenied(List<String> deniedPermissions) {
        mPermissionsFragment.get().log("QPermissionHelper onDenied " + mRequestCode);
        ReflectUtils.executeMethod(object, DENIED_MAP.get(mRequestCode), deniedPermissions);
        DENIED_MAP.remove(mRequestCode);
    }

    void onGranted() {
        mPermissionsFragment.get().log("QPermissionHelper onGranted " + mRequestCode);
        ReflectUtils.executeMethod(object, GRANTED_MAP.get(mRequestCode), null);
        GRANTED_MAP.remove(mRequestCode);
    }

    void recycle(int requestCode) {
        DENIED_MAP.remove(requestCode);
        GRANTED_MAP.remove(requestCode);
        mRequestCode = -1;
        object = null;
    }

    @FunctionalInterface
    public interface Lazy<V> {
        V get();
    }
}
