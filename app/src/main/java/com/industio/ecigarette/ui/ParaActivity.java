package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.industio.ecigarette.R;
import com.industio.ecigarette.adapter.NormalAdapter;
import com.industio.ecigarette.bean.DevicePara;
import com.industio.ecigarette.databinding.ActivityParaBinding;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.view.GridSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ParaActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityParaBinding binding;
    private DevicePara devicePara;
    private int selectIndex = 0;
    private NormalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.cusSeekPreheatValue.setOnSeekBarChangeListener((seekBar, progress) -> binding.textPreheatValue.setText("" + progress + "℃"));
        binding.cusSeekPreheatTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> binding.textPreheatTimeValue.setText("" + progress + "s"));
        binding.cusSeekConstantTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> binding.textConstantTemperatureValue.setText("" + progress + " ℃"));
        binding.cusSeekConstantTemperatureTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> binding.textConstantTemperatureTimeValue.setText("" + progress + "s"));
        binding.cusSeekNoOperationValue.setOnSeekBarChangeListener((seekBar, progress) -> binding.textNoOperationValue.setText("" + progress + "s"));
        binding.cusSeekTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textTemperatureValue.setText("" + progress + "℃");
            devicePara.getTemperature()[selectIndex] = progress;
        });

        binding.cusSeekCountValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textCountValue.setText("" + progress);
            if (selectIndex > progress) {
                selectIndex = 0;
            }
            adapter.setCountNum(progress);
            adapter.setSelectIndex(selectIndex);
            adapter.notifyDataSetChanged();
        });

        initAdapter();

        initLineChart();
        devicePara = CacheDataUtils.getDevicePara(0);

        showDevicePara();

        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnSave,
                binding.btnSaveAs,
                binding.btnExit,
                binding.btnReset
        }, this);

    }

    private void initAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(6, 30, 20));

        adapter = new NormalAdapter(index -> {
            selectIndex = index;
            adapter.setSelectIndex(selectIndex);
            adapter.notifyDataSetChanged();
        });
        binding.recyclerView.setAdapter(adapter);

    }

    private void initLineChart() {
        binding.chart1.setExtraOffsets(5, 10, 5, 10);
        binding.chart1.getDescription().setEnabled(false); // 不显示描述
        binding.chart1.getLegend().setEnabled(false);  // 不显示图例
        binding.chart1.animate();

        binding.chart1.animateX(1500);

        setAxis(); // 设置坐标轴
        setData(); // 设置数据
    }

    private void setData() {
        List<ILineDataSet> sets = new ArrayList<>();

        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 0));
        entries1.add(new Entry(1, 380));
        entries1.add(new Entry(2, 355));
        entries1.add(new Entry(3, 350));
        entries1.add(new Entry(4, 250));
        entries1.add(new Entry(5, 280));
        entries1.add(new Entry(6, 300));
        entries1.add(new Entry(7, 320));
        entries1.add(new Entry(8, 340));
        entries1.add(new Entry(9, 380));
        entries1.add(new Entry(10, 380));
        entries1.add(new Entry(11, 360));
        entries1.add(new Entry(12, 370));
        entries1.add(new Entry(13, 380));
        entries1.add(new Entry(14, 390));
        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setValueTextSize(9f);
        dataSet1.setDrawValues(true); // 不显示值
        dataSet1.setColor(getColor(R.color.red));
        dataSet1.setCircleHoleColor(getColor(R.color.white));
        dataSet1.setCircleColor(getColor(R.color.red));
        sets.add(dataSet1);

        LineData lineData = new LineData(sets);
        binding.chart1.setData(lineData);
        binding.chart1.setScaleEnabled(false); // 设置不能缩放
        binding.chart1.setTouchEnabled(false); // 设置不能触摸
//        binding.chart1.setDrawBorders(true);
    }

    private void setAxis() {
        // x轴
        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMaximum(15);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(15);
        xAxis.setTextSize(12f);

        // 右y轴
        YAxis yAxis_right = binding.chart1.getAxisRight();
        yAxis_right.setAxisMinimum(0);
        yAxis_right.setAxisMaximum(570);
        yAxis_right.setLabelCount(4, true);
        yAxis_right.setTextSize(10f);

        // 左y轴
        YAxis yAxis_left = binding.chart1.getAxisLeft();
        yAxis_left.setAxisMinimum(0);
        yAxis_left.setAxisMaximum(570);
        yAxis_left.setLabelCount(4, true);
        yAxis_left.setTextSize(10f);
    }

    private void showDevicePara() {
        binding.cusSeekPreheatValue.setProgress(devicePara.getPreheatValue());
        binding.cusSeekPreheatTimeValue.setProgress(devicePara.getPreheatTimeValue());
        binding.cusSeekConstantTemperatureValue.setProgress(devicePara.getConstantTemperatureValue());
        binding.cusSeekConstantTemperatureTimeValue.setProgress(devicePara.getConstantTemperatureTimeValue());
        binding.cusSeekNoOperationValue.setProgress(devicePara.getNoOperationValue());
    }

    private void saveDevicePara() {
        devicePara.setPreheatValue(binding.cusSeekPreheatValue.getProgress());
        devicePara.setPreheatTimeValue(binding.cusSeekPreheatTimeValue.getProgress());
        devicePara.setConstantTemperatureValue(binding.cusSeekConstantTemperatureValue.getProgress());
        devicePara.setConstantTemperatureTimeValue(binding.cusSeekConstantTemperatureTimeValue.getProgress());
        devicePara.setNoOperationValue(binding.cusSeekNoOperationValue.getProgress());

        CacheDataUtils.saveDevicePara(devicePara);

    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnSave) {
            saveDevicePara();
            ToastUtils.showShort("保存成功");
        } else if (view == binding.btnExit) {
            finish();
        } else if (view == binding.btnSaveAs) {
        } else if (view == binding.btnReset) {
            devicePara = CacheDataUtils.getDefaultDevicePara(devicePara.getId());
            CacheDataUtils.saveDevicePara(devicePara);
            showDevicePara();
        }
    }
}