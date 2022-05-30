package com.industio.ecigarette.util;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.industio.ecigarette.bean.DevicePara;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;


public class ClassicTemperatureUtils {

    public static DevicePara getDefaultDevicePara() {
        return new DevicePara();
    }

    public static DevicePara getDevicePara() {
        String name = ClassicTemperatureUtils.getCurrentTemperatureNameValue();
        DevicePara devicePara = (DevicePara) CacheDiskStaticUtils.getSerializable(name);
        if (devicePara != null) {
            return devicePara;
        }
        return getDefaultDevicePara();
    }

    public static void saveDevicePara(DevicePara devicePara) {
        String name = ClassicTemperatureUtils.getCurrentTemperatureNameValue();
        saveDevicePara(name, devicePara);
    }

    private static void saveDevicePara(String name, DevicePara devicePara) {
        CacheDiskStaticUtils.put(name, devicePara);
    }

    private static TreeSet<String> getTemperatureNameSet() {

        TreeSet<String> hashSet = (TreeSet<String>) CacheDiskStaticUtils.getSerializable("TemperatureNameSet");
        if (CollectionUtils.isEmpty(hashSet)) {
            hashSet = new TreeSet<>();
            hashSet.add(getCurrentTemperatureNameValue());
        }
        return hashSet;
    }

    public static void addTemperatureNameValue(String name) {
        TreeSet<String> hashSet = getTemperatureNameSet();
        hashSet.add(name);
        CacheDiskStaticUtils.put("TemperatureNameSet", hashSet);
    }

    public static String getCurrentTemperatureNameValue() {
        return CacheDiskStaticUtils.getString("TemperatureNameValue", "默认");
    }

    public static void setCurrentTemperatureNameValue(String name) {
        CacheDiskStaticUtils.put("TemperatureNameValue", name);
    }

    public static DevicePara getClassicTemperatureValue(String name) {
        return (DevicePara) CacheDiskStaticUtils.getSerializable(name);
    }


    public static List<String> getHashMapKeys() {
        return new ArrayList<>(getTemperatureNameSet());
    }

    public static void saveAsTemperatureValue(String name, DevicePara value) {
        addTemperatureNameValue(name);
        saveDevicePara(name, value);
    }

}
