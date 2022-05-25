package com.industio.ecigarette.util;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.industio.ecigarette.bean.ClassicTemperatureValue;
import com.industio.ecigarette.ui.ParaActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class ClassicTemperatureUtils {
    private static final int offset = 10;

    public static ClassicTemperatureValue classicTemperatureValue;

    private static HashSet<String> getTemperatureNameSet() {

        HashSet<String> hashSet = (HashSet<String>) CacheDiskStaticUtils.getSerializable("TemperatureNameSet");
        if (CollectionUtils.isEmpty(hashSet)) {
            hashSet = new HashSet<>();
            hashSet.add(getCurrentTemperatureNameValue());
        }
        return hashSet;
    }

    public static void addTemperatureNameValue(String name) {
        HashSet<String> hashSet = getTemperatureNameSet();
        hashSet.add(name);
        CacheDiskStaticUtils.put("TemperatureNameSet", hashSet);
    }

    private static String getCurrentTemperatureNameValue() {
        return CacheDiskStaticUtils.getString("TemperatureNameValue", "默认");
    }

    public static void setCurrentTemperatureNameValue(String name) {
        CacheDiskStaticUtils.put("TemperatureNameValue", name);
    }

    public static ClassicTemperatureValue getClassicTemperatureValue() {
        if (classicTemperatureValue != null) {
            return classicTemperatureValue;
        }
        classicTemperatureValue = (ClassicTemperatureValue) CacheDiskStaticUtils.getSerializable(getCurrentTemperatureNameValue());

        if (classicTemperatureValue != null) {
            return classicTemperatureValue;
        }
        classicTemperatureValue = new ClassicTemperatureValue();
        return classicTemperatureValue;
    }
    public static ClassicTemperatureValue getClassicTemperatureValue(String name) {
        return (ClassicTemperatureValue) CacheDiskStaticUtils.getSerializable(name);
    }

    public static void clearTemperatureValue() {
        classicTemperatureValue = null;
    }

    public static void resetTemperatureValue() {
        classicTemperatureValue = new ClassicTemperatureValue();
    }

    public static List<String> getHashMapKeys() {
        return new ArrayList<>(getTemperatureNameSet());
    }

    public static void saveAsTemperatureValue(String name, ClassicTemperatureValue value) {
        addTemperatureNameValue(name);
        CacheDiskStaticUtils.put(name, value);
    }

    public static void saveTemperatureValue() {
        CacheDiskStaticUtils.put(getCurrentTemperatureNameValue(), classicTemperatureValue);
    }

    public static void setPreheatValue(int id, int preheatValue) {

        if (id == ParaActivity.classics) {
        } else if (id == ParaActivity.elegant) {
            preheatValue = preheatValue + offset;
        } else {
            preheatValue = preheatValue - offset;
        }

        preheatValue = dataTrim(preheatValue);
        getClassicTemperatureValue().setPreheatValue(preheatValue);
    }

    public static int getPreheatValue(int id) {
        int preheatValue = getClassicTemperatureValue().getPreheatValue();
        if (id == ParaActivity.classics) {

        } else if (id == ParaActivity.elegant) {
            preheatValue = preheatValue - offset;
        } else {
            preheatValue = preheatValue + offset;
        }
        return dataTrim(preheatValue);

    }

    private static int dataTrim(int preheatValue) {
        if (preheatValue < 300) {
            preheatValue = 300;
        }
        if (preheatValue > 400) {
            preheatValue = 400;
        }
        return preheatValue;
    }

    public static int getConstantTemperatureValue(int id) {
        int constantTemperatureValue = getClassicTemperatureValue().getConstantTemperatureValue();
        if (id == ParaActivity.classics) {

        } else if (id == ParaActivity.elegant) {
            constantTemperatureValue = constantTemperatureValue - offset;
        } else {
            constantTemperatureValue = constantTemperatureValue + offset;
        }

        return dataTrim(constantTemperatureValue);
    }

    public static void setConstantTemperatureValue(int id, int constantTemperatureValue) {
        if (id == ParaActivity.classics) {
        } else if (id == ParaActivity.elegant) {
            constantTemperatureValue = constantTemperatureValue + offset;
        } else {
            constantTemperatureValue = constantTemperatureValue - offset;
        }
        constantTemperatureValue = dataTrim(constantTemperatureValue);

        getClassicTemperatureValue().setConstantTemperatureValue(constantTemperatureValue);

    }

}
