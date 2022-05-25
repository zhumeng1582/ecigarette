package com.industio.ecigarette.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.StringUtils;
import com.hjy.bluetooth.entity.BluetoothDevice;
import com.industio.ecigarette.R;

import java.util.List;

public class BluetoothListAdapter extends RecyclerView.Adapter<BluetoothListAdapter.VH> {


    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public TextView mac;
        public TextView name;

        public VH(View v) {
            super(v);
            mac = v.findViewById(R.id.mac);
            name = v.findViewById(R.id.name);
        }
    }

    private ItemOnClick itemOnClick;

    public BluetoothListAdapter(List<BluetoothDevice> mDatas, ItemOnClick itemOnClick) {
        this.itemOnClick = itemOnClick;
        this.mDatas = mDatas;
    }

    private List<BluetoothDevice> mDatas;

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, @SuppressLint("RecyclerView") int position) {
        if (StringUtils.isEmpty(mDatas.get(position).getName())) {
            holder.name.setText("未知");
        } else {
            holder.name.setText(mDatas.get(position).getName());
        }
        holder.mac.setText(mDatas.get(position).getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnClick.itemOnClick(holder.getAdapterPosition(), mDatas.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth, parent, false);
        return new VH(v);
    }

    public interface ItemOnClick {
        void itemOnClick(int index, BluetoothDevice device);
    }
}