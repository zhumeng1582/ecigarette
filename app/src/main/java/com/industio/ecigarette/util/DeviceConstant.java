package com.industio.ecigarette.util;

import com.blankj.utilcode.util.ArrayUtils;

public class DeviceConstant {
    public static byte[] header = new byte[]{0x55, (byte) 0xFF, (byte) 0xCE, (byte) 0xAA};

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
    public static byte[] resetCmd = getCommonCmd(new byte[]{0x04,0x01, 0x00, 0x00, 0x00, 0x00});
    public static byte[] saveCmd = getCommonCmd(new byte[]{0x04,0x02, 0x00, 0x00, 0x00, 0x00});
    public static byte[] sleepCmd = getCommonCmd(new byte[]{0x04,0x03, 0x00, 0x00, 0x00, 0x00});

    public static byte[] getCommonCmd(byte[] data) {
        return Crc16Utils.getData(ArrayUtils.add(header, data));
    }

    public enum CMD {
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

    public static byte[] getData(CMD cmd, int para) {
        byte para1 = (byte) (para >> 16 & 0xFF);
        byte para2 = (byte) (para >> 8 & 0xFF);
        byte para3 = (byte) (para & 0xFF);
        return getCommonCmd(new byte[]{0x02, cmd.value, para1, para2, para3, 0x00});
    }

    public static byte[] sendCount(int count, int para) {
        int para1 = para > 0 ? 0x01 : 0x00;
        para = Math.abs(para);
        int para2 = para >> 8 & 0xFF;
        int para3 = para & 0xFF;
        return getCommonCmd(new byte[]{0x03, (byte) count, (byte) para1, (byte) para2, (byte) para3, 0x00});
    }
}
