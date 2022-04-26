package com.industio.ecigarette.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.industio.ecigarette.R;

public class CusSeek extends LinearLayout {
    private SeekBar seekBar;
    private Button btnMinus;
    private Button btnPlus;

    private int minValue;
    private int maxValue;
    private int seekBarStep;
    private int buttonStep;
    private OnSeekBarChangeListener onSeekBarChangeListener;

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener onSeekBarChangeListener) {
        this.onSeekBarChangeListener = onSeekBarChangeListener;
    }


    public CusSeek(Context context) {
        this(context, null);

    }

    public CusSeek(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusSeek(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_cus_seek, this, true);

        init(context, attrs);

    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CusSeek);
        minValue = typedArray.getInt(R.styleable.CusSeek_minValue, 0);
        maxValue = typedArray.getInt(R.styleable.CusSeek_maxValue, 100);
        seekBarStep = typedArray.getInt(R.styleable.CusSeek_seekBarStep, 5);
        buttonStep = typedArray.getInt(R.styleable.CusSeek_buttonStep, 1);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setMax(maxValue);
        seekBar.setMin(minValue);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);

        btnMinus.setOnClickListener(view -> {
            int progress = seekBar.getProgress();
            if (progress > minValue) {
                seekBar.setProgress(progress - buttonStep);
            }

            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onProgressChanged(seekBar, progress);
            }
        });
        btnPlus.setOnClickListener(view -> {
            int progress = seekBar.getProgress();
            if (progress < maxValue) {
                seekBar.setProgress(progress + buttonStep);
            }
            if (onSeekBarChangeListener != null) {
                onSeekBarChangeListener.onProgressChanged(seekBar, progress);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                refreshView(seekBar);
            }
        });

    }

    private void refreshView(SeekBar seekBar) {
        int progress = seekBar.getProgress();//当前滑动到的值

        if (progress % seekBarStep > 0 && progress != maxValue) {
            int remainder = progress % seekBarStep;
            if (remainder > seekBarStep / 2 && seekBarStep * ((progress / seekBarStep) + 1) <= maxValue) {
                progress = seekBarStep * ((progress / seekBarStep) + 1);
            } else {
                progress = seekBarStep * (progress / seekBarStep);
            }
            seekBar.setProgress(progress);

        }
        if (onSeekBarChangeListener != null) {
            onSeekBarChangeListener.onProgressChanged(seekBar, progress);
        }
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(SeekBar seekBar, int progress);
    }
}
