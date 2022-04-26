package com.industio.ecigarette.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;

import org.ido.iface.SerialControl;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;

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

    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnPlayMusic) {
        } else if (view == binding.btnMode) {
            startActivity(new Intent(MainActivity.this, ParaActivity.class));
        } else if (view == binding.btnSetPara) {
        }
    }

    //初始化串口
    private void initSerial() {

        SerialControl mErrLogSerialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {

            }
        };

        String portName = "";
        if (mErrLogSerialControl.init(portName, 9600, 8, 'N', 1, 0, 10)) {
            ToastUtils.showShort("打开成功:" + portName);
        } else {
            ToastUtils.showLong("打开失败:" + portName);
        }

    }
}