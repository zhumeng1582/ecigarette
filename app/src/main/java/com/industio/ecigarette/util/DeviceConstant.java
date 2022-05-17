package com.industio.ecigarette.util;

import com.blankj.utilcode.util.ArrayUtils;

public class DeviceConstant {

    //发送数据头
    public static byte[] header = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA};

    //低电保护/请尽快充电,PCB过温保护,电池过温保护,短路保护,开路保护,充电过充保护,升级完成
    public static int[] stopTime = new int[]{5,5,5,5,5,5,5,0,5,0,0};
    //接收数据解析
    public static String[] RECEVICE_TIPS = new String[]{"",
            "低电保护 \n请尽快充电",
            "过充保护",
            "短路保护",
            "开路保护",
            "PCB过温保护",
            "电池过温保护",
            "充电过充保护",
            "升级中",
            "升级完成",
            "抽吸完成",
            "预热温度",
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
