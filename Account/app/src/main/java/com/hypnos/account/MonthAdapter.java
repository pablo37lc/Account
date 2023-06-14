package com.hypnos.account;

import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {

    ArrayList<MonthItem> items;

    public MonthAdapter(ArrayList<MonthItem> items) {
        this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDay, tvIncome, tvOutcome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.tvDay);
            tvIncome = itemView.findViewById(R.id.tvIncome);
            tvOutcome = itemView.findViewById(R.id.tvOutcome);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DecimalFormat decimalFormat = new DecimalFormat("###,###");

        MonthItem item = items.get(position);

        String date;
        String dateString = item.getDate().substring(8,10);
        if(dateString.substring(0,1).equals("0")) {
            date = dateString.substring(1,2);
        }
        else {
            date = dateString;
        }
        holder.tvDay.setText(date + "일");

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
