package com.zxm.autoinstall;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zxm.libinstaller.helper.ApkInstaller;
import com.zxm.libinstaller.info.InstallState;
import com.zxm.libinstaller.listener.SimpleOnInstallStateListener;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private Context mContext;

    public static final String APK_FILE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "Download" + File.separator + "test.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        findViewById(R.id.btn_install)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
                });
    }
}
