package com.industio.ecigarette.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.SettingUtils;

public class ToggleToolWidget extends FrameLayout implements OnClickListener {


    /**
     * Called when the activity is first created.
     */
    protected ImageView iv_bluetooth = null;
    protected ImageView iv_wifi = null;
    protected ImageView iv_brightness = null;
    protected ImageView iv_lock = null;
    protected SeekBar seekBar = null;

    protected WifiManager wifiManager = null;

    protected final static int mostBrightness = 255;//
    protected final static int moreBrightness = 150;//
    protected final static int lowBrightness = 50;//

    public ToggleToolWidget(Context context) {
        this(context, null);
    }

    public ToggleToolWidget(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleToolWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.toggle_tools, this, true);

        init(context, attrs);

    }

    public void init(Context context, AttributeSet attrs) {

        iv_bluetooth = (ImageView) findViewById(R.id.bluetooth);//
        iv_bluetooth.setOnClickListener(this);
        IntentFilter intentFilter = new IntentFilter(
                BluetoothAdapter.ACTION_STATE_CHANGED);
        getContext().registerReceiver(new BluetoothBroadcastReceiver(), intentFilter);//ע�������
        changeBluetoothImage();

        wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        iv_wifi = (ImageView) findViewById(R.id.wifi);
        iv_wifi.setOnClickListener(this);
        IntentFilter wifiIntentFilter = new IntentFilter(
                WifiManager.WIFI_STATE_CHANGED_ACTION);
        getContext().registerReceiver(new WifiBroadcastReceiver(), wifiIntentFilter);
        changeWifiImage();

        iv_brightness = (ImageView) findViewById(R.id.brightness);
        iv_brightness.setOnClickListener(this);
        seekBar = findViewById(R.id.seekBar);
        seekBar.setMin(lowBrightness);
        seekBar.setMax(mostBrightness);


        int currentBrightness = BrightnessUtils.getBrightness();
        seekBar.setProgress(currentBrightness);
        initBrightnessImage(currentBrightness);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setBrightness(i);
                initBrightnessImage(i);
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
    }

    private void initBrightnessImage(int currentBrightness) {
        int current = getNearestNumber(currentBrightness, mostBrightness, moreBrightness, lowBrightness);
        if (current == mostBrightness) {
            iv_brightness.setImageResource(R.mipmap.brightness_most);
            iv_brightness.setColorFilter(getContext().getColor(R.color.main_color));
        } else if (current == lowBrightness) {
            iv_brightness.setImageResource(R.mipmap.brightness_low);
            iv_brightness.setColorFilter(getContext().getColor(R.color.grey_color));
        } else {
            iv_brightness.setImageResource(R.mipmap.brightness_more);
            iv_brightness.setColorFilter(getContext().getColor(R.color.main_color));
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bluetooth) {
            if (BluetoothUtils.isBlueOn()) {
                BluetoothUtils.offBlueTooth();
            } else {
                BluetoothUtils.onBlueTooth();
            }

        } else if (v.getId() == R.id.wifi) {
            wifiManager.setWifiEnabled(!wifiManager.isWifiEnabled());
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

            setBrightness(currentBrightness);
            seekBar.setProgress(currentBrightness);
        } else if (v.getId() == R.id.lock) {
            SettingUtils.sysLock(getContext());
        }
    }

    private void setBrightness(int currentBrightness) {
        WindowManager.LayoutParams lp = ((Activity) getContext()).getWindow().getAttributes();
        lp.screenBrightness = currentBrightness / 255f;
        ((Activity) getContext()).getWindow().setAttributes(lp);
        BrightnessUtils.setBrightness(currentBrightness);
    }

    @SuppressLint("MissingPermission")
    protected void changeBluetoothImage() {
        PermissionUtils.permission(Manifest.permission.BLUETOOTH).callback(new PermissionUtils.SimpleCallback() {
            @Override
            public void onGranted() {
                int blueState = BluetoothUtils.getState();
                if (blueState == BluetoothAdapter.STATE_ON) {
                    iv_bluetooth.setImageResource(R.mipmap.bluetooth_on);
                } else if (blueState == BluetoothAdapter.STATE_OFF) {
                    iv_bluetooth.setImageResource(R.mipmap.bluetooth_off);
                } else if (blueState == BluetoothAdapter.STATE_TURNING_OFF
                        || blueState == BluetoothAdapter.STATE_TURNING_ON) {
                    iv_bluetooth.setImageResource(R.mipmap.bluetooth_ing);
                }
            }

            @Override
            public void onDenied() {

            }
        }).request();

    }

    protected void changeWifiImage() {
        int wifiState = wifiManager.getWifiState();
        if (wifiState == WifiManager.WIFI_STATE_DISABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_disabled);
        } else if (wifiState == WifiManager.WIFI_STATE_ENABLED) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabled);
        } else if (wifiState == WifiManager.WIFI_STATE_DISABLING
                || wifiState == WifiManager.WIFI_STATE_ENABLING) {
            iv_wifi.setImageResource(R.mipmap.wifi_enabling);
        }
    }


    private int getNearestNumber(int destNumber, int... numbers) {
        int diffValue = Integer.MAX_VALUE;
        int nearestNumber = numbers[0];
        for (int i = 0; i < numbers.length; i++) {
            System.out.println(diffValue + "   " + Math.abs(destNumber - numbers[i]));
            if (diffValue > Math.abs(destNumber - numbers[i])) {
                diffValue = Math.abs(destNumber - numbers[i]);
                nearestNumber = numbers[i];
            }
        }
        return nearestNumber;
    }

    class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (iv_bluetooth == null) {
                return;
            }
            changeBluetoothImage();
        }
    }

    class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            changeWifiImage();
        }
    }

}
