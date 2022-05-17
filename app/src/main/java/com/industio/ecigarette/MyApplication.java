package com.industio.ecigarette;

import android.app.Application;
import android.util.Log;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.Utils;
import com.industio.ecigarette.util.TimerUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("CrashUtils","---------_>"+Utils.getApp().getFilesDir());
        Log.d("CrashUtils","---------_>"+Utils.getApp().getExternalFilesDir(null));

        CrashUtils.init("",new CrashUtils.OnCrashListener() {
            @Override
            public void onCrash(CrashUtils.CrashInfo crashInfo) {
                ClipboardUtils.copyText(crashInfo.toString());
            }
        });

        TimerUtils.init();
    }
}
