package com.industio.ecigarette.util;

import com.blankj.utilcode.util.TimeUtils;
import com.industio.ecigarette.bean.CigaretteData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DateUtils {
    /**
     * 时间的显示方式，这个地方按自己的需求来修改，
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdfyM = new SimpleDateFormat("yyyy-MM");
    private static SimpleDateFormat sdfyH = new SimpleDateFormat("HH");

    public static int[] getDataDay(ArrayList<CigaretteData> list) {
        int[] dataDay = new int[12];
        for (CigaretteData cigaretteData : list) {
            if (TimeUtils.isToday(cigaretteData.getTime())) {
                String hourStr = TimeUtils.millis2String(cigaretteData.getTime(), sdfyH);
                int hour = Integer.parseInt(hourStr);
                dataDay[hour] = dataDay[hour] + 1;
            }
        }

        return dataDay;
//        return new int[]{1,2,3,4,5,6,7,7,8,9,0,1};
    }

    public static int getCountDay(ArrayList<CigaretteData> list) {
        int dataDay = 0;
        for (CigaretteData cigaretteData : list) {
            if (TimeUtils.isToday(cigaretteData.getTime())) {
                dataDay++;
            }
        }

        return dataDay;
    }

    public static int getTimeLongDay(ArrayList<CigaretteData> list) {
        int dataDay = 0;
        for (CigaretteData cigaretteData : list) {
            if (TimeUtils.isToday(cigaretteData.getTime())) {
                dataDay = dataDay + cigaretteData.getTimeLong();
            }
        }

        return dataDay;
    }

    public static int[] getDataWeek(ArrayList<CigaretteData> list) {
        int[] dataDay = new int[7];
        for (CigaretteData cigaretteData : list) {
            for (int i = 0; i < 6; i++) {
                if (TimeUtils.isToday(cigaretteData.getTime() + 86400000 * i)) {
                    dataDay[6 - i] = dataDay[6 - i] + 1;
                }
            }
        }

        return dataDay;
//        return new int[]{1,2,3,4,5,6,7};
    }
}