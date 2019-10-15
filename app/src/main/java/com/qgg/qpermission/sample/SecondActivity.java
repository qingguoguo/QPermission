package com.qgg.qpermission.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qgg.qpermission.QPermission;
import com.qgg.qpermission.anntation.PermissionDenied;
import com.qgg.qpermission.anntation.PermissionGranted;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Log.e("QPermission", "SecondActivity：" + getSupportFragmentManager().toString());
        findViewById(R.id.second_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QPermission.getInstance()
                        .with(SecondActivity.this)
                        .addRequestCode(300)
                        .openLog()
                        .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);

                QPermission.getInstance().requestPermission(
                        SecondActivity.this, 500, true, Manifest.permission.READ_CONTACTS);
            }
        });
    }

    @PermissionGranted(requestCode = 300)
    public void openCamera() {
        Toast.makeText(SecondActivity.this, "用户同意了相机和录音权限请求，可以干活了", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 300)
    public void closeCamera(List<String> permissions) {
        String text = TextUtils.join("，", permissions);
        Toast.makeText(SecondActivity.this, "用户拒绝了 " + text + " 请求", Toast.LENGTH_SHORT).show();
    }

    @PermissionGranted(requestCode = 500)
    public void openContacts() {
        Toast.makeText(SecondActivity.this, "用户同意了联系人权限请求，可以干活了", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 500)
    public void closeContacts(List<String> permissions) {
        String text = TextUtils.join("，", permissions);
        Toast.makeText(SecondActivity.this, "用户拒绝了 " + text + " 请求", Toast.LENGTH_SHORT).show();
    }
}
