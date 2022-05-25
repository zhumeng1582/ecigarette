package com.industio.ecigarette.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.util.ChargeUtils;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.view.ToggleToolWidget;
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
                binding.llShoutDown,
                binding.textSyncData,
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
//        binding.textLockScreenTime.setText(CacheDataUtils.getLockScreenTimeText());
        TimerUtils.addTimers(new TimerUtils.iTimer() {
            @Override
            public void timer() {
                binding.textShoutDownTime.setText(CacheDataUtils.getShoutDownTimeText());
//                binding.textLockScreenTime.setText(CacheDataUtils.getLockScreenTimeText());
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
        });

        binding.seekBarLockScreenTime.setMin1(10);
        binding.seekBarLockScreenTime.setMax1(60);
        int lockScreenTime = CacheDataUtils.getLockScreenTime();
        binding.textLockScreenTime.setText(lockScreenTime + "秒");
        binding.seekBarLockScreenTime.setProgress1(lockScreenTime);

        binding.seekBarLockScreenTime.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                CacheDataUtils.setLockScreenTime(i);
                binding.textLockScreenTime.setText(i + "秒");
                SettingUtils.systemSleep(SettingActivity.this, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.switchLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CacheDataUtils.setLockScreenSwitch(b);
                if (b) {
                    ToastUtils.showShort("锁屏功能已开启");
                } else {
                    ToastUtils.showShort("锁屏功能已关闭");
                }
            }
        });
        binding.switchLock.setChecked(CacheDataUtils.getLockScreenSwitch());
        initView();
    }


    private void initView() {
        int currentBrightness = BrightnessUtils.getBrightness();
        binding.seekBar.setProgress1(currentBrightness);
        ToggleToolWidget.initBrightnessImage(this, binding.brightness, currentBrightness);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChargeUtils.addCharges(iCharge);
    }

    ChargeUtils.iCharge iCharge = new ChargeUtils.iCharge() {
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
    };

    @Override
    protected void onStop() {
        super.onStop();
        ChargeUtils.removeCharges(iCharge);
    }

    @Override
    public View getLock() {
        return binding.textLock1;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.textBluetooth) {
            SettingUtils.setBlueTooth();
        } else if (view == binding.textWIFI) {
            SettingUtils.openWIFISettings();
//        } else if (view == binding.textLock) {
//            int currentBrightness = 0;
//            binding.seekBar.setProgress1(currentBrightness);
//            SettingUtils.setBrightness(SettingActivity.this, currentBrightness);
//            ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, currentBrightness);
//        } else if (view == binding.textUnLock) {
//            int currentBrightness = 0;
//            binding.seekBar.setProgress1(currentBrightness);
//            ToggleToolWidget.setBrightness(SettingActivity.this, currentBrightness);
//            ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, currentBrightness);
        } else if (view == binding.textSyncData) {
            PermissionUtils.permission(Manifest.permission.BLUETOOTH).callback(new PermissionUtils.SimpleCallback() {
                @Override
                public void onGranted() {
                    startActivity(new Intent(SettingActivity.this, DataSyncActivity.class));
                }

                @Override
                public void onDenied() {

                }
            }).request();
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