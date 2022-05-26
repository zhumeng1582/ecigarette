package com.industio.ecigarette.util;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothUtils {


    /**
     * 检测设备是否支持蓝牙
     */
    public static boolean checkBlueToothEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null;
    }


    /**
     * 打开蓝牙
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static void onBlueTooth() {
        if (checkBlueToothEnable()) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter.isEnabled()) {
                ToastUtils.showShort("蓝牙已打开，不用在点了~");
            } else {
                bluetoothAdapter.enable();
            }
        }
    }

    /**
     * 关闭蓝牙
     */
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static void offBlueTooth() {
        if (checkBlueToothEnable()) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter.isEnabled()) {
                bluetoothAdapter.disable();
            } else {
                ToastUtils.showShort("蓝牙已关闭，不用在点了~");
            }
        }
    }

    /**
     * 关闭蓝牙
     */
    public static boolean isBlueOn() {
        if (checkBlueToothEnable()) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return bluetoothAdapter.isEnabled();
        }
        return false;
    }

    /**
     * 获取蓝牙状态
     */
    public static int getState() {
        if (checkBlueToothEnable()) {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return bluetoothAdapter.getState();
        }
        return BluetoothAdapter.STATE_OFF;
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public static String getConnectedBtDevice() {
        //得到已匹配的蓝牙设备列表
        Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (CollectionUtils.isNotEmpty(bondedDevices)) {
            for (BluetoothDevice bondedDevice : bondedDevices) {
                try {
                    //使用反射调用被隐藏的方法
                    Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                    isConnectedMethod.setAccessible(true);
                    boolean isConnected = (boolean) isConnectedMethod.invoke(bondedDevice, (Object[]) null);
                    Log.e("123", "isConnected：" + isConnected);
                    if (isConnected) {
                        return bondedDevice.getName();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
