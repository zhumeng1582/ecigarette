package com.sdwfqin.bluetoothdemo.receivedata;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.GsonUtils;
import com.industio.ecigarette.bean.DevicePara;
import com.industio.ecigarette.databinding.ActivityReceiveDataBinding;
import com.industio.ecigarette.util.ClassicTemperatureUtils;
import com.sdwfqin.cbt.CbtManager;
import com.sdwfqin.cbt.callback.ServiceListenerCallback;
import com.sdwfqin.cbt.utils.CbtLogs;

import java.util.Map;


/**
 * 描述：接收数据
 *
 * @author zhangqin
 * @date 2018/6/3
 */
public class ReceiveDataActivity extends AppCompatActivity {
    private ActivityReceiveDataBinding binding;


    private ReceiveDataAdapter mReceiveDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReceiveDataBinding.inflate(getLayoutInflater());
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
        mReceiveDataAdapter = new ReceiveDataAdapter(null);
        binding.rv.setAdapter(mReceiveDataAdapter);
    }

    /**
     * 加载数据
     */
    private void initData() {
        CbtManager.getInstance().startServiceListener(new ServiceListenerCallback() {
            @Override
            public void onStartError(Throwable throwable) {
                CbtLogs.e(throwable.getMessage());
            }

            @Override
            public void onDataListener(String s, BluetoothDevice device) {
                CbtLogs.e("onDataListener：" + s);
                runOnUiThread(() ->
                        {
                            mReceiveDataAdapter.addData(new ReceiveDataModel(device, s));
                            Map<String, DevicePara> valueHashMap = GsonUtils.fromJson(s, GsonUtils.getMapType(String.class, DevicePara.class));
                            for (String s1 : valueHashMap.keySet()) {
                                ClassicTemperatureUtils.saveAsTemperatureValue(s1, valueHashMap.get(s1));
                            }

                        }
                );
            }
        });
    }
}
