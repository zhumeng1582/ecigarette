package com.industio.ecigarette.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.industio.ecigarette.R;

import java.util.ArrayList;
import java.util.List;

// ① 创建Adapter
public class NormalAdapter extends RecyclerView.Adapter<NormalAdapter.VH> {
    public void setCountNum(int countNum) {
        this.countNum = countNum;
        notifyDataSetChanged();
    }

    //② 创建ViewHolder
    public static class VH extends RecyclerView.ViewHolder {
        public AppCompatCheckedTextView title;

        public VH(View v) {
            super(v);
            title = v.findViewById(R.id.title);
        }
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
        notifyDataSetChanged();
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    private int selectIndex = 0;

    public int getCountNum() {
        return countNum;
    }

    private int countNum = 12;

    private ItemOnClick itemOnClick;

    public NormalAdapter(ItemOnClick itemOnClick) {
        this.itemOnClick = itemOnClick;
    }

    private List<String> mDatas = new ArrayList<String>(18) {{
        add("1");
        add("2");
        add("3");
        add("4");
        add("5");
        add("6");
        add("7");
        add("8");
        add("9");
        add("10");
        add("11");
        add("12");
        add("13");
        add("14");
        add("15");
        add("16");
        add("17");
        add("18");
    }};

    //③ 在Adapter中实现3个方法
    @Override
    public void onBindViewHolder(VH holder, int position) {
        holder.title.setText(mDatas.get(position));
        if (position < countNum) {
            holder.title.setEnabled(true);
            holder.title.setChecked(position == selectIndex);
            holder.title.setBackgroundResource(R.drawable.bg_item);
        } else {
            holder.title.setEnabled(false);
            holder.title.setBackgroundResource(R.color.color_enable);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemOnClick.itemOnClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        //LayoutInflater.from指定写法
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new VH(v);
    }

    public interface ItemOnClick {
        void itemOnClick(int index);
    }
}