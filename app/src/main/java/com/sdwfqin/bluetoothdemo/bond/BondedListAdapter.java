package com.sdwfqin.bluetoothdemo.bond;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.industio.ecigarette.R;

import java.util.List;

/**
 * 描述：
 *
 * @author zhangqin
 * @date 2018/5/30
 */
public class BondedListAdapter extends BaseQuickAdapter<BluetoothDevice, BaseViewHolder> {

    public BondedListAdapter(@Nullable List<BluetoothDevice> data) {
        super(R.layout.item_dev_list, data);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.title, item.getName())
                .setText(R.id.sub_title, item.getAddress());
    }
}
