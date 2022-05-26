package com.sdwfqin.bluetoothdemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class BlueToothActivity extends AppCompatActivity  {
    private ActivityBluetoothBinding binding;

    private Context mContext;

    private String[] mTitle = new String[]{"开启蓝牙", "关闭蓝牙", "设备列表", "接收数据", "已配对设备"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mContext = this;

        binding.list.setAdapter(new ArrayAdapter<>(this, R.layout.blue_item_list, R.id.tv_items, mTitle));

        initListener();
    }

    private void initListener() {
        binding.list.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (i) {
                case 0:
                    CbtManager
                            .getInstance()
                            .enableBluetooth(isOn -> {
                                if (isOn) {
                                    Toast.makeText(mContext, "蓝牙已开启", Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;
                case 1:
                    CbtManager
                            .getInstance()
                            .disableBluetooth(isOn -> {
                                if (!isOn) {
                                    Toast.makeText(mContext, "蓝牙已关闭", Toast.LENGTH_SHORT).show();
                                }
                            });
                    break;
                case 2:
                    startActivity(new Intent(mContext, ScanListActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(mContext, ReceiveDataActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(mContext, BondedListActivity.class));
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
