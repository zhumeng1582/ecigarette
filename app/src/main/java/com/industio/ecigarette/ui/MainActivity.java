package com.industio.ecigarette.ui;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.bean.CigaretteData;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.ChargeUtils;
import com.industio.ecigarette.util.Crc16Utils;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.view.ViewAnimate;


public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private int showTimeCount = 0;
    private int clearTimeCount = 5;
    private int totalTime;
    private int endTime;
    private int totalCount;
    private int endCount;
    public static long currentTime = 0;


    ScreenBroadcastReceiver screenBroadcastReceiver = new ScreenBroadcastReceiver();

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


        closeController();

        registerSerial();

        registerReceiverScreenBroadcast();
        findViewById(R.id.llLock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeController();
                binding.textLock.setVisibility(View.VISIBLE);
            }
        });
        initData();
    }

    @Override
    public void charge(boolean isCharge, int power) {
        if (power <= 5) {
            binding.included.batteryView.setPower(power * 20);
        }
        if (isCharge) {
            binding.included.imageChange.setVisibility(View.VISIBLE);
        } else {
            binding.included.imageChange.setVisibility(View.GONE);
        }
    }

    @Override
    public void timer() {
        if (showTimeCount > 0) {
            showTimeCount--;
        }
        if (clearTimeCount >= 0) {
            clearTimeCount--;
        }
        if (clearTimeCount <= 0) {
            binding.textAlarm.setText("");
        }

        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            binding.included.iconHomeWifi.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeWifi.setVisibility(View.GONE);
        }
        if (BluetoothUtils.getState() == BluetoothAdapter.STATE_CONNECTED) {
            binding.included.iconHomeBluetooth.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeBluetooth.setVisibility(View.GONE);
        }
    }

    @Override
    public View getLock() {
        return binding.textLock;
    }

    private void registerReceiverScreenBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
//            filter.addAction(Intent.ACTION_SCREEN_ON);
//            filter.addAction(Intent.ACTION_USER_PRESENT);
        getApplicationContext().registerReceiver(screenBroadcastReceiver, filter);
    }


    @Override
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (e1.getRawY() < 300 && distanceY < 0) {
            binding.topView.onStateDoing();
            ViewAnimate.topOpen(binding.topView, (int) e2.getRawY());
        } else if (distanceY > 0) {
            closeController();
        }
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        closeController();
    }


    private void permission() {
        PermissionUtils.permission(Manifest.permission.BLUETOOTH, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
            }

            @Override
            public void onDenied() {

            }
        }).request();
    }

    private void closeController() {
        binding.topView.stop();
        ViewAnimate.animateClose(binding.topView, new ViewAnimate.AnimaionLoadEndListener() {
            @Override
            public void onLoadEnd() {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        permission();
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
            ParaActivity.newInstance(MainActivity.this);

        } else if (view == binding.btnSetPara) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
    }

    private void registerSerial() {
        SerialController.getInstance().registerSerialReadListener((buf, len) -> {

            byte[] subBuf = ArrayUtils.subArray(buf, 0, len);

            if (showTimeCount > 0) {
                return;
            }

            if (Crc16Utils.dataError(subBuf)) return;

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataAnalysis(subBuf);
                }
            });

        });
    }

    private void dataAnalysis(byte[] buf) {
        if (ArrayUtils.equals(buf, DeviceConstant.startCmd)) {
            SerialController.getInstance().sendSync(DeviceConstant.startCmd);
            return;
        }
        switch (buf[4] & 0xff) {
            case 0x00:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;//显示主界面;
            case 0x01:
                int key = buf[5] & 0xff;
                if (key == 0x0A && currentTime != 0) {
                    Log.d("CigaretteDataSet", "抽吸完成，串口返回原始数据:" + Crc16Utils.byteTo16String(buf));

                    CigaretteData.add(new CigaretteData(currentTime, totalCount - endCount, totalTime - endTime));
                    initData();
                }

                if (DeviceConstant.stopTime.containsKey(key)) {
                    showTimeCount = DeviceConstant.stopTime.get(key);
                }

                if (DeviceConstant.RECEVICE_TIPS.containsKey(key)) {
                    String text = DeviceConstant.RECEVICE_TIPS.get(key);

                    if (key <= 0x0A) {
                        setAlarmText(text);
                    } else if (key == 0x0B) {
                        int temp = (((buf[6] & 0xff) << 8) & 0xff00) | (buf[7] & 0xff);
                        setAlarmText(text + "\n" + temp + "℃");
                        if (!isFront) {
                            startActivity(new Intent(MainActivity.this, MainActivity.class));
                        }
                    }
                } else {
                    setAlarmText("错误数据：" + buf[5]);
                }

                break;
            case 0x0C:

                int temp1 = (((buf[5] & 0xff) << 8) & 0xff00) | (buf[6] & 0xff);
                int temp2 = buf[7] & 0xff;
                int temp3 = (((buf[8] & 0xff) << 8) & 0xff00) | (buf[9] & 0xff);
                String text = "温度：" + temp1 + "℃" + "\n" + "口数：" + temp2 + "\n" + "时间：" + temp3 + "s\n";
                if (currentTime == 0) {
                    totalTime = temp3;
                    totalCount = temp2;
                    endTime = 0;
                    endCount = 0;
                    currentTime = TimeUtils.getNowMills();
                } else {
                    endTime = temp3;
                    endCount = temp2;
                }

                setAlarmText(text);
                break;
            case 0x04:
                if (buf[5] == 0x01) {
                    ToastUtils.showShort("复位数据成功");
                } else if (buf[5] == 0x02) {
                    ToastUtils.showShort("保存数据成功");
                }
                break;
            case 0x10:
                ChargeUtils.notifyCharges(buf[7] != 0, buf[6] & 0xff);
                break;
            default:
                break;
        }
    }

    private void initData() {
        totalTime = 0;
        endTime = 0;
        totalCount = 0;
        endCount = 0;
        currentTime = 0;
    }

    private void setAlarmText(String text) {
        clearTimeCount = 5;
        binding.textAlarm.setText(text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerReceiverScreenBroadcast();
    }
}