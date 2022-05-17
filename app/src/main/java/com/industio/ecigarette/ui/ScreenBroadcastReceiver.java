package com.industio.ecigarette.ui;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.DeviceConstant;

/**
 * @author master
 * @date 2018/1/23
 */

public class ScreenBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e("ScreenBroadcastReceiver","广播Action = " + action);
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e("ScreenBroadcastReceiver","锁屏");
            SerialController.getInstance().sendSync(DeviceConstant.sleepCmd);
        } else if (action.equals(Intent.ACTION_SCREEN_ON)) {
            Log.e("ScreenBroadcastReceiver","解锁");
        }else if(action.equals(Intent.ACTION_USER_PRESENT)){
            Log.e("ScreenBroadcastReceiver","开屏");
        }
    }
}
