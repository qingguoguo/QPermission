package com.qgg.qpermission.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qgg.qpermission.QPermission;
import com.qgg.qpermission.anntation.PermissionDenied;
import com.qgg.qpermission.anntation.PermissionGranted;

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
                        .request(Manifest.permission.CAMERA);

                QPermission.getInstance().requestPermission(
                        SecondActivity.this, 500, true, Manifest.permission.READ_CONTACTS);
            }
        });
    }

    @PermissionGranted(requestCode = 300)
    public void openCamera() {
        Toast.makeText(SecondActivity.this, "用户同意了权限请求，可以干活了", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 300)
    public void closeCamera() {
        Toast.makeText(SecondActivity.this, "用户拒绝了权限请求", Toast.LENGTH_SHORT).show();
    }
}
