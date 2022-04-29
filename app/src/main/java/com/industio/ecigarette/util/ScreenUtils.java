package com.industio.ecigarette.util;

import android.os.PowerManager;
import android.util.Log;

public class ScreenUtils {
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public void turnOnScreen() {
        // turn on screen
        Log.v("ProximityActivity", "ON!");
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        mWakeLock.acquire();
        mWakeLock.release();
    }
}
