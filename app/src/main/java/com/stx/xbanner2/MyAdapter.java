package com.stx.xbanner2;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * @author: xiaohaibin.
 * @time: 2020/1/9
 * @mail:xhb_199409@163.com
 * @github:https://github.com/xiaohaibin
 * @describe:
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.PagerViewHolder> {

    private List<String> dataList;

    public MyAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public PagerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PagerViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PagerViewHolder holder, int position) {
        holder.tvText.setText(position + "");
        holder.tvText.setBackgroundColor(Color.parseColor(dataList.get(position)));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    class PagerViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;

        public PagerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tv_text);
        }
    }
}
