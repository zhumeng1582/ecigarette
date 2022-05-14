package com.industio.ecigarette.util;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.industio.ecigarette.bean.DevicePara;

public class CacheDataUtils {
    public static DevicePara getDefaultDevicePara(int id) {
        return new DevicePara(id);
    }

    public static DevicePara getDevicePara(int id) {
        DevicePara devicePara = (DevicePara) CacheDiskStaticUtils.getSerializable("DevicePara" + id);
        if (devicePara != null) {
            return devicePara;
        }
        return getDefaultDevicePara(id);
    }

    public static void saveDevicePara(DevicePara devicePara) {
        CacheDiskStaticUtils.put("DevicePara" + devicePara.getId(), devicePara);
    }

    private static int shoutDownTime = 0;
    private static int lockScreenTime = 0;

    public static void setShoutDownTime(int time) {
        shoutDownTime = time;
    }

    public static int getShoutDownTime() {
        return shoutDownTime;
    }

    public static void setLockScreenTime(int time) {
        lockScreenTime = time;
    }

    public static int getLockScreenTime() {
        return lockScreenTime;
    }

    public static String getShoutDownTimeText() {
        if (shoutDownTime <= 0) {
            return "";
        }
        return transFom(shoutDownTime);
    }

    public static String getLockScreenTimeText() {
        if (lockScreenTime <= 0) {
            return "";
        }
        return "" + lockScreenTime + "秒";
    }

    /**
     * 秒转为时分秒  7200 -》 02:00:00
     *
     * @param time
     * @return
     */
    public static String transFom(int time) {
        int hh = time / 3600;
        int mm = (time % 3600) / 60;
        int ss = (time % 3600) % 60;
        return (hh < 10 ? ("0" + hh) : hh) + ":" + (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
    }
}
