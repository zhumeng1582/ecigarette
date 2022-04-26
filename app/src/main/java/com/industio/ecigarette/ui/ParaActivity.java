package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.industio.ecigarette.R;
import com.industio.ecigarette.view.CusSeek;

public class ParaActivity extends AppCompatActivity {
    private CusSeek cusSeek;
    private TextView textValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_para);
        cusSeek = findViewById(R.id.cusSeek);
        textValue = findViewById(R.id.textValue);
        cusSeek.setOnSeekBarChangeListener((seekBar, progress) -> textValue.setText("" + progress));
    }
}