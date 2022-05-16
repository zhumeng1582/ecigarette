package com.industio.ecigarette.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.view.ToggleToolWidget;
import com.industio.ecigarette.view.ViewAnimate;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private GestureDetector mGestureDetector;
    private BatteryReceiver batteryReceiver;

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
                    binding.topView.onStateDoing();
                    ViewAnimate.topOpen(binding.topView, (int) e2.getRawY());
                } else if (distanceY > 0) {
                    closeController();
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                closeController();
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                int currentBrightness = BrightnessUtils.getBrightness();
                if (currentBrightness < 100) {
                    setBrightness(200);
                } else if (currentBrightness >= 175) {
                    setBrightness(0);
                }

                return super.onDoubleTap(e);
            }
        });
        TimerUtils.addBrightnessLister(new TimerUtils.iBrightnessListener() {
            @Override
            public void brightnessListener() {
                setBrightness(0);
            }
        });

        closeController();

        registerSerial();
        initBatteryReceiver();
        TimerUtils.init();
    }

    void setBrightness(int currentBrightness) {
        SettingUtils.setBrightness(MainActivity.this, currentBrightness);
        binding.topView.initView();
    }

    private void initBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver();
        registerReceiver(batteryReceiver, intentFilter);
    }


    private void permission() {
        PermissionUtils.permission(Manifest.permission.BLUETOOTH, Manifest.permission.CHANGE_WIFI_STATE).callback(new PermissionUtils.SimpleCallback() {
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
        binding.batteryView.setVisibility(View.GONE);
        SerialController.getInstance().registerSerialReadListener((buf, len) -> {
            switch (buf[4]) {
                case 0x00:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    break;//显示主界面;
                case 0x01:
                    if (buf[5] <= 0x0A) {
                        binding.textAlarm.setText(DeviceConstant.RECEVICE_TIPS[buf[5]]);
                    } else if (buf[5] == 0x0B) {
                        int temp = buf[6] * 255 + buf[7];
                        binding.textAlarm.setText(DeviceConstant.RECEVICE_TIPS[buf[5]] + "\n" + temp);
                    }
                    break;
                case 0x0C:
                    int temp1 = buf[5] * 255 + buf[6];
                    int temp2 = buf[7];
                    int temp3 = buf[8] * 255 + buf[9];
                    binding.textAlarm.setText("温度：" + temp1 + "\n");
                    binding.textAlarm.setText("口数：" + temp2 + "\n");
                    binding.textAlarm.setText("时间：" + temp3 + "\n");

                    break;
                case 0x10:
                    setDevicePower(buf[6]);
                    if (buf[7] == 0) {
                        binding.textAlarm.setText("没有充电");
                    } else {
                        binding.textAlarm.setText("正在充电");
                    }
                    break;
                default:
                    break;
            }
        });
    }

    private void setPower(int percent) {
        //相应的电池符号;
        if (percent > 75) {
            binding.imagePower.setImageResource(R.mipmap.icon_power4);
            binding.imagePower.setColorFilter(getColor(R.color.main_color));
        } else if (percent > 50) {
            binding.imagePower.setImageResource(R.mipmap.icon_power3);
            binding.imagePower.setColorFilter(getColor(R.color.main_color));
        } else if (percent > 25) {
            binding.imagePower.setImageResource(R.mipmap.icon_power2);
            binding.imagePower.setColorFilter(getColor(R.color.red));
        } else if (percent > 0) {
            binding.imagePower.setImageResource(R.mipmap.icon_power1);
            binding.imagePower.setColorFilter(getColor(R.color.red_low_power));
        }
    }

    private void setDevicePower(int power) {
        binding.batteryView.setVisibility(View.VISIBLE);
        if (power >= 5) {
            binding.batteryView.setPower(100);
        } else if (power >= 4) {
            binding.batteryView.setPower(80);
        } else if (power >= 3) {
            binding.batteryView.setPower(60);
        } else if (power >= 2) {
            binding.batteryView.setPower(20);
        } else if (power >= 1) {
            binding.batteryView.setPower(10);
        } else if (power >= 0) {
            binding.batteryView.setPower(0);
        }

    }

    class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //判断它是否是为电量变化的Broadcast Action
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                //获取当前电量
                int current = intent.getIntExtra("level", 0);
                //电量的总刻度
                int total = intent.getIntExtra("scale", 100);
                int percent = current * 100 / total;
                setPower(percent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁广播
        unregisterReceiver(batteryReceiver);
    }

}