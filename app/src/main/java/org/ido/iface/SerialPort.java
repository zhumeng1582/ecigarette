package org.ido.iface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;

public class SerialPort {

    public FileInputStream mFileInputStream;
    public FileOutputStream mFileOutputStream;
    private FileDescriptor mFd;

    protected SerialPort(){}

    protected boolean init(File device, int speed) {
        mFd = open(device.getAbsolutePath(), speed);
        if (mFd == null) {
            return false;
        } else {
            try {
                mFileInputStream = new FileInputStream(mFd);
                mFileOutputStream = new FileOutputStream(mFd);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    protected InputStream getInputStream() {
        return mFileInputStream;
    }

    protected OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    protected void serialClose() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
                mFileOutputStream.close();
                close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //path：设备节点
    //speed:最大4Mbps
    private native FileDescriptor open(String path, int speed);

    private native void close();

    static {
        System.loadLibrary("serialport");
    }
}
