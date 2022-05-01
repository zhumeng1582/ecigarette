package com.industio.ecigarette.serialcontroller;

import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ToastUtils;

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
                    }
                }
            }
        };

        String portName = "";
        if (serialControl.init(portName, 9600, 8, 'N', 1, 0, 10)) {
            ToastUtils.showShort("打开成功:" + portName);
        } else {
            ToastUtils.showLong("打开失败:" + portName);
        }
    }

    public void send(byte[] buf) {
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
}
