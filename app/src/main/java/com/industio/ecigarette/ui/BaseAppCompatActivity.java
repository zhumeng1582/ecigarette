package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.BrightnessUtils;
import com.industio.ecigarette.util.SettingUtils;

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    private GestureDetector mGestureDetector;

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
                int currentBrightness = BrightnessUtils.getBrightness();
                if (currentBrightness < 100) {
                    SettingUtils.setBrightness(BaseAppCompatActivity.this, 200);
                } else if (currentBrightness >= 175) {
                    SettingUtils.setBrightness(BaseAppCompatActivity.this, 0);
                }

                return super.onDoubleTap(e);
            }
        });
    }
    public void onSingleTapUp(MotionEvent e){

    }
    public void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
