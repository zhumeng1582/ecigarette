package com.industio.ecigarette.serialcontroller;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.util.DeviceConstant;

import org.ido.iface.SerialControl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SerialController {
    private static final String TAG = "SerialController";
    private static final int MSG_CLEAN_SLEEP_COUNT = 0X001;
    private volatile boolean delay_flag = true;

    private static SerialController serialController;
    private SerialControl serialControl;
    private SerialReadListener listSerialReadListener;


    public void registerSerialReadListener(SerialReadListener serialReadListener) {
        listSerialReadListener = serialReadListener;
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
                Log.d("uart", "rx:" + bytesToHexString(buf, len));
                if (listSerialReadListener != null) {
                    setClsSleepCnt();
                    listSerialReadListener.read(buf, len);
                }
            }
        };

        String portName = "/dev/ttyS2";
        if (serialControl.init(portName, 115200)) {
//            serialControl.write(DeviceConstant.startCmd);
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
        });
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

    public static String bytesToHexString(byte[] src, int len) {
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

    private static void setClsSleepCnt() { //清除掉系统无操作时计时
        final String path = "/dev/cls_evt";
        if (new File(path).exists()) {
            FileWriter writer = null;
            try {
                writer = new FileWriter(path);
                writer.write("on ");
                writer.flush();
            } catch (Exception ex) {
                Log.d("setClsSleepCnt", "" + ex);
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLEAN_SLEEP_COUNT:
                    Log.i(TAG, "clean sleep count");
                    setClsSleepCnt();
                    delay_flag = true;
                    break;
            }
        }
    }

    ;
}
