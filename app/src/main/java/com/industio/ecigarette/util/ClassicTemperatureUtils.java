package com.industio.ecigarette.util;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.industio.ecigarette.bean.ClassicTemperatureValue;
import com.industio.ecigarette.ui.ParaActivity;


public class ClassicTemperatureUtils {
    private static final int offset = 10;
    public static ClassicTemperatureValue classicTemperatureValue;

    public static ClassicTemperatureValue getClassicTemperatureValue() {
        if (classicTemperatureValue != null) {
            return classicTemperatureValue;
        }
        classicTemperatureValue = (ClassicTemperatureValue) CacheDiskStaticUtils.getSerializable("ClassicTemperatureValue");
        if (classicTemperatureValue != null) {
            return classicTemperatureValue;
        }
        return new ClassicTemperatureValue();
    }
    public static void initTemperatureValue() {
        classicTemperatureValue = (ClassicTemperatureValue) CacheDiskStaticUtils.getSerializable("ClassicTemperatureValue",new ClassicTemperatureValue());
    }

    public static void setPreheatValue(int id,int preheatValue) {

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
    public static void setConstantTemperatureValue(int id,int constantTemperatureValue) {
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
