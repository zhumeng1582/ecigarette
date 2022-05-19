package com.industio.ecigarette.bean;

import com.industio.ecigarette.ui.ParaActivity;
import com.industio.ecigarette.util.CacheDataUtils;

import java.io.Serializable;

public class DevicePara implements Serializable {
    private final int offset = 10;

    public DevicePara(int id) {
        this.id = id;
    }

    private int id;
    //初始化数值：预热温度350；预热时长20；恒温330；恒温时长220;无操作保护60；口数12；DIY口数选择到第一口位置；调整温度0℃
//    private int preheatValue = 350;
    private int preheatTimeValue = 20;
    //    private int constantTemperatureValue = 330;
    private int constantTemperatureTimeValue = 220;
    private int noOperationValue = 60;
    private int count = 12;
    private int[] temperature = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int[] getTemperature() {
        return temperature;
    }

    public void setTemperature(int[] temperature) {
        this.temperature = temperature;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPreheatValue() {
        int preheatValue;
        if (id == ParaActivity.classics) {
            preheatValue = CacheDataUtils.getClassicTemperatureValue().getPreheatValue();
        } else if (id == ParaActivity.elegant) {
            preheatValue = CacheDataUtils.getClassicTemperatureValue().getPreheatValue() - offset;
        } else {
            preheatValue = CacheDataUtils.getClassicTemperatureValue().getPreheatValue() + offset;
        }
        return dataTrim(preheatValue);

    }

    private int dataTrim(int preheatValue) {
        if (preheatValue < 300) {
            preheatValue = 300;
        }
        if (preheatValue > 400) {
            preheatValue = 400;
        }
        return preheatValue;
    }

    public void setPreheatValue(int preheatValue) {

        if (id == ParaActivity.classics) {
        } else if (id == ParaActivity.elegant) {
            preheatValue = preheatValue + offset;
        } else {
            preheatValue = preheatValue - offset;
        }

        preheatValue = dataTrim(preheatValue);
        CacheDataUtils.getClassicTemperatureValue().setPreheatValue(preheatValue);
    }

    public int getPreheatTimeValue() {
        return preheatTimeValue;
    }

    public void setPreheatTimeValue(int preheatTimeValue) {
        this.preheatTimeValue = preheatTimeValue;
    }

    public int getConstantTemperatureValue() {
        int constantTemperatureValue;
        if (id == ParaActivity.classics) {
            constantTemperatureValue = CacheDataUtils.getClassicTemperatureValue().getConstantTemperatureValue();
        } else if (id == ParaActivity.elegant) {
            constantTemperatureValue = CacheDataUtils.getClassicTemperatureValue().getConstantTemperatureValue() - offset;
        } else {
            constantTemperatureValue = CacheDataUtils.getClassicTemperatureValue().getConstantTemperatureValue() + offset;
        }

        return dataTrim(constantTemperatureValue);
    }

    public void setConstantTemperatureValue(int constantTemperatureValue) {
        if (id == ParaActivity.classics) {
        } else if (id == ParaActivity.elegant) {
            constantTemperatureValue = constantTemperatureValue + offset;
        } else {
            constantTemperatureValue = constantTemperatureValue - offset;
        }
        constantTemperatureValue = dataTrim(constantTemperatureValue);

        CacheDataUtils.getClassicTemperatureValue().setConstantTemperatureValue(constantTemperatureValue);

    }

    public int getConstantTemperatureTimeValue() {
        return constantTemperatureTimeValue;
    }

    public void setConstantTemperatureTimeValue(int constantTemperatureTimeValue) {
        this.constantTemperatureTimeValue = constantTemperatureTimeValue;
    }

    public int getNoOperationValue() {
        return noOperationValue;
    }

    public void setNoOperationValue(int noOperationValue) {
        this.noOperationValue = noOperationValue;
    }
}
