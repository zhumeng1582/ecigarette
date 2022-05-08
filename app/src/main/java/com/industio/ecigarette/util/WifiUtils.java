package com.industio.ecigarette.util;

import static android.content.Context.WIFI_SERVICE;

import android.net.wifi.WifiManager;
import android.util.Log;
import android.util.TimeUtils;

import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.concurrent.TimeUnit;

public class WifiUtils {

    /**
     * 判断wifi是否已经打开
     *
     */

    public static int getWifiState() {
        WifiManager wifiManager = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        return wifiManager.getWifiState();
    }
}
