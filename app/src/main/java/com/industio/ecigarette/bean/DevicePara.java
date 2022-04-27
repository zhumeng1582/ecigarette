package com.industio.ecigarette.bean;

import java.io.Serializable;

public class DevicePara implements Serializable {
    public DevicePara(int id) {
        this.id = id;
    }

    private int id;
    //初始化数值：预热温度350；预热时长20；恒温330；恒温时长220;无操作保护60；口数12；DIY口数选择到第一口位置；调整温度0℃
    private int preheatValue = 350;
    private int preheatTimeValue = 20;
    private int constantTemperatureValue = 330;
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
        return preheatValue;
    }

    public void setPreheatValue(int preheatValue) {
        this.preheatValue = preheatValue;
    }

    public int getPreheatTimeValue() {
        return preheatTimeValue;
    }

    public void setPreheatTimeValue(int preheatTimeValue) {
        this.preheatTimeValue = preheatTimeValue;
    }

    public int getConstantTemperatureValue() {
        return constantTemperatureValue;
    }

    public void setConstantTemperatureValue(int constantTemperatureValue) {
        this.constantTemperatureValue = constantTemperatureValue;
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
