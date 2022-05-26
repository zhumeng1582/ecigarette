package com.industio.ecigarette.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hjy.bluetooth.HBluetooth;
import com.hjy.bluetooth.entity.BluetoothDevice;
import com.hjy.bluetooth.exception.BluetoothException;
import com.hjy.bluetooth.inter.BleMtuChangedCallback;
import com.hjy.bluetooth.inter.BleNotifyCallBack;
import com.hjy.bluetooth.inter.ClassicBluetoothPairCallBack;
import com.hjy.bluetooth.inter.ConnectCallBack;
import com.hjy.bluetooth.inter.ReceiveCallBack;
import com.hjy.bluetooth.inter.ScanCallBack;
import com.hjy.bluetooth.inter.SendCallBack;
import com.hjy.bluetooth.operator.abstra.Sender;
import com.industio.ecigarette.adapter.BluetoothListAdapter;
import com.industio.ecigarette.bean.ClassicTemperatureValue;
import com.industio.ecigarette.databinding.ActivityDataSyncBinding;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.ClassicTemperatureUtils;
import com.industio.ecigarette.util.Crc16Utils;

import java.io.DataInputStream;
import java.util.List;


public class DataSyncActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivityDataSyncBinding binding;
    private static final String TAG = "DataSyncActivity";
    private static final List<String> ClassicTemperatureNameList = ClassicTemperatureUtils.getHashMapKeys();
    BluetoothListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDataSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnSync,
        }, this);

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        scanDevice();
        getConnectedBtDevice();
        receiver();
        adapter = new BluetoothListAdapter(new BluetoothListAdapter.ItemOnClick() {
            @Override
            public void itemOnClick(int index, BluetoothDevice device) {
                connect(device);
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(DataSyncActivity.this));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public void charge(boolean isCharge, int power) {
        if (power <= 5) {
            binding.included.batteryView.setPower(power * 20);
        }
        if (isCharge) {
            binding.included.imageChange.setVisibility(View.VISIBLE);
        } else {
            binding.included.imageChange.setVisibility(View.GONE);
        }
    }

    @Override
    public void timer() {
        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            binding.included.iconHomeWifi.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeWifi.setVisibility(View.GONE);
        }
        if (BluetoothUtils.getState() == BluetoothAdapter.STATE_CONNECTED) {
            binding.included.iconHomeBluetooth.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeBluetooth.setVisibility(View.GONE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getConnectedBtDevice() {
        binding.toolbar.setTitle(BluetoothUtils.getConnectedBtDevice());
    }


    private void scanDevice() {

//        //请填写你自己设备的UUID
//        //低功耗蓝牙才需要如下配置BleConfig,经典蓝牙不需要new HBluetooth.BleConfig()
//        HBluetooth.BleConfig bleConfig = new HBluetooth.BleConfig();
//        bleConfig.withServiceUUID("0000fe61-0000-1000-8000-00805f9b34fb")
//                .withWriteCharacteristicUUID("0000fe61-0000-1000-8000-00805f9b34fb")
//                .withNotifyCharacteristicUUID("0000fe61-0000-1000-8000-00805f9b34fb")
//                //.liveUpdateScannedDeviceName(true)
//                //命令长度大于20个字节时是否分包发送，默认false,分包时可以调两参方法设置包之间发送间隔
//                //默认false,注释部分为默认值
//                //.splitPacketToSendWhenCmdLenBeyond(false)
//                //.useCharacteristicDescriptor(false)
//                //连接后开启通知的延迟时间，单位ms，默认200ms
//                //.notifyDelay(200)
//                .setMtu(200, new BleMtuChangedCallback() {
//                    @Override
//                    public void onSetMTUFailure(int realMtuSize, BluetoothException bleException) {
//                        Log.i(TAG, "bleException:" + bleException.getMessage() + "  realMtuSize:" + realMtuSize);
//                    }
//
//                    @Override
//                    public void onMtuChanged(int mtuSize) {
//                        Log.i(TAG, "Mtu set success,mtuSize:" + mtuSize);
//                    }
//                });


        HBluetooth.getInstance()
                .enableBluetooth();
//                .setBleConfig(bleConfig);

        HBluetooth.getInstance().scan(android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC, new ScanCallBack() {
            @Override
            public void onScanStart() {
                Log.i(TAG, "开始扫描");
            }

            @Override
            public void onScanning(List<BluetoothDevice> scannedDevices, BluetoothDevice currentScannedDevice) {
                Log.i(TAG, "扫描中");
                if (CollectionUtils.isNotEmpty(scannedDevices)) {
                    adapter.setDatas(scannedDevices);
                }
            }

            @Override
            public void onError(int errorType, String errorMsg) {

            }

            @Override
            public void onScanFinished(List<BluetoothDevice> bluetoothDevices) {
                Log.i(TAG, "扫描结束:" + CollectionUtils.size(bluetoothDevices));

                if (CollectionUtils.isNotEmpty(bluetoothDevices)) {
                    adapter.setDatas(bluetoothDevices);
                }
            }
        });
    }

    private void connect(BluetoothDevice device) {
        Log.i(TAG, "开始连接。。。。。");
        HBluetooth.getInstance()
                .connect(device, new ConnectCallBack() {

                    @Override
                    public void onConnecting() {
                        Log.i(TAG, "连接中...");
                    }

                    @Override
                    public void onConnected(Sender sender) {
                        Log.i(TAG, "连接成功,isConnected:" + HBluetooth.getInstance().isConnected());
                        //调用发送器发送命令
                        byte[] demoCommand = new byte[]{0x01, 0x02};
                        sender.send(demoCommand, new SendCallBack() {
                            @Override
                            public void onSending(byte[] command) {
                                Log.i(TAG, "命令发送中...");
                            }

                            @Override
                            public void onSendFailure(BluetoothException bleException) {
                                Log.e("mylog", "发送命令失败->" + bleException.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onDisConnecting() {
                        Log.i(TAG, "断开连接中...");
                    }

                    @Override
                    public void onDisConnected() {
                        Log.i(TAG, "已断开连接,isConnected:" + HBluetooth.getInstance().isConnected());
                    }

                    @Override
                    public void onError(int errorType, String errorMsg) {
                        Log.i(TAG, "错误类型：" + errorType + " 错误原因：" + errorMsg);
                    }
                });
    }

    private static final String dataEnd = "-->data end";
    private static final String nameTitleSplit = "<-name&data->";

    private void receiver() {

        HBluetooth.getInstance().setReceiver(new ReceiveCallBack() {
            @Override
            public void onReceived(DataInputStream dataInputStream, byte[] result) {
                String text = Crc16Utils.byte2String(result);
                if (text.endsWith(dataEnd)) {
                    //收到数据之后让队友发送下一条
                    sendDataNext(text.replace(dataEnd, ""));
                } else if (text.contains(nameTitleSplit)) {
                    String[] data = text.split(nameTitleSplit);
                    ClassicTemperatureUtils.saveAsTemperatureValue(data[0], GsonUtils.fromJson(data[0], ClassicTemperatureValue.class));
                    //告诉队友已经收到数据
                    sendDataNext(data[0] + dataEnd);
                }

            }
        });

    }

    private void sendDataNext(String currentName) {
        int nextIndex = 0;


        if (!StringUtils.isTrimEmpty(currentName)) {
            nextIndex = ClassicTemperatureNameList.indexOf(currentName) + 1;
        }

        if (nextIndex < CollectionUtils.size(ClassicTemperatureNameList)) {
            String hashMapKey = ClassicTemperatureNameList.get(nextIndex);
            ClassicTemperatureValue value = ClassicTemperatureUtils.getClassicTemperatureValue(hashMapKey);
            String text = GsonUtils.toJson(value);

            HBluetooth.getInstance()
                    .send(Crc16Utils.string2Byte(hashMapKey + nameTitleSplit + text), new SendCallBack() {
                        @Override
                        public void onSending(byte[] command) {
                            Log.i(TAG, "命令发送中...");
                        }

                        @Override
                        public void onSendFailure(BluetoothException bleException) {
                            Log.e("mylog", "发送命令失败->" + bleException.getMessage());
                        }
                    });
        }
    }

    private void sendData() {
        sendDataNext("");
    }


    @Override
    public View getLock() {
        return binding.textLock1;
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnSync) {
            sendData();
        }
    }


}