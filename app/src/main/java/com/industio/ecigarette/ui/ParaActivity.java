package com.industio.ecigarette.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.blankj.utilcode.util.ClickUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.industio.ecigarette.R;
import com.industio.ecigarette.adapter.CheckedAdapter;
import com.industio.ecigarette.adapter.NormalAdapter;
import com.industio.ecigarette.bean.DevicePara;
import com.industio.ecigarette.databinding.ActivityParaBinding;
import com.industio.ecigarette.serialcontroller.SerialController;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.CacheDataUtils;
import com.industio.ecigarette.util.ChargeUtils;
import com.industio.ecigarette.util.ClassicTemperatureUtils;
import com.industio.ecigarette.util.DeviceConstant;
import com.industio.ecigarette.util.TimerUtils;
import com.industio.ecigarette.view.GridSpaceItemDecoration;
import com.kennyc.bottomsheet.BottomSheetListener;
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment;
import com.mylhyl.circledialog.CircleDialog;

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
        ClassicTemperatureUtils.clearTemperatureValue();
        devicePara = CacheDataUtils.getDevicePara(mode);

        binding.cusSeekPreheatValue.setOnSeekBarChangeListener((seekBar, progress) -> {
            binding.textPreheatValue.setText(progress + "℃");

            ClassicTemperatureUtils.setPreheatValue(devicePara.getId(), progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.预热温度, progress));
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
            ClassicTemperatureUtils.setConstantTemperatureValue(devicePara.getId(), progress);
            SerialController.getInstance().sendSync(DeviceConstant.getSendData(DeviceConstant.CMD.恒温温度, progress));

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
                                    devicePara.setId(ParaActivity.classics);
//                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.classics);
                                } else if (menuItem.getItemId() == R.id.elegant) {
                                    devicePara.setId(ParaActivity.elegant);
//                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.elegant);
                                } else {
                                    devicePara.setId(ParaActivity.strong);
//                                    devicePara = CacheDataUtils.getDevicePara(ParaActivity.strong);
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
        binding.recyclerView.addItemDecoration(new GridSpaceItemDecoration(6, 20, 20));

        initData();

        initEvent();
    }

    @Override
    public View getLock() {
        return binding.textLock;
    }


    private void initData() {

        if (devicePara.getId() == ParaActivity.classics) {
            binding.textModeChange.setText("经典（默认）");
        } else if (devicePara.getId() == ParaActivity.elegant) {
            binding.textModeChange.setText("淡雅");
        } else {
            binding.textModeChange.setText("浓郁");
        }
        initAdapter();
        initLineChart();
        showDevicePara();
    }

    @Override
    public void timer() {

        if (NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            binding.included.iconHomeWifi.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeWifi.setVisibility(View.GONE);
        }
        if (BluetoothUtils.getState() == BluetoothAdapter.STATE_CONNECTED) {
            binding.included.iconHomeBluetooth.setVisibility(View.VISIBLE);
        } else {
            binding.included.iconHomeBluetooth.setVisibility(View.GONE);
        }
    }

    @Override
    public void charge(boolean isCharge, int power) {
        if (power <= 5) {
            binding.included.batteryView.setPower(power * 20);
        }
        if (isCharge) {
            binding.included.imageChange.setVisibility(View.VISIBLE);
        } else {
            binding.included.imageChange.setVisibility(View.GONE);
        }
    }

    private void initEvent() {
        ClickUtils.applySingleDebouncing(new View[]{
                binding.btnSave,
                binding.btnImport,
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

        entries1.add(new Entry(1, ClassicTemperatureUtils.getPreheatValue(devicePara.getId())));

        for (int i = 0; i < 18; i++) {
            if (adapter.getCountNum() > i) {
                entries1.add(new Entry(i + 2, ClassicTemperatureUtils.getConstantTemperatureValue(devicePara.getId()) + devicePara.getTemperature()[i]));
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
        binding.cusSeekPreheatValue.setCurProgress(ClassicTemperatureUtils.getPreheatValue(devicePara.getId()));
        binding.cusSeekPreheatTimeValue.setCurProgress(devicePara.getPreheatTimeValue());
        binding.cusSeekConstantTemperatureValue.setCurProgress(ClassicTemperatureUtils.getConstantTemperatureValue(devicePara.getId()));
        binding.cusSeekConstantTemperatureTimeValue.setCurProgress(devicePara.getConstantTemperatureTimeValue());
        binding.cusSeekNoOperationValue.setCurProgress(devicePara.getNoOperationValue());

        binding.cusSeekCountValue.setCurProgress(devicePara.getCount());

    }

    private void sendSaveCmd() {
        SerialController.getInstance().sendSync(DeviceConstant.saveCmd);
    }


    @Override
    public void onClick(View view) {
        if (view == binding.btnSave) {
            sendSaveCmd();
        } else if (view == binding.btnImport) {
            importCache();
        } else if (view == binding.btnExit) {
            CacheDataUtils.saveDevicePara(devicePara);
            finish();
        } else if (view == binding.btnSaveAs) {
            saveAs();
        } else if (view == binding.btnReset) {
            devicePara = CacheDataUtils.getDefaultDevicePara(devicePara.getId());
            ClassicTemperatureUtils.resetTemperatureValue();

            CacheDataUtils.saveDevicePara(devicePara);
            showDevicePara();
            SerialController.getInstance().sendSync(DeviceConstant.resetCmd);
        }
    }

    private void importCache() {
        List<String> dataList = ClassicTemperatureUtils.getHashMapKeys();
        CheckedAdapter checkedAdapterR = new CheckedAdapter(this, dataList);

        new CircleDialog.Builder()
                .configDialog(params -> params.backgroundColorPress = Color.CYAN)
                .setItems(checkedAdapterR, (parent, view15, position15, id) -> {
                    ClassicTemperatureUtils.setCurrentTemperatureNameValue(dataList.get(position15));
                    ClassicTemperatureUtils.clearTemperatureValue();
                    showDevicePara();
                    return true;
                })
                .show(getSupportFragmentManager());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CacheDataUtils.saveDevicePara(devicePara);
    }

    private void saveAs() {
        new CircleDialog.Builder()
                .setCanceledOnTouchOutside(true)
                .setCancelable(true)
                .setTitle("另存为")
                .setInputHint("请输入名字")
                .setNegative("取消", null)
                .setPositiveInput("确定", (text, v) -> {
                    if (TextUtils.isEmpty(text)) {
                        v.setError("请输入内容");
                        return false;
                    } else {
                        Toast.makeText(ParaActivity.this, "另存成功", Toast.LENGTH_SHORT).show();
                        ClassicTemperatureUtils.saveAsTemperatureValue(text, ClassicTemperatureUtils.getClassicTemperatureValue());
                        return true;
                    }
                })
                .show(getSupportFragmentManager());
    }

}