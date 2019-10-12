package com.qgg.qpermission;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;

/**
 * 作者：wangqing
 * 创建日期：2019/10/11 on 19:51
 * 描述：无界面fragment
 */
public class PermissionsFragment extends Fragment {
    private boolean mLogging = true;
    private SparseArray<QPermissionHelper> mSubjects = new SparseArray<>();
    private int mRequestCode;

    public PermissionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("onCreate()");
        // 设置为 true，configuration change 的时候，fragment 实例不会被重新创建
        setRetainInstance(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log("onDestroy()");
        QPermissionHelper qPermission = getQPermission(mRequestCode);
        if (qPermission != null) {
            qPermission.recycle(mRequestCode);
        }
        mRequestCode = -1;
    }

    /**
     * 对无界面的fragment请求权限回调结果解析
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mRequestCode = requestCode;
        log("Fragment onRequestPermissionsResult requestCode = " + mRequestCode);
        int code = mSubjects.get(requestCode).getRequestCode();
        if (requestCode != code) {
            Log.e(QPermission.TAG, "request code:" + requestCode + " != " + code);
            return;
        }
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }
        onRequestPermissionsResult(requestCode, permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        QPermissionHelper qPermissionHelper = getQPermission(requestCode);
        if (qPermissionHelper == null) {
            Log.e(QPermission.TAG, "QPermissionHelper.onRequestPermissionsResult invoked but didn't find the corresponding permission request.");
            return;
        }
        for (int i = 0, size = permissions.length; i < size; i++) {
            log("onRequestPermissionsResult  " + permissions[i]);
            boolean denied = grantResults[i] == PackageManager.PERMISSION_DENIED;
            if (denied) {
                //
                qPermissionHelper.onDenied();
                return;
            }
        }
        // 权限获得授权
        qPermissionHelper.onGranted();
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isGranted(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        final FragmentActivity fragmentActivity = getActivity();
        if (fragmentActivity == null) {
            throw new IllegalStateException("This fragment must be attached to an activity.");
        }
        return fragmentActivity.getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    public QPermissionHelper getQPermission(int key) {
        return mSubjects.get(key);
    }

    public void setSubjectForPermission(int key, @NonNull QPermissionHelper subject) {
        mSubjects.put(key, subject);
    }

    public void setLogging(boolean logging) {
        mLogging = logging;
    }

    void log(String message) {
        if (mLogging) {
            Log.d(QPermission.TAG, message);
        }
    }
}
