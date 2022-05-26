package com.industio.ecigarette;

import android.app.Application;
import android.util.Log;

import com.blankj.utilcode.util.ClipboardUtils;
import com.blankj.utilcode.util.CrashUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.sdwfqin.cbt.CbtManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashUtils.init("",new CrashUtils.OnCrashListener() {
            @Override
            public void onCrash(CrashUtils.CrashInfo crashInfo) {
                ClipboardUtils.copyText(crashInfo.toString());
            }
        });
        CbtManager
                .getInstance()
                .init(this)
                .enableLog(true);
        TimerUtils.init();
    }
}
