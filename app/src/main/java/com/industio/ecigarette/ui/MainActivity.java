package com.industio.ecigarette.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.view.ViewAnimate;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;


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
                LogUtils.d("----------->onScroll:" + e1.getRawY());
                if (e1.getRawY() < 300 && distanceY < 0) {
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


        });

        closeController();
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
//        binding.imagePower.setVisibility(View.GONE);
        SerialController.getInstance().registerSerialReadListener((buf, len) -> {
            switch (buf[4]) {
                case 0x00:
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    break;//显示主界面;
                case 0x01:
                    if (buf[5] <= 0x0C) {
                        binding.textAlarm.setText(DeviceConstant.RECEVICE_TIPS[buf[5]]);
                    }
                    break;
                case 0x10:
                    binding.imagePower.setVisibility(View.VISIBLE);
                    //相应的电池符号;
                    if (buf[6] > 75) {
                        binding.imagePower.setImageResource(R.mipmap.icon_power4);
                        binding.imagePower.setColorFilter(getColor(R.color.main_color));
                    } else if (buf[6] > 50) {
                        binding.imagePower.setImageResource(R.mipmap.icon_power3);
                        binding.imagePower.setColorFilter(getColor(R.color.main_color));
                    } else if (buf[6] > 25) {
                        binding.imagePower.setImageResource(R.mipmap.icon_power2);
                        binding.imagePower.setColorFilter(getColor(R.color.red));
                    } else if (buf[6] > 0) {
                        binding.imagePower.setImageResource(R.mipmap.icon_power1);
                        binding.imagePower.setColorFilter(getColor(R.color.red_low_power));
                    }
                    //buf[6]（电量）
                    break;
                default:
                    break;
            }
        });
    }

}