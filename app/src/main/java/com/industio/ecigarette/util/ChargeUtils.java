package com.industio.ecigarette.util;


import java.util.ArrayList;
import java.util.List;

public class ChargeUtils {
    private static boolean isCharge;
    private static int power;
    private static final List<iCharge> iCharge = new ArrayList<>();

    public static void addCharges(iCharge iTimer) {
        iCharge.add(iTimer);
        iTimer.charge(isCharge, power);
    }
    public static void removeCharges(iCharge iTimer) {
        iCharge.remove(iTimer);
    }

    public static void notifyCharges(boolean isCharge, int power) {
        ChargeUtils.isCharge = isCharge;
        ChargeUtils.power = power;
        for (ChargeUtils.iCharge charge : iCharge) {
            charge.charge(isCharge, power);
        }
    }

    public interface iCharge {
        void charge(boolean isCharge, int power);
    }
}
