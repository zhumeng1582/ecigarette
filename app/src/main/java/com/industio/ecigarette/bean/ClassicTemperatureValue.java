package com.industio.ecigarette.bean;

import java.io.Serializable;

public class ClassicTemperatureValue implements Serializable {
    private int preheatValue = 350;
    private int constantTemperatureValue = 330;

    public int getPreheatValue() {
        return preheatValue;
    }

    public void setPreheatValue(int preheatValue) {
        this.preheatValue = preheatValue;
    }

    public int getConstantTemperatureValue() {
        return constantTemperatureValue;
    }

    public void setConstantTemperatureValue(int constantTemperatureValue) {
        this.constantTemperatureValue = constantTemperatureValue;
    }
}
