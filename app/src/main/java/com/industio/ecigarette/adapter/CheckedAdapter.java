package com.industio.ecigarette.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.industio.ecigarette.R;

import java.util.List;

/**
 * Created by hupei on 2018/4/12.
 */

public class CheckedAdapter extends ArrayAdapter<String> {

    public CheckedAdapter(@NonNull Context context, List<String> objects) {
        super(context, R.layout.list_item, R.id.text1, objects);
    }

}