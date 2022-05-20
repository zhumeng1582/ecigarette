package com.industio.ecigarette.util;

import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.industio.ecigarette.serialcontroller.SerialController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimerUtils {
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
                    SerialController.getInstance().sendSync(DeviceConstant.shoutDownCmd);
                    SettingUtils.systemShutdown(Utils.getApp(), false);
                }
            }
            for (iTimer iTimer : iTimers) {
                iTimer.timer();
            }
        }
    };


    public static void addTimers(iTimer iTimer) {
        iTimers.add(iTimer);
    }


    public interface iTimer {
        void timer();
    }
}
