package com.industio.ecigarette.bean;

import android.util.Log;

import com.blankj.utilcode.util.CacheDiskStaticUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.industio.ecigarette.util.DateUtils;

import java.io.Serializable;
import java.util.ArrayList;

public class CigaretteData implements Serializable {
    public CigaretteData(long time, int count, int timeLong) {
        this.time = time;
        this.timeStr = TimeUtils.millis2String(time);
        this.count = count;
        this.timeLong = timeLong;
    }

    private long time;
    private String timeStr;
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

        ArrayList<CigaretteData> hashSet = (ArrayList<CigaretteData>) CacheDiskStaticUtils.getSerializable("CigaretteDataList");
        ArrayList<CigaretteData> newSet = new ArrayList<>();
        if (hashSet == null) {
            hashSet = new ArrayList<>();
        }
        for (CigaretteData data : hashSet) {
            if (data.getTime() > TimeUtils.getNowMills() - 86400000 * 7) {
                newSet.add(data);
                Log.d("CigaretteDataSet", "时间没有过期，数据有效：" + GsonUtils.toJson(data));
            } else {
                Log.d("CigaretteDataSet", "时间过期，删除数据：" + GsonUtils.toJson(data));
            }
        }
        Log.d("CigaretteDataSet", "获取缓存数据：" + GsonUtils.toJson(hashSet));

        return newSet;
    }

    public static void add(CigaretteData cigaretteData) {
        ArrayList<CigaretteData> hashSet = getCigaretteDataSet();
        hashSet.add(cigaretteData);
        Log.d("CigaretteDataSet", "抽吸完成，保存数据：" + GsonUtils.toJson(cigaretteData));
        CacheDiskStaticUtils.put("CigaretteDataList", hashSet);
    }
}
