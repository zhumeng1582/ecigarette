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
import com.industio.ecigarette.view.battery.BatteryView;

import java.sql.Time;


public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    protected boolean isNeedReturn = false;

    private int showTimeCount = 0;
    private int clearTimeCount = 5;
    private int totalTime;
    private int endTime;
    private int totalCount;
    private int endCount;
    public long currentTime = 0;
    public static boolean disableAllClick = false;
    private long lastTime = 0;

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

        permission();
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
    public void onClick(View view) {
        if (view == binding.btnPlayMusic) {
            //qq??????
            String qqmusic = "com.tencent.qqmusic";
            //???????????????
            String cloudmusic = "com.netease.cloudmusic";
            //??????
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

            if (Crc16Utils.dataError(subBuf))
                return;
            if (((subBuf[4] & 0xff) == 0x01) && ((subBuf[5] & 0xff) == 0x0A)) {
                Log.d("CigaretteDataSet", "??????????????????:" + Crc16Utils.byteTo16String(subBuf));
            }
            dataAnalysis(subBuf);

        });
    }


    private synchronized void dataAnalysis(byte[] buf) {
        if (ArrayUtils.equals(buf, DeviceConstant.startCmd)) {
            SerialController.getInstance().sendSync(DeviceConstant.startCmd);
            return;
        }
        switch (buf[4] & 0xff) {
            case 0x00:
                Log.d("ReturnMainActivity", "???????????????--------->");
                returnMainActivity();
                break;//???????????????;
            case 0x01:
                int key = buf[5] & 0xff;
                if (TimeUtils.getNowMills() - lastTime > 2000) {
                    lastTime = TimeUtils.getNowMills();
                    if (key == 0x0A && currentTime != 0) {
                        Log.d("CigaretteDataSet", "???????????????????????????????????????:" + Crc16Utils.byteTo16String(buf));

                        CigaretteData.add(new CigaretteData(currentTime, Crc16Utils.byteTo16String(buf), totalCount - endCount, totalTime - endTime));
                        initData();
                    }
                }

                if (DeviceConstant.stopTime.containsKey(key)) {
                    showTimeCount = DeviceConstant.stopTime.get(key);
                }

                if (DeviceConstant.RECEVICE_TIPS.containsKey(key)) {
                    String text = DeviceConstant.RECEVICE_TIPS.get(key);

                    if (key < 0x0A) {
                        setAlarmText(text);
                    } else if (key == 0x0A) {
                        isNeedReturn = true;
                        disableAllClick = false;
                        setAlarmText(text);
                    } else if (key == 0x0B) {
                        disableAllClick = true;
                        int temp = (((buf[6] & 0xff) << 8) & 0xff00) | (buf[7] & 0xff);
                        setAlarmText(text + "\n" + temp + "???");
                        if (isNeedReturn && !isFront) {
                            isNeedReturn = false;
                            Log.d("ReturnMainActivity", text + "\n" + temp + "???");
                            returnMainActivity();
                        }
                    }
                } else {
                    setAlarmText("???????????????" + buf[5]);
                }

                break;
            case 0x0C:

                int temp1 = (((buf[5] & 0xff) << 8) & 0xff00) | (buf[6] & 0xff);
                int temp2 = buf[7] & 0xff;
                int temp3 = (((buf[8] & 0xff) << 8) & 0xff00) | (buf[9] & 0xff);
                String text = "?????????" + temp1 + "???" + "\n" + "?????????" + temp2 + "\n" + "?????????" + temp3 + "s\n";
                if (currentTime == 0) {
                    totalTime = temp3;
                    totalCount = temp2;
                    endTime = 0;
                    endCount = 0;
                    disableAllClick = true;
                    currentTime = TimeUtils.getNowMills();
                } else {
                    endTime = temp3;
                    endCount = temp2;
                }

                setAlarmText(text);
                break;
            case 0x04:
                if (buf[5] == 0x01) {
                    showShort("??????????????????");
                } else if (buf[5] == 0x02) {
                    showShort("??????????????????");
                }
                break;
            case 0x10:
                notifyCharges(buf[7] != 0, buf[6] & 0xff);
                break;
            default:
                break;
        }
    }

    private void showShort(String text) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ToastUtils.showShort(text);

            }
        });
    }


    private void notifyCharges(boolean isCharge, int power) {
        if (!isCharge && (power == BatteryView.currentPower)) {
            return;
        }
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChargeUtils.notifyCharges(isCharge, power);

            }
        });
    }

    private void returnMainActivity() {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });
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
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textAlarm.setText(text);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerReceiverScreenBroadcast();
    }
}