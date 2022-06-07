package com.industio.ecigarette.bean;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.TimeUtils;

import java.util.ArrayList;

public class CigaretteData {
    private long time;
    private int count;
    private int timeLong;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getTimeLong() {
        return timeLong;
    }

    public void setTimeLong(int timeLong) {
        this.timeLong = timeLong;
    }


    public static ArrayList<CigaretteData> getCigaretteDataSet() {

        ArrayList<CigaretteData> hashSet = (ArrayList<CigaretteData>) CacheDiskStaticUtils.getSerializable("CigaretteData");
        if (hashSet == null) {
            hashSet = new ArrayList<>();
        }
        return hashSet;
    }

    public static void add(CigaretteData cigaretteData) {
        ArrayList<CigaretteData> hashSet = getCigaretteDataSet();
        ArrayList<CigaretteData> newSet = new ArrayList<>();

        for (CigaretteData data : hashSet) {
            if (data.getTime() > TimeUtils.getNowMills() - 86400000 * 7) {
                newSet.add(data);
            }
        }

        newSet.add(cigaretteData);
        CacheDiskStaticUtils.put("CigaretteData", newSet);
    }
}
