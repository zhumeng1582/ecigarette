package com.industio.ecigarette.util;

import android.content.Intent;
import android.provider.Settings;

import com.blankj.utilcode.util.Utils;

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
}
