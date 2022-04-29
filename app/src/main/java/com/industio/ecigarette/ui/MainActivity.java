package com.industio.ecigarette.ui;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.Strings;
import com.industio.ecigarette.util.WifiUtils;
import com.industio.ecigarette.view.ViewAnimate;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnPlayMusic,
                binding.btnMode,
                binding.btnSetPara,
        }, this);

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1.getRawY() < 300 && distanceY < 0) {
                    ViewAnimate.topOpen(binding.topView, (int) e2.getRawY());
                } else if (distanceY > 0) {
                    closeController();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        closeController();

        initTop();
    }

    private void initTop() {
        PermissionUtils.permission(Manifest.permission.CHANGE_WIFI_STATE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                LogUtils.dTag("PermissionUtils", "-------->onGranted");
            }

            @Override
            public void onDenied() {
                LogUtils.dTag("PermissionUtils", "-------->onDenied");

            }
        }).request();
        binding.toggleWIFI.setChecked(NetworkUtils.getWifiEnabled());
        binding.toggleWIFI.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                NetworkUtils.setWifiEnabled(b);
            }
        });
        binding.toggleBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                PermissionUtils.permission(Manifest.permission.BLUETOOTH_CONNECT).callback(new PermissionUtils.SimpleCallback() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onGranted() {
                        if (b) {
                            BluetoothUtils.offBlueTooth();
                        } else {
                            BluetoothUtils.onBlueTooth();
                        }
                    }

                    @Override
                    public void onDenied() {
                        LogUtils.dTag("PermissionUtils", "-------->onDenied");

                    }
                }).request();

            }
        });

        binding.seekBar.setMin(0);
        binding.seekBar.setMax(255);
        binding.seekBar.setProgress(BrightnessUtils.getBrightness());
        BrightnessUtils.setAutoBrightnessEnabled(false);

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                BrightnessUtils.setBrightness(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
//        WifiUtils.onWifiOpenDoing();
    }

    private void closeController() {
        ViewAnimate.animateClose(binding.topView, new ViewAnimate.AnimaionLoadEndListener() {
            @Override
            public void onLoadEnd() {
            }
        });
    }

    //2.让手势识别器 工作起来
    // 当activity被触摸的时候调用的方法.
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onClick(View view) {
        if (view == binding.btnPlayMusic) {
            //qq音乐
            String qqmusic = "com.tencent.qqmusic";
            //网易云音乐
            String cloudmusic = "com.netease.cloudmusic";
            //咪咕
            String migu = "cmccwm.mobilemusic";
            if (AppUtils.isAppInstalled(qqmusic)) {
                AppUtils.launchApp(qqmusic);
            } else if (AppUtils.isAppInstalled(cloudmusic)) {
                AppUtils.launchApp(cloudmusic);
            } else if (AppUtils.isAppInstalled(migu)) {
                AppUtils.launchApp(migu);
            }
        } else if (view == binding.btnMode) {
            startActivity(new Intent(MainActivity.this, ParaActivity.class));
        } else if (view == binding.btnSetPara) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));

        }
    }

    private void registerSerial() {
        SerialController.getInstance().registerSerialReadListener((buf, len) -> {
            switch (buf[4]) {
                case 0x00:
                    break;//显示主界面;
                case 0x01:
                    if (buf[5] <= 0x0C) {
                        binding.textAlarm.setText(Strings.RECEVICE_TIPS[buf[5]]);
                    }
                    break;
                case 0x10:
                    //相应的电池符号;
                    //buf[5]（电量）
                    break;
                default:
                    break;
            }
        });
    }

}