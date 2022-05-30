package com.sdwfqin.bluetoothdemo.scan;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.industio.ecigarette.databinding.ActivityScanListBinding;
import com.sdwfqin.bluetoothdemo.send.SendDataActivity;
import com.sdwfqin.cbt.CbtManager;
import com.sdwfqin.cbt.callback.ConnectDeviceCallback;
import com.sdwfqin.cbt.callback.ScanCallback;
import com.sdwfqin.cbt.utils.CbtLogs;

import java.util.List;


/**
 * 描述：扫描设备
 *
 * @author zhangqin
 * @date 2018/5/30
 */
public class ScanListActivity extends AppCompatActivity {

    private ActivityScanListBinding binding;
    private ScanListAdapter mScanListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScanListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initList();
        initData();
    }

    /**
     * 初始化列表
     */
    private void initList() {
        binding.rv.setLayoutManager(new LinearLayoutManager(this));
        binding.rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mScanListAdapter = new ScanListAdapter(null);
        binding.rv.setAdapter(mScanListAdapter);
        mScanListAdapter.setOnItemClickListener(
                (adapter, view, position) -> {
                    BluetoothDevice item = mScanListAdapter.getItem(position);
                    CbtManager
                            .getInstance()
                            .connectDevice(item, new ConnectDeviceCallback() {
                                @Override
                                public void connectSuccess(BluetoothSocket socket, BluetoothDevice device) {
                                    Toast.makeText(ScanListActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ScanListActivity.this, SendDataActivity.class));
                                }

                                @Override
                                public void connectError(Throwable throwable) {
                                    CbtLogs.e(throwable.getMessage());
                                    Toast.makeText(ScanListActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        );
    }

    /**
     * 加载数据
     */
    private void initData() {
        CbtManager
                .getInstance()
                .scan(new ScanCallback() {
                    @Override
                    public void onScanStart(boolean isOn) {

                    }

                    @Override
                    public void onScanStop(List<BluetoothDevice> devices) {
                        mScanListAdapter.setNewData(devices);
                    }

                    @Override
                    public void onFindDevice(BluetoothDevice device) {
                        mScanListAdapter.addData(device);
                    }
                });
    }
}
