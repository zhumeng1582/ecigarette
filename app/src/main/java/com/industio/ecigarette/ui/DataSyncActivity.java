package com.industio.ecigarette.ui;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.hjy.bluetooth.HBluetooth;
import com.hjy.bluetooth.entity.BluetoothDevice;
import com.hjy.bluetooth.exception.BluetoothException;
import com.hjy.bluetooth.inter.ConnectCallBack;
import com.hjy.bluetooth.inter.ReceiveCallBack;
import com.hjy.bluetooth.inter.ScanCallBack;
import com.hjy.bluetooth.inter.SendCallBack;
import com.hjy.bluetooth.operator.abstra.Sender;
import com.industio.ecigarette.bean.ClassicTemperatureValue;
import com.industio.ecigarette.databinding.ActivityDataSyncBinding;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.ChargeUtils;
import com.industio.ecigarette.util.ClassicTemperatureUtils;
import com.industio.ecigarette.util.Crc16Utils;
import com.industio.ecigarette.util.TimerUtils;

import java.io.DataInputStream;
import java.util.List;


public class DataSyncActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private ActivityDataSyncBinding binding;
    private static final String TAG = "DataSyncActivity";
    private static final List<String> ClassicTemperatureNameList = ClassicTemperatureUtils.getHashMapKeys();
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

        TimerUtils.addTimers(new TimerUtils.iTimer() {
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
        });
        ChargeUtils.addCharges(new ChargeUtils.iCharge() {
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
        });

        scanDevice();
        getConnectedBtDevice();
        receiver();
    }

    @SuppressLint("MissingPermission")
    private void getConnectedBtDevice() {
        binding.textBluetoothName.setText(BluetoothUtils.getConnectedBtDevice());

    }


    private void scanDevice() {

        HBluetooth.getInstance()
                .enableBluetooth()
                .scan(android.bluetooth.BluetoothDevice.DEVICE_TYPE_CLASSIC, new ScanCallBack() {
                    @Override
                    public void onScanStart() {
                        Log.i(TAG, "开始扫描");
                    }

                    @Override
                    public void onScanning(List<BluetoothDevice> scannedDevices, BluetoothDevice currentScannedDevice) {
                        Log.i(TAG, "扫描中");
                    }

                    @Override
                    public void onError(int errorType, String errorMsg) {

                    }

                    @Override
                    public void onScanFinished(List<BluetoothDevice> bluetoothDevices) {
                        Log.i(TAG, "扫描结束:" + CollectionUtils.size(bluetoothDevices));

                        if (CollectionUtils.isNotEmpty(bluetoothDevices)) {
                            for (BluetoothDevice bluetoothDevice : bluetoothDevices) {
                                Log.i(TAG, "bluetoothDevice:" + GsonUtils.toJson(bluetoothDevice));
                                if (StringUtils.equals(bluetoothDevice.getName(), binding.textBluetoothName.getText().toString())) {
                                    connect(bluetoothDevice);
                                    return;
                                }
                            }
                        }
                    }
                });
    }

    private void connect(BluetoothDevice device) {
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

        if (nextIndex < CollectionUtils.size(nextIndex)) {
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