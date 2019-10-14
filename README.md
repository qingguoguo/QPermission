# QPermission
QPermission是基于注解解耦的Android权限请求框架

# 使用场景
支持在  
1.Fragment  
2.Activity

# 如何使用
gradle中添加依赖
```
implementation 'com.qingguoguo:qpermission:1.0.0'
```
## 使用方式1
1.链式调用，发起权限请求
```
QPermission.getInstance()
    .with(SecondActivity.this)
    .addRequestCode(300)
    .openLog()
    .request(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO);
```
2.定义带自定义注解的方法  
PermissionGranted：表示权限请求成功后执行的方法  
PermissionDenied：表示权限被拒绝后执行的方法 
 
特别注意：
PermissionDenied修饰的方法，必须带参数，如示例代码所示，参数表示被拒绝授权的权限  
requestCode：一次请求的requestCode要一致，也就是PermissionGranted和PermissionDenied的参数一致
```
    @PermissionGranted(requestCode = 300)
    public void openCamera() {
        Toast.makeText(SecondActivity.this, "用户同意了相机和录音权限请求，可以干活了", Toast.LENGTH_SHORT).show();
    }

    @PermissionDenied(requestCode = 300)
    public void closeCamera(List<String> permissions) {
        Toast.makeText(SecondActivity.this, "用户拒绝了 " + permissions.size() + " 请求", Toast.LENGTH_SHORT).show();
    }
```
## 使用方式2
和方式1比较只是第一步调用形式不一样，其他一致，参考方式1
```
QPermission.getInstance().requestPermission(
    SecondActivity.this, 500, true, Manifest.permission.READ_CONTACTS);
```
# 其他流行的Android权限请求框架
[PermissionGen](https://github.com/lovedise/PermissionGen)  

[AndPermission](https://github.com/yanzhenjie/AndPermission)  

[RxPermissions](https://github.com/tbruyelle/RxPermissions)

# License
Copyright (C) 2019 qingguoguo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.