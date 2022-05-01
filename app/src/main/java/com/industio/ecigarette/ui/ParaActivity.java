package com.industio.ecigarette.ui;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClickUtils;
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
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.view.GridSpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class ParaActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityParaBinding binding;
    private DevicePara devicePara;
    private NormalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityParaBinding.inflate(getLayoutInflater());
        devicePara = CacheDataUtils.getDevicePara(0);

        setContentView(binding.getRoot());

        binding.cusSeekPreheatValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textPreheatValue.setText("" + progress + "℃");
            devicePara.setPreheatValue(progress);
            setChartData();
        });
        binding.cusSeekPreheatTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textPreheatTimeValue.setText("" + progress + "s");
            devicePara.setPreheatTimeValue(progress);
            setChartData();
        });
        binding.cusSeekConstantTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textConstantTemperatureValue.setText("" + progress + " ℃");
            devicePara.setConstantTemperatureValue(progress);
            setChartData();
        });
        binding.cusSeekConstantTemperatureTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textConstantTemperatureTimeValue.setText("" + progress + "s");
            devicePara.setConstantTemperatureTimeValue(progress);
            setChartData();
        });
        binding.cusSeekNoOperationValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textNoOperationValue.setText("" + progress + "s");
            devicePara.setNoOperationValue(progress);
            setChartData();
        });
        binding.cusSeekTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textTemperatureValue.setText("" + progress + "℃");
            devicePara.getTemperature()[adapter.getSelectIndex()] = progress;
            setChartData();
        });

        binding.cusSeekCountValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textCountValue.setText("" + progress);
            if (adapter.getSelectIndex() >= progress) {
                adapter.setSelectIndex(0);
            }
            adapter.setCountNum(progress);
            binding.cusSeekTemperatureValue.setProgress(devicePara.getTemperature()[adapter.getSelectIndex()]);
            devicePara.setCount(progress);
            setChartData();
        });

        initAdapter();
        initLineChart();
        initData();

    }

    private void initData() {

        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnSave,
                binding.btnSaveAs,
                binding.btnExit,
                binding.btnReset
        }, this);

        showDevicePara();
    }

    private void initAdapter() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(6, 30, 20));

        adapter = new NormalAdapter(index -> {
            adapter.setSelectIndex(index);
            binding.cusSeekTemperatureValue.setProgress(devicePara.getTemperature()[adapter.getSelectIndex()]);
        });
        binding.recyclerView.setAdapter(adapter);

    }

    private void initLineChart() {
        binding.chart1.setExtraOffsets(5, 10, 5, 10);
        binding.chart1.getDescription().setEnabled(false); // 不显示描述
        binding.chart1.getLegend().setEnabled(false);  // 不显示图例
        binding.chart1.animate();

        binding.chart1.animateX(1500);
        setChartData();
    }

    private void setChartData() {
        setAxis(); // 设置坐标轴
        setData(); // 设置数据
    }

    private void setData() {
        List<ILineDataSet> sets = new ArrayList<>();

        List<Entry> entries1 = new ArrayList<>();
        entries1.add(new Entry(0, 0));

        entries1.add(new Entry(1, devicePara.getPreheatValue()));

        for (int i = 0; i < 18; i++) {
            if (adapter.getCountNum() > i) {
                entries1.add(new Entry(i+2, devicePara.getConstantTemperatureValue() + devicePara.getTemperature()[i]));
            }
        }

        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setValueTextSize(9f);
        dataSet1.setDrawValues(true); // 不显示值
        dataSet1.setColor(getColor(R.color.red));
        dataSet1.setCircleHoleColor(getColor(R.color.white));
        dataSet1.setCircleColor(getColor(R.color.red));
        sets.add(dataSet1);

        LineData lineData = new LineData(sets);
        binding.chart1.clear();
        binding.chart1.setData(lineData);
        binding.chart1.setScaleEnabled(false); // 设置不能缩放
        binding.chart1.setTouchEnabled(false); // 设置不能触摸
//        binding.chart1.setDrawBorders(true);

    }

    private void setAxis() {
        // x轴
        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setAxisMaximum(adapter.getCountNum() + 1);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(adapter.getCountNum() + 1);
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

        binding.cusSeekCountValue.setProgress(devicePara.getCount());

    }

    private void saveDevicePara() {
//        devicePara.setPreheatValue(binding.cusSeekPreheatValue.getProgress());
//        devicePara.setPreheatTimeValue(binding.cusSeekPreheatTimeValue.getProgress());
//        devicePara.setConstantTemperatureValue(binding.cusSeekConstantTemperatureValue.getProgress());
//        devicePara.setConstantTemperatureTimeValue(binding.cusSeekConstantTemperatureTimeValue.getProgress());
//        devicePara.setNoOperationValue(binding.cusSeekNoOperationValue.getProgress());
//        devicePara.setCount(binding.cusSeekCountValue.getProgress());

        CacheDataUtils.saveDevicePara(devicePara);

//        sendDataToDevice();
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

    public void sendDataToDevice() {
        SerialController.getInstance().send(DeviceConstant.startCmd);
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.预热温度, devicePara.getPreheatValue()));
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.预热时长, devicePara.getPreheatTimeValue()));
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.恒温温度, devicePara.getConstantTemperatureValue()));
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.恒温时长, devicePara.getConstantTemperatureTimeValue()));
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.休眠时长, devicePara.getNoOperationValue()));
        SerialController.getInstance().send(DeviceConstant.sendData(DeviceConstant.CMD.发送口数, devicePara.getCount()));
        for (int i = 0; i < 18; i++) {
            if(i<devicePara.getCount()){
                SerialController.getInstance().send(DeviceConstant.sendCount(i+1, devicePara.getTemperature()[i]));
            }
        }

    }
}