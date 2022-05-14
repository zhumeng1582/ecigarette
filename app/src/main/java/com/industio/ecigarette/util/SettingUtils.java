package com.industio.ecigarette.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.WindowManager;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;
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

    //confirm:ture-直接关机，false-无弹框，直接关机
    public static void systemShutdown(Context context, boolean confirm) {
        Intent intent = new Intent("android.ido.intent.action.set.shutdown");
        intent.putExtra("confirm", confirm);
        context.sendBroadcast(intent);
    }



    public static void setBrightness(Context context, int currentBrightness) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.screenBrightness = currentBrightness / 255f;
        ((Activity) context).getWindow().setAttributes(lp);
        BrightnessUtils.setBrightness(currentBrightness);
    }

}
