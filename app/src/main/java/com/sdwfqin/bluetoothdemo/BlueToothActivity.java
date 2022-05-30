package com.sdwfqin.bluetoothdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityBluetoothBinding;
import com.sdwfqin.bluetoothdemo.bond.BondedListActivity;
import com.sdwfqin.bluetoothdemo.receivedata.ReceiveDataActivity;
import com.sdwfqin.bluetoothdemo.scan.ScanListActivity;
import com.sdwfqin.cbt.CbtManager;

/**
 * 描述：经典蓝牙示例
 *
 * @author zhangqin
 * @date 2018/5/30
 */
public class BlueToothActivity extends AppCompatActivity {
    private ActivityBluetoothBinding binding;


    private String[] mTitle = new String[]{/*"开启蓝牙", "关闭蓝牙",*/ "设备列表", "接收数据", "已配对设备"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.list.setAdapter(new ArrayAdapter<>(this, R.layout.blue_item_list, R.id.tv_items, mTitle));

        initListener();
    }

    private void initListener() {
        binding.list.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
//                case 0:
//                    CbtManager
//                            .getInstance()
//                            .enableBluetooth(isOn -> {
//                                if (isOn) {
//                                    Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    break;
//                case 1:
//                    CbtManager
//                            .getInstance()
//                            .disableBluetooth(isOn -> {
//                                if (!isOn) {
//                                    Toast.makeText(this, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                    break;
                case 0:
                    startActivity(new Intent(this, ScanListActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(this, ReceiveDataActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(this, BondedListActivity.class));
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CbtManager.getInstance().onDestroy();
    }
}
