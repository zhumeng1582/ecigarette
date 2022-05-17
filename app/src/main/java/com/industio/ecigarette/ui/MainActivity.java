package com.industio.ecigarette.ui;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.Crc16Utils;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.view.ViewAnimate;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;


public class MainActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    int showTimeCount = 0;

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

        TimerUtils.addBrightnessLister(new TimerUtils.iBrightnessListener() {
            @Override
            public void brightnessListener() {
                setBrightness(0);
            }
        });

        closeController();

        registerSerial();
        TimerUtils.addTimers(new TimerUtils.iTimer() {
            @Override
            public void timer() {
                if (showTimeCount > 0) {
                    showTimeCount--;
                }
                if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
                    binding.iconHomeWifi.setVisibility(View.VISIBLE);
                } else {
                    binding.iconHomeWifi.setVisibility(View.GONE);
                }
                if (BluetoothUtils.getState() == BluetoothAdapter.STATE_CONNECTED) {
                    binding.iconHomeBluetooth.setVisibility(View.VISIBLE);
                } else {
                    binding.iconHomeBluetooth.setVisibility(View.GONE);
                }
            }
        });
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

    void setBrightness(int currentBrightness) {
        SettingUtils.setBrightness(MainActivity.this, currentBrightness);
        binding.topView.initView();
    }

    private void permission() {
        PermissionUtils.permission(Manifest.permission.BLUETOOTH, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).callback(new PermissionUtils.SimpleCallback() {
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
            new BottomSheetMenuDialogFragment.Builder(this)
                    .setSheet(R.menu.list_mode)
                    .setTitle("选择模式")
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o) {
                        }

                        @Override
                        public void onSheetItemSelected(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, MenuItem menuItem, Object o) {
                            if (menuItem.getItemId() == R.id.classics) {
                                ParaActivity.newInstance(MainActivity.this, ParaActivity.classics);
                            } else if (menuItem.getItemId() == R.id.elegant) {
                                ParaActivity.newInstance(MainActivity.this, ParaActivity.elegant);
                            } else {
                                ParaActivity.newInstance(MainActivity.this, ParaActivity.strong);
                            }
                        }

                        @Override
                        public void onSheetDismissed(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o, int i) {

                        }
                    })
                    .show(getSupportFragmentManager());

        } else if (view == binding.btnSetPara) {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        }
    }

    private void registerSerial() {
        SerialController.getInstance().registerSerialReadListener((buf, len) -> {
            if (showTimeCount > 0) {
                return;
            }

            if (Crc16Utils.dataVerify(buf)) return;

            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dataAnalysis(buf);
                }
            });

        });
    }

    private void dataAnalysis(byte[] buf) {
        switch (buf[4] & 0xff) {
            case 0x00:
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;//显示主界面;
            case 0x01:
                int key = buf[5] & 0xff;

                if (DeviceConstant.stopTime.containsKey(key)) {
                    showTimeCount = DeviceConstant.stopTime.get(key);
                }

                if (DeviceConstant.RECEVICE_TIPS.containsKey(key)) {
                    String text = DeviceConstant.RECEVICE_TIPS.get(key);
                    if (key <= 0x0A) {
                        binding.textAlarm.setText(text);
                    } else if (key == 0x0B) {
                        int temp = (buf[6] & 0xff) * 255 + (buf[7] & 0xff);
                        binding.textAlarm.setText(text + "\n" + temp+ "℃");
                    }
                } else {
                    binding.textAlarm.setText("错误数据：" + buf[5]);
                }

                break;
            case 0x0C:
                int temp1 = (buf[5] & 0xff) * 255 + (buf[6] & 0xff);
                int temp2 = buf[7];
                int temp3 = (buf[8] & 0xff) * 255 + (buf[9] & 0xff);
                String text = "温度：" + temp1 + "℃"+ "\n"+"口数：" + temp2 + "\n"+"时间：" + temp3 + "s\n";
                binding.textAlarm.setText(text);

                break;
            case 0x10:
                setDevicePower(buf[6] & 0xff);
                if (buf[7] == 0) {
                    binding.textAlarm.setText("没有充电");
                } else {
                    binding.textAlarm.setText("正在充电");
                }
                break;
            default:
                break;
        }
    }

    private void setDevicePower(int power) {
        if (power <= 5) {
            binding.batteryView.setPower(power * 20);
        }
    }

}