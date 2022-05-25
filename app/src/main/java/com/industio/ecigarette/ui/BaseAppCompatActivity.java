package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.util.ChargeUtils;
import com.industio.ecigarette.util.SettingUtils;
import com.industio.ecigarette.util.TimerUtils;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;
    private View llLock;
    protected TimerUtils.iTimer iTimer;
    protected ChargeUtils.iCharge iCharge;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                BaseAppCompatActivity.this.onScroll(e1, e2, distanceX, distanceY);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                BaseAppCompatActivity.this.onSingleTapUp(e);
                return super.onSingleTapUp(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (llLock != null && CacheDataUtils.getLockScreenSwitch()) {
                    if (llLock.getVisibility() == View.GONE) {
                        llLock.setVisibility(View.VISIBLE);
                    } else {
                        llLock.setVisibility(View.GONE);
                    }
                }

                return super.onDoubleTap(e);
            }
        });
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ChargeUtils.addCharges(iCharge);
        TimerUtils.addTimers(iTimer);

        llLock = getLock();
        if (llLock != null) {
            llLock.setOnClickListener(new ClickUtils.OnMultiClickListener(2) {
                @Override
                public void onTriggerClick(View v) {
                    llLock.setVisibility(View.GONE);
                }

                @Override
                public void onBeforeTriggerClick(View v, int count) {

                }
            });
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        ChargeUtils.removeCharges(iCharge);
        TimerUtils.removeTimers(iTimer);
    }

    public abstract View getLock();

    public void onSingleTapUp(MotionEvent e) {
    }

    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
