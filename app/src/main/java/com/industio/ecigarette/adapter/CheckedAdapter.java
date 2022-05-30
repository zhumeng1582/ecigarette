package com.industio.ecigarette.adapter;

import static com.blankj.utilcode.util.ColorUtils.getColor;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.StringUtils;
import com.industio.ecigarette.R;
import com.industio.ecigarette.util.ClassicTemperatureUtils;

import java.util.List;

/**
 * Created by hupei on 2018/4/12.
 */

public class CheckedAdapter extends ArrayAdapter<String> {

    public CheckedAdapter(@NonNull Context context, List<String> objects) {
        super(context, R.layout.list_item, R.id.text1, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView) view.findViewById(R.id.text1);
        if (StringUtils.equals(getItem(position), ClassicTemperatureUtils.getCurrentTemperatureNameValue())) {
            textView.setTextColor(getColor(R.color.red_low_power));
        } else {
            textView.setTextColor(getColor(R.color.black));

        }

        return view;
    }


}