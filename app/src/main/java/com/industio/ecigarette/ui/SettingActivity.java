package com.industio.ecigarette.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.lockscreen.LockScreenService;
import com.industio.ecigarette.util.SettingUtils;

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

    }


    @Override
    public void onClick(View view) {
        if (view == binding.textBluetooth) {
            SettingUtils.setBlueTooth();
        } else if (view == binding.textWIFI) {
            SettingUtils.openWIFISettings();
        } else if (view == binding.textLock) {
            startService(new Intent(this, LockScreenService.class));
        } else if (view == binding.textUnLock) {
            BrightnessUtils.setBrightness(0);
        } else if (view == binding.textLight) {
            SettingUtils.setDISPLAY();
        } else if (view == binding.textShoutDown) {
        }
    }
}