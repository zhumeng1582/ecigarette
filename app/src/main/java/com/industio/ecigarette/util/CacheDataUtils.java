package com.industio.ecigarette.util;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.industio.ecigarette.bean.DevicePara;

public class CacheDataUtils {

    public static DevicePara getDefaultDevicePara(int id) {
        return new DevicePara(id);
    }


    public static DevicePara getDevicePara(int id) {
        DevicePara devicePara = (DevicePara) CacheDiskStaticUtils.getSerializable("KeyDevicePara" + id);
        if (devicePara != null) {
            return devicePara;
        }
        return getDefaultDevicePara(id);
    }

    public static void saveDevicePara(DevicePara devicePara) {
        CacheDiskStaticUtils.put("KeyDevicePara" + devicePara.getId(), devicePara);
        CacheDiskStaticUtils.put("ClassicTemperatureValue", ClassicTemperatureUtils.getClassicTemperatureValue());

    }

    public static void setLockScreenSwitch(boolean b) {
        CacheDiskStaticUtils.put("lockScreenSwitch", b);
    }

    public static boolean getLockScreenSwitch() {
        return (boolean) CacheDiskStaticUtils.getSerializable("lockScreenSwitch", true);
    }

    private static int shoutDownTime = 0;

    public static void setShoutDownTime(int time) {
        shoutDownTime = time;
    }

    public static int getShoutDownTime() {
        return shoutDownTime;
    }

    public static void setLockScreenTime(int time) {
        CacheDiskStaticUtils.put("LockScreenTime",time);
    }

    public static int getLockScreenTime() {
        return (int) CacheDiskStaticUtils.getSerializable("LockScreenTime",0);
    }

    public static String getShoutDownTimeText() {
        if (shoutDownTime <= 0) {
            return "";
        }
        return transFom(shoutDownTime);
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
