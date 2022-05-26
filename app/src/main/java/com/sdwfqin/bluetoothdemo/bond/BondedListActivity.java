package com.sdwfqin.bluetoothdemo.bond;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.industio.ecigarette.databinding.ActivityBondedListBinding;
import com.sdwfqin.bluetoothdemo.send.SendDataActivity;
import com.sdwfqin.cbt.CbtManager;
import com.sdwfqin.cbt.callback.ConnectDeviceCallback;
import com.sdwfqin.cbt.utils.CbtLogs;

import java.util.List;

/**
 * 描述：已配对设备
 *
 * @author zhangqin
 * @date 2018/6/5
 */
public class BondedListActivity extends AppCompatActivity {
    private ActivityBondedListBinding binding;

    private Context mContext;
    private BondedListAdapter mBondedListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBondedListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mContext = this;

        initList();
    }

    /**
     * 初始化列表
     */
    private void initList() {
        binding.rv.setLayoutManager(new LinearLayoutManager(mContext));
        binding.rv.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mBondedListAdapter = new BondedListAdapter(null);
        binding.rv.setAdapter(mBondedListAdapter);
        mBondedListAdapter.setOnItemClickListener(
                (adapter, view, position) -> {
                    BluetoothDevice item = mBondedListAdapter.getItem(position);
                    CbtManager
                            .getInstance()
                            .connectDevice(item, new ConnectDeviceCallback() {
                                @Override
                                public void connectSuccess(BluetoothSocket socket, BluetoothDevice device) {
                                    Toast.makeText(mContext, "连接成功！", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(mContext, SendDataActivity.class));
                                }

                                @Override
                                public void connectError(Throwable throwable) {
                                    CbtLogs.e(throwable.getMessage());
                                    Toast.makeText(mContext, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
        );

        List<BluetoothDevice> bondedDevices = CbtManager.getInstance().getBondedDevices();
        mBondedListAdapter.setNewData(bondedDevices);
    }
}
