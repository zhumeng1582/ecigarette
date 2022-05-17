package com.industio.ecigarette.serialcontroller;

import android.util.Log;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.util.DeviceConstant;

import org.ido.iface.SerialControl;

import java.util.ArrayList;
import java.util.List;

public class SerialController {
    private static SerialController serialController;
    private SerialControl serialControl;
    private List<SerialReadListener> listSerialReadListener;

    public void registerSerialReadListener(SerialReadListener serialReadListener) {
        if (listSerialReadListener == null) {
            listSerialReadListener = new ArrayList<>();
        }
        listSerialReadListener.add(serialReadListener);
    }

    public void unRegisterSerialReadListener(SerialReadListener serialReadListener) {
        if (CollectionUtils.isEmpty(listSerialReadListener)) {
            return;
        }
        listSerialReadListener.remove(serialReadListener);
    }

    private SerialController() {
        initSerial();
    }

    public static SerialController getInstance() {
        if (serialController == null) {
            serialController = new SerialController();
        }
        return serialController;
    }

    //初始化串口
    private void initSerial() {

        serialControl = new SerialControl() {
            @Override
            protected void read(byte[] buf, int len) {
                if (CollectionUtils.isNotEmpty(listSerialReadListener)) {
                    for (SerialReadListener serialReadListener : listSerialReadListener) {
                        serialReadListener.read(buf, len);
                        Log.d("uart", "rx:" + bytesToHexString(buf, len));
                    }
                }
            }
        };

        String portName = "/dev/ttyS2";
        if (serialControl.init(portName, 115200)) {
            serialControl.write(DeviceConstant.startCmd);
            ToastUtils.showShort("打开成功:" + portName);
        } else {
            ToastUtils.showLong("打开失败:" + portName);
        }
    }

    public void sendSync(byte[] buf) {
        ThreadUtils.executeByCached(new ThreadUtils.SimpleTask<Void>() {
            @Override
            public Void doInBackground() {
                send(buf);
                return null;
            }

            @Override
            public void onSuccess(Void result) {

            }
        },1);
    }

    private void send(byte[] buf) {
        if (serialControl == null) {
            initSerial();
        }
        if (serialControl != null) {
            serialControl.write(buf);//发送
        }
    }

    public interface SerialReadListener {
        void read(byte[] buf, int len);
    }

    private static String bytesToHexString(byte[] src, int len) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < len; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);

            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }
}
