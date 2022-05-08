package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.view.ToggleToolWidget;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {
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
                binding.textUnLock,
                binding.textLight,
                binding.textShoutDown,
        }, this);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.seekBar.setMin1(ToggleToolWidget.lowBrightness);
        binding.seekBar.setMax1(ToggleToolWidget.mostBrightness);

        int currentBrightness = BrightnessUtils.getBrightness();
        binding.seekBar.setProgress1(currentBrightness);
        ToggleToolWidget.initBrightnessImage(this, binding.brightness, currentBrightness);

        binding.seekBar.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                ToggleToolWidget.setBrightness(SettingActivity.this, i);
                ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }


    @Override
    public void onClick(View view) {
        if (view == binding.textBluetooth) {
            SettingUtils.setBlueTooth();
        } else if (view == binding.textWIFI) {
            SettingUtils.openWIFISettings();
        } else if (view == binding.textLock) {
            SettingUtils.sysLock(this);
//            startService(new Intent(this, LockScreenService.class));
        } else if (view == binding.textUnLock) {
            int currentBrightness = 0;
            binding.seekBar.setProgress1(currentBrightness);
            ToggleToolWidget.setBrightness(SettingActivity.this, currentBrightness);
            ToggleToolWidget.initBrightnessImage(SettingActivity.this, binding.brightness, currentBrightness);
        } else if (view == binding.textLight) {
            SettingUtils.setDISPLAY();
        } else if (view == binding.textShoutDown) {
            SettingUtils.systemShutdown(this, true);
        }
    }

}