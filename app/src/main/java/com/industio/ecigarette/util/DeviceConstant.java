package com.industio.ecigarette.util;

public class DeviceConstant {
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
            "温度、口数、时间",
    };
    public static byte[] startCmd = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA, 0x00, 0x01, 0x45, 0x67, (byte) 0x89, 0x00, 0x00, 0x55};

    public static byte[] resetCmd = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA, 0x05, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x55};
    public static byte[] saveCmd = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA, 0x06, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x55};
    public static byte[] sleepCmd = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA, 0x07, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x55};

    enum CMD {
        预热温度(0x01),
        预热时长(0x02),
        恒温温度(0x03),
        恒温时长(0x04),
        休眠时长(0x05),
        发送口数(0x06);
        private byte value;

        CMD(int value) {
            this.value = (byte) value;
        }
    }

    public static byte[] sendData(CMD cmd, int para) {
        byte para1 = (byte) (para >> 16 & 0xFF);
        byte para2 = (byte) (para >> 8 & 0xFF );
        byte para3 = (byte) (para & 0xFF);
        return new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA, 0x02, cmd.value, para1, para2, para3, 0x00, 0x00, 0x55};
    }

    public static int[] sendCount(int count, int para) {
        int para1 = para > 0 ? 0x01 : 0x00;
        para = Math.abs(para);
        int para2 = para & 0xFF00 >> 8;
        int para3 = para & 0x00FF;
        return new int[]{0x55, 0xFF, 0xCE, 0xAA, 0x03, count, para1, para2, para3, 0x00, 0x00, 0x55};
    }
}
