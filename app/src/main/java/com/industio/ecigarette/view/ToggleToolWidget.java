package com.industio.ecigarette.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.WifiUtils;

import java.util.concurrent.TimeUnit;

public class ToggleToolWidget extends FrameLayout implements OnClickListener {


    /**
     * Called when the activity is first created.
     */
    protected ImageView iv_bluetooth = null;
    protected ImageView iv_wifi = null;
    protected TextView textWIFI = null;
    protected TextView textBluetooth = null;
    protected ImageView iv_brightness = null;
    protected ImageView iv_lock = null;
    protected ProSeekBar seekBar = null;

    public final static int mostBrightness = 255;//
    public final static int moreBrightness = 150;//
    public final static int lowBrightness = 50;//
    private boolean runing = false;

    public ToggleToolWidget(Context context) {
        this(context, null);
    }

    public ToggleToolWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleToolWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.toggle_tools, this, true);

        init();
    }

    public void init() {

        textBluetooth = findViewById(R.id.textBluetooth);
        iv_bluetooth = findViewById(R.id.bluetooth);
        iv_wifi = findViewById(R.id.wifi);
        textWIFI = findViewById(R.id.textWIFI);

        findViewById(R.id.llWifi).setOnClickListener(this);
        findViewById(R.id.llBluetooth).setOnClickListener(this);

        iv_brightness = findViewById(R.id.brightness);
        iv_brightness.setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMin1(lowBrightness);
        seekBar.setMax1(mostBrightness);

        int currentBrightness = BrightnessUtils.getBrightness();
        seekBar.setProgress1(currentBrightness);
        initBrightnessImage(getContext(), iv_brightness, currentBrightness);

        seekBar.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("onProgressChanged", "onProgressChanged = " + i);
                setBrightness(getContext(), i);
                initBrightnessImage(getContext(), iv_brightness, i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        iv_lock = findViewById(R.id.lock);
        iv_lock.setOnClickListener(this);

        ThreadUtils.executeBySingleAtFixRate(task, 1, TimeUnit.SECONDS);
    }

    public static void initBrightnessImage(Context context, ImageView image, int currentBrightness) {
        int current = getNearestNumber(currentBrightness, mostBrightness, moreBrightness, lowBrightness);
        if (current == mostBrightness) {
            image.setImageResource(R.mipmap.brightness_most);
            image.setColorFilter(context.getColor(R.color.main_color));
        } else if (current == lowBrightness) {
            image.setImageResource(R.mipmap.brightness_low);
            image.setColorFilter(context.getColor(R.color.grey_color));
        } else {
            image.setImageResource(R.mipmap.brightness_more);
            image.setColorFilter(context.getColor(R.color.main_color));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.llBluetooth) {
            if (BluetoothUtils.isBlueOn()) {
                BluetoothUtils.offBlueTooth();
            } else {
                BluetoothUtils.onBlueTooth();
            }

        } else if (v.getId() == R.id.llWifi) {
            NetworkUtils.setWifiEnabled(!NetworkUtils.getWifiEnabled());
        } else if (v.getId() == R.id.brightness) {
            int currentBrightness = BrightnessUtils.getBrightness();
            currentBrightness = getNearestNumber(currentBrightness, mostBrightness, moreBrightness, lowBrightness);

            if (currentBrightness == mostBrightness) {
                currentBrightness = moreBrightness;
            } else if (currentBrightness == moreBrightness) {
                currentBrightness = lowBrightness;
            } else {
                currentBrightness = mostBrightness;
            }

            setBrightness(getContext(), currentBrightness);
            seekBar.setProgress1(currentBrightness);
        } else if (v.getId() == R.id.lock) {
            SettingUtils.sysLock(getContext());
        }
    }

    public static void setBrightness(Context context, int currentBrightness) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.screenBrightness = currentBrightness / 255f;
        ((Activity) context).getWindow().setAttributes(lp);
        BrightnessUtils.setBrightness(currentBrightness);
    }

    protected void changeBluetoothImage() {

        int blueState = BluetoothUtils.getState();
        Log.d("ThreadUtils", "----------->blueState = " + blueState);

        if (blueState == BluetoothAdapter.STATE_ON) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_on);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.main_color));
            textBluetooth.setText("已连接");
            textBluetooth.setTextColor(getContext().getColor(R.color.main_color));
        } else if (blueState == BluetoothAdapter.STATE_OFF) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_off);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.grey_color));
            textBluetooth.setText("已关闭");
            textBluetooth.setTextColor(getContext().getColor(R.color.grey_color));
        } else if (blueState == BluetoothAdapter.STATE_TURNING_OFF
                || blueState == BluetoothAdapter.STATE_TURNING_ON) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_ing);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.second_color));
            if (blueState == BluetoothAdapter.STATE_TURNING_OFF) {
                textBluetooth.setText("关闭中...");
            } else {
                textBluetooth.setText("链接中...");
            }
            textBluetooth.setTextColor(getContext().getColor(R.color.second_color));
        }
    }

    protected void changeWifiImage() {
        int wifiState = WifiUtils.getWifiState();
        Log.d("ThreadUtils", "----------->wifiState = " + wifiState);

        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabled);
            iv_wifi.setColorFilter(getContext().getColor(R.color.main_color));
            textWIFI.setText("已链接");
            textWIFI.setTextColor(getContext().getColor(R.color.main_color));
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_disabled);
            iv_wifi.setColorFilter(getContext().getColor(R.color.grey_color));
            textWIFI.setText("已关闭");
            textWIFI.setTextColor(getContext().getColor(R.color.grey_color));
        } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabling);
            iv_wifi.setColorFilter(getContext().getColor(R.color.second_color));
            textWIFI.setText("已打开");
            textWIFI.setTextColor(getContext().getColor(R.color.main_color));
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLING || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabling);
            iv_wifi.setColorFilter(getContext().getColor(R.color.second_color));
            if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                textWIFI.setText("关闭中...");
            } else {
                textWIFI.setText("链接中...");
            }
            textWIFI.setTextColor(getContext().getColor(R.color.second_color));
        }
    }

    public void onStateDoing() {
        runing = true;
        LogUtils.d("----------->onStateDoing");
    }

    public void stop() {
        runing = false;
    }

    private final ThreadUtils.Task task = new ThreadUtils.Task<Object>() {
        @Override
        public Object doInBackground() throws Throwable {
            return null;
        }

        @Override
        public void onSuccess(Object result) {
            if (runing) {
                Log.d("ThreadUtils", "----------->ThreadUtils");
                changeWifiImage();
                changeBluetoothImage();
            }

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFail(Throwable t) {

        }
    };


    public static int getNearestNumber(int destNumber, int... numbers) {
        int diffValue = Integer.MAX_VALUE;
        int nearestNumber = numbers[0];
        for (int number : numbers) {
            System.out.println(diffValue + "   " + Math.abs(destNumber - number));
            if (diffValue > Math.abs(destNumber - number)) {
                diffValue = Math.abs(destNumber - number);
                nearestNumber = number;
            }
        }
        return nearestNumber;
    }


}
