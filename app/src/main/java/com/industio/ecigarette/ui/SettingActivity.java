package com.industio.ecigarette.ui;


import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.Strings;
import com.industio.ecigarette.view.ViewAnimate;


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
            Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intent);
        } else if (view == binding.textWIFI) {
            Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intent);
        } else if (view == binding.textLock) {
        } else if (view == binding.textUnLock) {
        } else if (view == binding.textLight) {
            Intent intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
            startActivity(intent);
        } else if (view == binding.textShoutDown) {
        }
    }


}