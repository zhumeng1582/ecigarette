package com.industio.ecigarette.ui;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.databinding.ActivitySettingBinding;
import com.industio.ecigarette.lockscreen.AdminReceiver;
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
            sysLock();
//            startService(new Intent(this, LockScreenService.class));
        } else if (view == binding.textUnLock) {
            BrightnessUtils.setBrightness(0);
        } else if (view == binding.textLight) {
            SettingUtils.setDISPLAY();
        } else if (view == binding.textShoutDown) {
        }
    }

    /**
     * 调用系统锁方法实现
     */
    private void sysLock() {
        //取得系统服务
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this, AdminReceiver.class);

        boolean active = dpm.isAdminActive(componentName);

        if (active) {
            dpm.lockNow();
        }
    }

}