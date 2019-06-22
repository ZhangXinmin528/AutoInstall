package com.zxm.autoinstall;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.zxm.libinstaller.BuildConfig;
import com.zxm.libinstaller.helper.ApkInstaller;
import com.zxm.libinstaller.info.InstallState;
import com.zxm.libinstaller.listener.SimpleOnInstallStateListener;
import com.zxm.libinstaller.utils.AccessibilityUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String APK_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "Download" + File.separator + "tkmeeting1.0.8.apk";
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Toast.makeText(mContext, "Current device is root?-->" + AccessibilityUtil.isDeviceRooted(),
                Toast.LENGTH_SHORT).show();

        findViewById(R.id.btn_install)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        installUnknownSourceApp();
                    }
                });
    }

    private void installUnknownSourceApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final PackageManager packageManager = mContext.getPackageManager();
            if (packageManager != null) {
                final boolean hasPermission = packageManager.canRequestPackageInstalls();
                if (!hasPermission) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("允许安装未知来源应用？")
                            .setMessage("安装未知应用容易带来未知风险！")
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openUnknownAppSourcesSetting(MainActivity.this, 1001);
                        }
                    }).show();

                } else {
                   /* final File file = AccessibilityUtil.getFileByPath(APK_FILE_PATH);
                    installApkWitNoRoot(file);*/

                    ApkInstaller installer = ApkInstaller.getInstance(mContext);
                    installer.setInstallStateListener(new SimpleOnInstallStateListener() {
                        @Override
                        public void onInstallComplete() {
                            super.onInstallComplete();
                            Toast.makeText(mContext, "安装完成！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onInstallFailed(InstallState state) {
                            super.onInstallFailed(state);
                            Toast.makeText(mContext, "安装失败:"+state.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    installer.startInstall(APK_FILE_PATH);
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @RequiresPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
    public void openUnknownAppSourcesSetting(@NonNull Activity context, int requestCode) {
        if (context == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                final boolean hasPermission = packageManager.canRequestPackageInstalls();
                if (!hasPermission) {

                    final Uri uri = Uri.parse("package:" + context.getPackageName());
                    final Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
                    context.startActivityForResult(intent, requestCode);
                } else {
                    ApkInstaller installer = ApkInstaller.getInstance(mContext);
                    installer.setInstallStateListener(new SimpleOnInstallStateListener() {
                        @Override
                        public void onInstallComplete() {
                            super.onInstallComplete();
                            Toast.makeText(mContext, "安装完成！", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onInstallFailed(InstallState state) {
                            super.onInstallFailed(state);
                            Toast.makeText(mContext, "安装失败:"+state.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    installer.startInstall(APK_FILE_PATH);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1001:
                Toast.makeText(mContext, "resultCode : " + resultCode, Toast.LENGTH_SHORT).show();
                installUnknownSourceApp();

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
