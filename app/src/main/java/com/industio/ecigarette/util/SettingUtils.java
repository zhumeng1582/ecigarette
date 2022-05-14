package com.industio.ecigarette.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.industio.ecigarette.lockscreen.AdminReceiver;
import com.industio.ecigarette.ui.SettingActivity;

import java.util.concurrent.TimeUnit;

public class SettingUtils {
    public static void openWIFISettings() {
        Utils.getApp().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void setBlueTooth() {
        Utils.getApp().startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public static void setDISPLAY() {
        Utils.getApp().startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    /**
     * 调用系统锁方法实现
     */
    public static void sysLock(Context context) {
        //取得系统服务
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(context, AdminReceiver.class);

        boolean active = dpm.isAdminActive(componentName);

        if (active) {
            dpm.lockNow();
        }
    }

    //confirm:ture-直接关机，false-无弹框，直接关机
    public static void systemShutdown(Context context, boolean confirm) {
        Intent intent = new Intent("android.ido.intent.action.set.shutdown");
        intent.putExtra("confirm", confirm);
        context.sendBroadcast(intent);
    }

    public static void systemShutdown(Runnable runnable) {
        ThreadUtils.executeByCachedAtFixRate(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() {
                return null;
            }

            @Override
            public void onSuccess(Void result) {
                if (CacheDataUtils.getShoutDownTime() > 0) {
                    CacheDataUtils.setShoutDownTime(CacheDataUtils.getShoutDownTime() - 1);
                    if (CacheDataUtils.getShoutDownTime() == 0) {
                        SettingUtils.systemShutdown(Utils.getApp(), true);
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                }

            }
        }, 1, TimeUnit.SECONDS);
    }

}
