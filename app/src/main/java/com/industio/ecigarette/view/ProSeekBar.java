package com.industio.ecigarette.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ProSeekBar extends SeekBar {
    int min;

    public ProSeekBar(@NonNull Context context) {
        super(context);
    }

    public ProSeekBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ProSeekBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public synchronized void setMax1(int max) {
        setMax(max - min);
    }

    public synchronized void setProgress1(int progress) {
        setProgress(progress - min);
    }

    public synchronized int getProgress1() {
        return getProgress() + min;
    }

    public synchronized void setMin1(int min) {
        setMin(0);
        this.min = min;
    }

    public void setOnSeekBarChangeListener1(OnSeekBarChangeListener onSeekBarChangeListener) {
        super.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                onSeekBarChangeListener.onProgressChanged(seekBar, i + min, b);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onSeekBarChangeListener.onStartTrackingTouch(seekBar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onSeekBarChangeListener.onStopTrackingTouch(seekBar);
            }
        });
    }

}
