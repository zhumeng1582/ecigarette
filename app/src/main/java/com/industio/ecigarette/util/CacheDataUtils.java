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
}
