package com.industio.ecigarette.ui;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.util.Strings;
import com.industio.ecigarette.view.DragTopLayout;

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

        initController();
    }

    private void initController() {
        DragTopLayout.from(this)
                .setRefreshRadio(1.4f)
                .listener(new DragTopLayout.SimplePanelListener() {
                    @Override
                    public void onSliding(float radio) {

                    }
                }).setup(binding.dragLayout);

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                switch (buf[4]) {
                    case 0x00:
                        break;//显示主界面;
                    case 0x01:
                        if (buf[5] <= 0x0C) {
                            binding.textAlarm.setText(Strings.RECEVICE_TIPS[buf[5]]);
                        }
                        break;
                    case 0x10:
                        //相应的电池符号;
                        //buf[5]（电量）
                        break;
                    default:
                        break;
                }
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