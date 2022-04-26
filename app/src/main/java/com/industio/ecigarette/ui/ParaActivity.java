package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;
import com.industio.ecigarette.databinding.ActivityParaBinding;
import com.industio.ecigarette.view.CusSeek;

public class ParaActivity extends AppCompatActivity {

    private ActivityParaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cusSeek.setOnSeekBarChangeListener((seekBar, progress) ->  binding.textValue.setText("" + progress));
    }
}