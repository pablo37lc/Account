package com.hypnos.account;

import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    ArrayList<DayItem> items;

    void clear() {
        items.clear();
    }
    public DayAdapter (ArrayList<DayItem> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvTag, tvUse, tvIncome, tvOutcome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTag = itemView.findViewById(R.id.tvTag);
            tvUse = itemView.findViewById(R.id.tvUse);
            tvIncome = itemView.findViewById(R.id.tvIncome);
            tvOutcome = itemView.findViewById(R.id.tvOutcome);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        DayItem item = items.get(position);
        holder.tvTag.setText(item.getTag());
        holder.tvUse.setText(item.getUse());
        if(item.getIncome() != null) {
            holder.tvIncome.setText(decimalFormat.format(Integer.parseInt(item.getIncome())));
        }
        if(item.getOutcome() != null) {
            holder.tvOutcome.setText(decimalFormat.format(Integer.parseInt(item.getOutcome())));
        }

        if(recyclerViewClickListener != null) {
            int pos = position;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewClickListener.onItemClicked(pos);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface RecyclerViewClickListener{
        void onItemClicked(int position);
    }

    private RecyclerViewClickListener recyclerViewClickListener;

    public void setOnClickListener(RecyclerViewClickListener listener) {
        this.recyclerViewClickListener = listener;
    }

}
