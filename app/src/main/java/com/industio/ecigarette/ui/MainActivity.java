package com.industio.ecigarette.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ClickUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnMode,
        }, this);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnMode) {
            startActivity(new Intent(MainActivity.this, ParaActivity.class));
        }
    }
}