package com.industio.ecigarette.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.MenuRes;
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
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;

import java.util.ArrayList;
import java.util.List;

public class ParaActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private ActivityParaBinding binding;
    private DevicePara devicePara;
    private NormalAdapter adapter;
    public final static int classics = 0;
    public final static int elegant = 1;
    public final static int strong = 2;

    public static void newInstance(Context mContext, int mode) {
        Intent intent = new Intent(mContext, ParaActivity.class);
        intent.putExtra("mode", mode);
        mContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityParaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int mode = getIntent().getIntExtra("mode", ParaActivity.classics);
        devicePara = CacheDataUtils.getDevicePara(mode);

        binding.cusSeekPreheatValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textPreheatValue.setText(progress + "℃");
            devicePara.setPreheatValue(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.预热温度, devicePara.getPreheatValue()));
            setChartData();
        });
        binding.cusSeekPreheatTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textPreheatTimeValue.setText(progress + "s");
            devicePara.setPreheatTimeValue(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.预热时长, devicePara.getPreheatTimeValue()));
            setChartData();
        });

        binding.cusSeekConstantTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textConstantTemperatureValue.setText(progress + " ℃");
            devicePara.setConstantTemperatureValue(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.恒温温度, devicePara.getConstantTemperatureValue()));

            setChartData();
        });

        binding.cusSeekConstantTemperatureTimeValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textConstantTemperatureTimeValue.setText(progress + "s");
            devicePara.setConstantTemperatureTimeValue(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.恒温时长, devicePara.getConstantTemperatureTimeValue()));

            setChartData();
        });

        binding.cusSeekNoOperationValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textNoOperationValue.setText(progress + "s");
            devicePara.setNoOperationValue(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.休眠时长, devicePara.getNoOperationValue()));

            setChartData();
        });
        binding.cusSeekTemperatureValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textTemperatureValue.setText(progress + "℃");
            int i = adapter.getSelectIndex();
            devicePara.getTemperature()[i] = progress;
            SerialController.getInstance().sendSync(DeviceConstant.sendCount(i + 1, devicePara.getTemperature()[i]));
            setChartData();
        });

        binding.cusSeekCountValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textCountValue.setText("" + progress);
            if (adapter.getSelectIndex() >= progress) {
                adapter.setSelectIndex(0);
            }
            adapter.setCountNum(progress);
            binding.cusSeekTemperatureValue.setCurProgress(devicePara.getTemperature()[adapter.getSelectIndex()]);
            devicePara.setCount(progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.发送口数, devicePara.getCount()));

            setChartData();
        });
        binding.textModeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new BottomSheetMenuDialogFragment.Builder(ParaActivity.this)
                        .setSheet(R.menu.list_mode)
                        .setTitle("选择模式")
                        .setListener(new BottomSheetListener() {
                            @Override
                            public void onSheetShown(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o) {
                            }

                            @Override
                            public void onSheetItemSelected(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, MenuItem menuItem, Object o) {
                                if (menuItem.getItemId() == R.id.classics) {
                                    binding.textModeChange.setText("经典（默认）");
                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.classics);
                                } else if (menuItem.getItemId() == R.id.elegant) {
                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.elegant);
                                    binding.textModeChange.setText("淡雅");
                                } else {
                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.strong);
                                    binding.textModeChange.setText("浓郁");
                                }
                                initData();
                            }

                            @Override
                            public void onSheetDismissed(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o, int i) {

                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 6);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(6, 30, 20));

        initData();

        initEvent();
    }



    private void initData() {

        initAdapter();
        initLineChart();
        showDevicePara();

    }

    private void initEvent() {
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnSave,
                binding.btnSaveAs,
                binding.btnExit,
                binding.btnReset
        }, this);
    }

    private void initAdapter() {
        adapter = new NormalAdapter(index -> {
            adapter.setSelectIndex(index);
            binding.cusSeekTemperatureValue.setCurProgress(devicePara.getTemperature()[adapter.getSelectIndex()]);
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
                entries1.add(new Entry(i + 2, devicePara.getConstantTemperatureValue() + devicePara.getTemperature()[i]));
            }
        }

        LineDataSet dataSet1 = new LineDataSet(entries1, "");
        dataSet1.setValueTextSize(9f);
        dataSet1.setDrawValues(true); // 不显示值
        dataSet1.setColor(getColor(R.color.red));
        dataSet1.setCircleHoleColor(getColor(R.color.white));
        dataSet1.setCircleColor(getColor(R.color.red));
        dataSet1.setLineWidth(1);
        dataSet1.setCircleHoleRadius(2);
        dataSet1.setCircleRadius(4);
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
        binding.cusSeekPreheatValue.setCurProgress(devicePara.getPreheatValue());
        binding.cusSeekPreheatTimeValue.setCurProgress(devicePara.getPreheatTimeValue());
        binding.cusSeekConstantTemperatureValue.setCurProgress(devicePara.getConstantTemperatureValue());
        binding.cusSeekConstantTemperatureTimeValue.setCurProgress(devicePara.getConstantTemperatureTimeValue());
        binding.cusSeekNoOperationValue.setCurProgress(devicePara.getNoOperationValue());

        binding.cusSeekCountValue.setCurProgress(devicePara.getCount());

    }

    private void saveDevicePara() {
        CacheDataUtils.saveDevicePara(devicePara);
        SerialController.getInstance().sendSync(DeviceConstant.saveCmd);

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
            saveAs();
        } else if (view == binding.btnReset) {
            devicePara = CacheDataUtils.getDefaultDevicePara(devicePara.getId());
            CacheDataUtils.saveDevicePara(devicePara);
            showDevicePara();
            SerialController.getInstance().sendSync(DeviceConstant.resetCmd);
        }
    }

    private void saveAs() {
        @MenuRes int id;
        if (devicePara.getId() == ParaActivity.classics) {
            id = R.menu.list_classics;
        } else if (devicePara.getId() == ParaActivity.elegant) {
            id = R.menu.list_elegant;
        } else {
            id = R.menu.list_strong;
        }

        new BottomSheetMenuDialogFragment.Builder(ParaActivity.this)
                .setSheet(id)
                .setTitle("另存为")
                .setListener(new BottomSheetListener() {
                    @Override
                    public void onSheetShown(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o) {
                    }

                    @Override
                    public void onSheetItemSelected(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, MenuItem menuItem, Object o) {
                        if (menuItem.getItemId() == R.id.classics) {
                            devicePara.setId(ParaActivity.classics);
                            binding.textModeChange.setText("经典（默认）");
                        } else if (menuItem.getItemId() == R.id.elegant) {
                            devicePara.setId(ParaActivity.elegant);
                            binding.textModeChange.setText("淡雅");
                        } else {
                            devicePara.setId(ParaActivity.strong);
                            binding.textModeChange.setText("浓郁");
                        }
                        saveDevicePara();

                    }

                    @Override
                    public void onSheetDismissed(BottomSheetMenuDialogFragment bottomSheetMenuDialogFragment, Object o, int i) {

                    }
                })
                .show(getSupportFragmentManager());
    }

}