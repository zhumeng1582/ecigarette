package com.industio.ecigarette.util;

import com.blankj.utilcode.util.ArrayUtils;

import java.util.HashMap;

public class DeviceConstant {

    //发送数据头
    public static byte[] header = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA};

    //低电保护/请尽快充电,PCB过温保护,电池过温保护,短路保护,开路保护,充电过充保护,升级完成
    //接收数据暂停时间
    public static HashMap<Integer, Integer> stopTime = new HashMap<Integer, Integer>() {
        {
            put(0x01, 5);
            put(0x02, 5);
            put(0x03, 5);
            put(0x04, 5);
            put(0x05, 5);
            put(0x06, 5);
            put(0x07, 5);
            put(0x08, 0);
            put(0x09, 5);
            put(0x0A, 0);
            put(0x0B, 0);
        }
    };
    //接收数据解析
    public static HashMap<Integer, String> RECEVICE_TIPS = new HashMap<Integer, String>() {
        {
            put(0x01, "低电保护 \n请尽快充电");
            put(0x02, "过充保护");
            put(0x03, "短路保护");
            put(0x04, "开路保护");
            put(0x05, "PCB过温保护");
            put(0x06, "电池过温保护");
            put(0x07, "充电过充保护");
            put(0x08, "升级中");
            put(0x09, "升级完成");
            put(0x0A, "抽吸完成");
            put(0x0B, "预热温度");
        }
    };


    public static byte[] startCmd = getCommonCmd(new byte[]{0x00, 0x01, 0x45, 0x67, (byte) 0x89, 0x00});
    public static byte[] resetCmd = getCommonCmd(new byte[]{0x04, 0x01, 0x00, 0x00, 0x00, 0x00});
    public static byte[] saveCmd = getCommonCmd(new byte[]{0x04, 0x02, 0x00, 0x00, 0x00, 0x00});
    public static byte[] sleepCmd = getCommonCmd(new byte[]{0x04, 0x03, 0x00, 0x00, 0x00, 0x00});

    public static byte[] getCommonCmd(byte[] data) {
        return Crc16Utils.getData(ArrayUtils.add(header, data));
    }

    //发送设备参数命令
    public enum CMD {
        预热温度(0x02, 0x01),
        预热时长(0x02, 0x02),
        恒温温度(0x02, 0x03),
        恒温时长(0x02, 0x04),
        休眠时长(0x02, 0x05),
        发送口数(0x02, 0x06);
        private final byte value1;
        private final byte value2;

        CMD(int value1, int value2) {
            this.value1 = (byte) value1;
            this.value2 = (byte) value2;
        }
    }

    //发送设备参数
    public static byte[] getSendData(CMD cmd, int para) {
        byte para1 = (byte) (para >> 8 & 0xFF);
        byte para2 = (byte) (para & 0xFF);
//        byte para3 = (byte) (para & 0xFF);
        return getCommonCmd(new byte[]{cmd.value1, cmd.value2, para1, para2, 0x00, 0x00});
    }

    //发送口数
    public static byte[] sendCount(int count, int para) {
        int para1 = para > 0 ? 0x01 : 0x00;
        para = Math.abs(para);
        int para2 = para & 0xFF;
//        int para3 = para & 0xFF;
        return getCommonCmd(new byte[]{0x03, (byte) count, (byte) para1, (byte) para2, 0x00, 0x00});
    }
}
