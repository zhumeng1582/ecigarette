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
     * wifi打开后执行某个操作
     */

    public static void onWifiOpenDoing() {
        ThreadUtils.executeByFixedAtFixRate(1, new ThreadUtils.Task<Object>() {
            @Override
            public Object doInBackground() throws Throwable {
                return null;
            }

            @Override
            public void onSuccess(Object result) {
                isWifiOpened();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onFail(Throwable t) {

            }
        }, 1, TimeUnit.SECONDS);


    }

    /**
     * 判断wifi是否已经打开
     *
     * @returntrue：已打开、false:未打开
     */

    public static boolean isWifiOpened() {
        WifiManager wifiManager = (WifiManager) Utils.getApp().getSystemService(WIFI_SERVICE);
        int status = wifiManager.getWifiState();

        if (status == WifiManager.WIFI_STATE_ENABLED) {
            //wifi已经打开
            Log.d("", "-------->isWifiOpened = " + true);
            return true;

        } else {
            Log.d("", "-------->isWifiOpened = " + false);

            return false;

        }
    }
}
