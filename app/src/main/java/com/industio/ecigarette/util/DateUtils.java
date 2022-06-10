package com.industio.ecigarette.util;

import android.util.Pair;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.industio.ecigarette.bean.CigaretteData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {
    /**
     * 时间的显示方式，这个地方按自己的需求来修改，
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdfy = new SimpleDateFormat("yyyy");
    private static SimpleDateFormat sdfyM = new SimpleDateFormat("yyyy-MM");
    private static SimpleDateFormat sdfyH = new SimpleDateFormat("HH");
    private static SimpleDateFormat sdfyDD = new SimpleDateFormat("dd");

    public static List<Pair<Integer, Integer>> getDataDay(ArrayList<CigaretteData> list) {
        int[] dataDay = new int[12];
        for (CigaretteData cigaretteData : list) {
            if (TimeUtils.isToday(cigaretteData.getTime())) {
                String hourStr = TimeUtils.millis2String(cigaretteData.getTime(), sdfyH);
                int hour = Integer.parseInt(hourStr);
                dataDay[hour] = dataDay[hour] + 1;
            }
        }
        List<Pair<Integer, Integer>> dataDayList = new ArrayList<>();
        for (int i = 0; i < dataDay.length; i++) {
            dataDayList.add(new Pair<>(i * 2, dataDay[i]));
        }

        return dataDayList;
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

    public static List<Pair<Integer, Integer>> getDataWeek(ArrayList<CigaretteData> list) {
        int[] dataDay = new int[7];
        for (CigaretteData cigaretteData : list) {
            for (int i = 0; i < 6; i++) {
                if (TimeUtils.isToday(cigaretteData.getTime() + 86400000 * i)) {
                    dataDay[6 - i] = dataDay[6 - i] + 1;
                }
            }
        }
        List<Pair<Integer, Integer>> dataDayList = new ArrayList<>();
        for (int i = 0; i < dataDay.length; i++) {
            long time = TimeUtils.getNowMills() - (6 - i) * 86400000;
            String DD = TimeUtils.millis2String(time, sdfyDD);

            dataDayList.add(new Pair<>(Integer.valueOf(DD), dataDay[i]));
        }

        return dataDayList;
//        return new int[]{1,2,3,4,5,6,7};
    }
}