package com.qgg.qpermission.sample;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

/**
 * 作者：王青 wangqing
 * 创建日期：2019/10/12 on 9:39
 * 描述：
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        setupLeakCanary();
    }

    /**
     * 配置LeakCanary
     */
    private void setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }
}
