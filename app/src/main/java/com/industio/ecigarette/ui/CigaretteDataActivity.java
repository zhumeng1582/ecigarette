package com.industio.ecigarette.ui;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.blankj.utilcode.util.ArrayUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.industio.ecigarette.R;
import com.industio.ecigarette.bean.CigaretteData;
import com.industio.ecigarette.databinding.ActivityCigaretteDataBinding;
import com.industio.ecigarette.util.BluetoothUtils;
import com.industio.ecigarette.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class CigaretteDataActivity extends BaseAppCompatActivity {
    private ActivityCigaretteDataBinding binding;
    private final ArrayList<CigaretteData> list = CigaretteData.getCigaretteDataSet();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCigaretteDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                setChartData();
            }
        });
        initLineChart();
    }

    @Override
    public View getLock() {
        return binding.textLock;
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

    private void initLineChart() {
        binding.chart1.setExtraOffsets(5, 10, 5, 10);
        binding.chart1.getDescription().setEnabled(false); // 不显示描述
        binding.chart1.getLegend().setEnabled(false);  // 不显示图例
        binding.chart1.animate();

        binding.chart1.animateX(1500);
        setChartData();
        setCigData();
    }

    private void setChartData() {
        int[] data;
        if (binding.radioDay.isChecked()) {
            data = DateUtils.getDataDay(list);
        } else {
            data = DateUtils.getDataWeek(list);
        }

        setAxis(data); // 设置坐标轴
        setData(data); // 设置数据
    }

    private void setData(int[] data) {
        List<ILineDataSet> sets = new ArrayList<>();

        List<Entry> entries1 = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            entries1.add(new Entry(i, data[i]));
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
        dataSet1.setValueFormatter(new ValueFormatter() {

            @Override
            public String getPointLabel(Entry entry) {
                return "" + (int) entry.getY();
            }
        });

        LineData lineData = new LineData(sets);
        binding.chart1.clear();
        binding.chart1.setData(lineData);
        binding.chart1.setScaleEnabled(false); // 设置不能缩放
        binding.chart1.setTouchEnabled(false); // 设置不能触摸
//        binding.chart1.setDrawBorders(true);

    }

    private int getMax(int[] data) {
        int max = 0;
        for (int datum : data) {
            max = Math.max(max, datum);
        }
        return max;
    }

    private void setAxis(int[] data) {

        // x轴
        XAxis xAxis = binding.chart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setAxisMaximum(data.length-1);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(data.length-1);
        xAxis.setTextSize(9f);
        xAxis.setValueFormatter(new ValueFormatter() {

            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                if (binding.radioDay.isChecked()) {
                    return  ((int) value * 2)+"时";
                } else {
                    return TimeUtils.millis2String(TimeUtils.getNowMills() - 86400000L * (6 - (int) value), DateUtils.sdfyDD);
                }
            }
        });

        int max = getMax(data);
        // 右y轴
        YAxis yAxis_right = binding.chart1.getAxisRight();
        yAxis_right.setAxisMinimum(0);
        yAxis_right.setAxisMaximum(max + 1);
        yAxis_right.setLabelCount(5, true);
        yAxis_right.setTextSize(10f);

        // 左y轴
        YAxis yAxis_left = binding.chart1.getAxisLeft();
        yAxis_left.setAxisMinimum(0);
        yAxis_left.setAxisMaximum(max + 1);
        yAxis_left.setLabelCount(5, true);
        yAxis_left.setTextSize(10f);
    }

    void setCigData() {
        if (CollectionUtils.isNotEmpty(list)) {
            CigaretteData recent = list.get(list.size() - 1);
            binding.textRecentTimeValue.setText(TimeUtils.millis2String(recent.getTime(), TimeUtils.getSafeDateFormat("MM-dd HH:mm")));
            binding.textRecentCountValue.setText("" + recent.getCount());
            binding.textRecentTimeLongValue.setText("" + recent.getTimeLong());
            binding.textDayCountValue.setText("" + DateUtils.getCountDay(list));
        }
    }
}
