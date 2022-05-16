package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.view.ToggleToolWidget;
import com.industio.ecigarette.view.ViewAnimate;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;


public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.textBluetooth,
                binding.textWIFI,
                binding.textLock,
                binding.llShoutDown,
        }, this);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.seekBar.setMin1(ToggleToolWidget.lowBrightness);
        binding.seekBar.setMax1(ToggleToolWidget.mostBrightness);


        binding.seekBar.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                SettingUtils.setBrightness(SettingActivity.this, i);
                ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        binding.textShoutDownTime.setText(CacheDataUtils.getShoutDownTimeText());
        binding.textLockScreenTime.setText(CacheDataUtils.getLockScreenTimeText());
        TimerUtils.addTimers(new TimerUtils.iTimer() {
            @Override
            public void timer() {
                binding.textShoutDownTime.setText(CacheDataUtils.getShoutDownTimeText());
                binding.textLockScreenTime.setText(CacheDataUtils.getLockScreenTimeText());
            }
        });
        TimerUtils.addBrightnessLister(new TimerUtils.iBrightnessListener() {
            @Override
            public void brightnessListener() {
                SettingUtils.setBrightness(SettingActivity.this, 0);
                initView();
            }
        });
        binding.seekBarLockScreenTime.setMin1(10);
        binding.seekBarLockScreenTime.setMax1(60);

        binding.seekBarLockScreenTime.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CacheDataUtils.setLockScreenTime(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initView();
    }


    private void initView() {
        int currentBrightness = BrightnessUtils.getBrightness();
        binding.seekBar.setProgress1(currentBrightness);
        ToggleToolWidget.initBrightnessImage(this, binding.brightness, currentBrightness);
    }

    @Override
    public void onClick(View view) {
        if (view == binding.textBluetooth) {
            SettingUtils.setBlueTooth();
        } else if (view == binding.textWIFI) {
            SettingUtils.openWIFISettings();
        } else if (view == binding.textLock) {
            int currentBrightness = 0;
            binding.seekBar.setProgress1(currentBrightness);
            SettingUtils.setBrightness(SettingActivity.this, currentBrightness);
            ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, currentBrightness);
//        } else if (view == binding.textUnLock) {
//            int currentBrightness = 0;
//            binding.seekBar.setProgress1(currentBrightness);
//            ToggleToolWidget.setBrightness(SettingActivity.this, currentBrightness);
//            ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, currentBrightness);
//        } else if (view == binding.textLight) {
//            SettingUtils.setDISPLAY();
        } else if (view == binding.llShoutDown) {
            new BottomSheetMenuDialogFragment.Builder(this)
                    .setSheet(R.menu.list_time)
                    .setTitle("选择关机时间")
                    .setListener(new BottomSheetListener() {
                        @Override
                        public void onSheetShown(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o) {
                        }

                        @Override
                        public void onSheetItemSelected(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, MenuItem menuItem, Object o) {
                            if (menuItem.getItemId() == R.id.time5) {
                                CacheDataUtils.setShoutDownTime(5 * 60);
                            } else if (menuItem.getItemId() == R.id.time15) {
                                CacheDataUtils.setShoutDownTime(15 * 60);
                            } else if (menuItem.getItemId() == R.id.time30) {
                                CacheDataUtils.setShoutDownTime(30 * 60);
                            } else if (menuItem.getItemId() == R.id.time60) {
                                CacheDataUtils.setShoutDownTime(60 * 60);
                            } else if (menuItem.getItemId() == R.id.time120) {
                                CacheDataUtils.setShoutDownTime(120 * 60);
                            }
                        }

                        @Override
                        public void onSheetDismissed(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o, int i) {

                        }
                    })
                    .show(getSupportFragmentManager());
        }
    }


}