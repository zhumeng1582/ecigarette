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
import com.industio.ecigarette.R;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.util.WifiUtils;


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

        initView();

        seekBar.setOnSeekBarChangeListener1(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                Log.d("onProgressChanged", "onProgressChanged = " + i);
                SettingUtils.setBrightness(getContext(), i);
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
        TimerUtils.addTimers(new TimerUtils.iTimer() {
            @Override
            public void timer() {
                if (runing) {
                    changeWifiImage();
                    changeBluetoothImage();
                }
            }
        });
    }

    public void initView() {
        int currentBrightness = BrightnessUtils.getBrightness();
        seekBar.setProgress1(currentBrightness);
        initBrightnessImage(getContext(), iv_brightness, currentBrightness);
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

            SettingUtils.setBrightness(getContext(), currentBrightness);
            seekBar.setProgress1(currentBrightness);
        } else if (v.getId() == R.id.llLock) {
//            int currentBrightness = 0;
//            SettingUtils.setBrightness(getContext(), currentBrightness);
//            initBrightnessImage(getContext(), iv_brightness, currentBrightness);
//            seekBar.setProgress1(currentBrightness);
        }
    }


    protected void changeBluetoothImage() {

        int blueState = BluetoothUtils.getState();
        Log.d("ThreadUtils", "----------->blueState = " + blueState);

        if (blueState == BluetoothAdapter.STATE_CONNECTED) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_on);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.main_color));
            textBluetooth.setText("?????????");
            textBluetooth.setTextColor(getContext().getColor(R.color.main_color));
        } else if (blueState == BluetoothAdapter.STATE_ON) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_on);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.main_color));
            textBluetooth.setText("?????????");
            textBluetooth.setTextColor(getContext().getColor(R.color.main_color));
        } else if (blueState == BluetoothAdapter.STATE_OFF) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_off);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.grey_color));
            textBluetooth.setText("?????????");
            textBluetooth.setTextColor(getContext().getColor(R.color.grey_color));
        } else if (blueState == BluetoothAdapter.STATE_TURNING_OFF
                || blueState == BluetoothAdapter.STATE_TURNING_ON
                || blueState == BluetoothAdapter.STATE_DISCONNECTING
                || blueState == BluetoothAdapter.STATE_CONNECTING) {
            iv_bluetooth.setImageResource(R.mipmap.bluetooth_ing);
            iv_bluetooth.setColorFilter(getContext().getColor(R.color.second_color));
            if (blueState == BluetoothAdapter.STATE_TURNING_OFF) {
                textBluetooth.setText("?????????...");
            } else if (blueState == BluetoothAdapter.STATE_TURNING_ON) {
                textBluetooth.setText("?????????...");
            } else if (blueState == BluetoothAdapter.STATE_DISCONNECTING) {
                textBluetooth.setText("?????????...");
            } else{
                textBluetooth.setText("?????????...");
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
            textWIFI.setText("?????????");
            textWIFI.setTextColor(getContext().getColor(R.color.main_color));
        } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabled);
            iv_wifi.setColorFilter(getContext().getColor(R.color.main_color));
            textWIFI.setText("?????????");
            textWIFI.setTextColor(getContext().getColor(R.color.main_color));
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_disabled);
            iv_wifi.setColorFilter(getContext().getColor(R.color.grey_color));
            textWIFI.setText("?????????");
            textWIFI.setTextColor(getContext().getColor(R.color.grey_color));
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLING || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabling);
            iv_wifi.setColorFilter(getContext().getColor(R.color.second_color));
            if (wifiState == WifiManager.WIFI_STATE_DISABLING) {
                textWIFI.setText("?????????...");
            } else {
                textWIFI.setText("?????????...");
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
