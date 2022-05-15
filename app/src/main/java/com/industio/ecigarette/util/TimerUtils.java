package com.industio.ecigarette.util;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimerUtils {
    private static final List<iBrightnessListener> iBrightnessListeners = new ArrayList<>();
    private static final List<iTimer> iTimers = new ArrayList<>();

    public static void init() {
        ThreadUtils.executeByCachedAtFixRate(timer, 1, TimeUnit.SECONDS);
    }

    private static final ThreadUtils.SimpleTask<Void> timer = new ThreadUtils.SimpleTask<Void>() {
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
            }
            if (CacheDataUtils.getLockScreenTime() > 0) {
                CacheDataUtils.setLockScreenTime(CacheDataUtils.getLockScreenTime() - 1);
                if (CacheDataUtils.getLockScreenTime() == 0) {
                    for (iBrightnessListener iBrightnessListener : iBrightnessListeners) {
                        iBrightnessListener.brightnessListener();
                    }
                }
            }
            for (iTimer iTimer : iTimers) {
                iTimer.timer();
            }
        }
    };


    public static void addBrightnessLister(iBrightnessListener iBrightnessListener) {
        iBrightnessListeners.add(iBrightnessListener);
    }

    public static void addTimers(iTimer iTimer) {
        iTimers.add(iTimer);
    }



    public interface iBrightnessListener {
        void brightnessListener();
    }

    public interface iTimer {
        void timer();
    }
}
