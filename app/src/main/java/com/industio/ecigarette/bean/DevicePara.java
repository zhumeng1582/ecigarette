package com.industio.ecigarette.bean;

import java.io.Serializable;

public class DevicePara implements Serializable {
    public DevicePara(int id) {
        this.id = id;
    }

    private int id;
    private int preheatValue = 300;
    private int preheatTimeValue = 15;
    private int constantTemperatureValue = 300;
    private int constantTemperatureTimeValue = 150;
    private int noOperationValue = 60;

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
