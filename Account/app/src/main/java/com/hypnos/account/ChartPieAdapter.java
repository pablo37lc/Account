package com.hypnos.account;

import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ChartPieAdapter extends RecyclerView.Adapter<ChartPieAdapter.ViewHolder>  {

    ArrayList<ChartPieItem> items;

    ChartPieAdapter (ArrayList<ChartPieItem> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvPercent, tvTag, tvPrice;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPercent = itemView.findViewById(R.id.tvPercent);
            tvTag = itemView.findViewById(R.id.tvTag);
            tvPrice = itemView.findViewById(R.id.tvPrice);

        }
    }

    void clear() {
        items.clear();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chart_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        ChartPieItem item = items.get(position);

        int totPrice = 0;
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getPrice() != null) {
                totPrice += Integer.parseInt(items.get(i).getPrice());
            }
        }
        if(item.getPrice() != null) {
            Double percent = Integer.parseInt(item.getPrice())*1.0 / totPrice;
            holder.tvPercent.setText(Double.toString(Math.round(percent*1000)/10.0) + " %");
            holder.tvPrice.setText(decimalFormat.format(Integer.parseInt(item.getPrice())));
        }
        holder.tvPercent.setBackgroundColor(item.getColor());
        holder.tvTag.setText(item.getTag());

        if(recyclerViewClickListener != null) {
            int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickListener.onItemClicked(pos);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    recyclerViewLongClickListener.onItemClicked(pos);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private DayAdapter.RecyclerViewClickListener recyclerViewClickListener;
    private DayAdapter.RecyclerViewClickListener recyclerViewLongClickListener;

    public void setOnClickListener(DayAdapter.RecyclerViewClickListener listener) {
        this.recyclerViewClickListener = listener;
    }

    public void setOnLongClickListener(DayAdapter.RecyclerViewClickListener listener) {
        this.recyclerViewLongClickListener = listener;
    }
}
