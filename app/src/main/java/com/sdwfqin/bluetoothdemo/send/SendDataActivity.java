package com.sdwfqin.bluetoothdemo.send;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.GsonUtils;
import com.industio.ecigarette.bean.DevicePara;
import com.industio.ecigarette.databinding.ActivitySendDataBinding;
import com.industio.ecigarette.util.ClassicTemperatureUtils;
import com.sdwfqin.cbt.CbtManager;
import com.sdwfqin.cbt.callback.SendDataCallback;
import com.sdwfqin.cbt.utils.CbtLogs;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 描述：发送数据
 *
 * @author zhangqin
 * @date 2018/6/2
 */
public class SendDataActivity extends AppCompatActivity {


    final static byte[][] BYTES = {
            // 0复位打印机
            {0x1b, 0x40},
            // 标准ASCII字体1
            {0x1b, 0x4d, 0x00},
            // 压缩ASCII字体2
            {0x1b, 0x4d, 0x01},
            // 字体不放大3
            {0x1d, 0x21, 0x00},
            // 宽高加倍4
            {0x1d, 0x21, 0x11},
            // 取消加粗模式5
            {0x1b, 0x45, 0x00},
            // 选择加粗模式6
            {0x1b, 0x45, 0x01},
            // 取消倒置打印7
            {0x1b, 0x7b, 0x00},
            // 选择倒置打印8
            {0x1b, 0x7b, 0x01},
            // 取消黑白反显9
            {0x1d, 0x42, 0x00},
            // 选择黑白反显10
            {0x1d, 0x42, 0x01},
            // 取消顺时针旋转90°11
            {0x1b, 0x56, 0x00},
            // 选择顺时针旋转90°12
            {0x1b, 0x56, 0x01},
    };
    private ActivitySendDataBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySendDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HashMap<String, DevicePara> valueHashMap = new HashMap<>();
        for (String hashMapKey : ClassicTemperatureUtils.getHashMapKeys()) {
            valueHashMap.put(hashMapKey,ClassicTemperatureUtils.getClassicTemperatureValue(hashMapKey));
        }
        binding.data.setText(GsonUtils.toJson(valueHashMap));
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] data;
                try {
                    data = (binding.data.getText().toString() + "\n\n").getBytes("GBK");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return;
                }

                List<byte[]> bytes = new ArrayList<>();
                bytes.add(data);

                CbtManager
                        .getInstance()
                        .sendData(bytes, new SendDataCallback() {
                            @Override
                            public void sendSuccess() {

                            }

                            @Override
                            public void sendError(Throwable throwable) {
                                Toast.makeText(SendDataActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                CbtLogs.e(throwable);
                            }
                        });

            }
        });
    }
}
