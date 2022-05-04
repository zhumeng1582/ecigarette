package com.industio.ecigarette.util;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;

public class BluetoothUtils {


    /**
     * 检测设备是否支持蓝牙
     */
    public static boolean checkBlueToothEnable() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            ToastUtils.showShort("该设备不支持蓝牙");
            return false;
        } else {
            ToastUtils.showShort("该设备能支持蓝牙");
            return true;
        }
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

}