package com.hypnos.account;

import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.DecimalFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>{

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    ArrayList<LocalDate> dayList;

    public CalendarAdapter(ArrayList<LocalDate> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.calendar_cell, parent, false);

        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {

        //날짜 변수에 담기
        LocalDate day = dayList.get(position);

        sqLiteOpenHelper = new SQLiteOpenHelper(holder.parentView.getContext(), "account.db", null, 1);


        //텍스트 색상 지정
        if( (position + 1) % 7 == 0){ //토요일 파랑

            holder.dayText.setTextColor(ContextCompat.getColor(holder.parentView.getContext(), R.color.income));

        }else if( position == 0 || position % 7 == 0){ //일요일 빨강

            holder.dayText.setTextColor(ContextCompat.getColor(holder.parentView.getContext(), R.color.outcome));
        }

        String check = "null";
        if(day == null){
            holder.dayText.setText("");
            holder.childView.setVisibility(View.INVISIBLE);
            holder.parentView.setBackgroundColor(Color.WHITE);
        }
        else{
            holder.dayText.setText(String.valueOf(day.getDayOfMonth()));

            //현재 날짜 색상 칠하기
            if(day.equals(LocalDate.now())){
                holder.dayText.setBackgroundColor(ContextCompat.getColor(holder.parentView.getContext(), R.color.plan));
                holder.dayText.setTextColor(Color.WHITE);
            }

            database = sqLiteOpenHelper.getReadableDatabase();
            Cursor cursor = database.rawQuery("SELECT SUM(income), SUM(outcome) FROM account WHERE date = '" + day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'", null);


            DecimalFormat decimalFormat = new DecimalFormat("###,###");

            while (cursor.moveToNext()) {

                String income = cursor.getString(0);
                String outcome = cursor.getString(1);
                if(income != null) {
                    check = "true";
                    if(income.length() > 6) {
                        income = income.substring(0, income.length()-4);
                        holder.dayIncome.setText(decimalFormat.format(Integer.parseInt(income)) + "만");
                    }
                    else {
                        holder.dayIncome.setText(decimalFormat.format(Integer.parseInt(income)));
                    }
                }
                else if(outcome != null) {
                    check = "true";
                    if(outcome.length() > 6) {
                        outcome = outcome.substring(0, outcome.length()-4);
                        holder.dayOutcome.setText(decimalFormat.format(Integer.parseInt(outcome)) + "만");
                    }
                    else {
                        holder.dayOutcome.setText(decimalFormat.format(Integer.parseInt(outcome)));
                    }
                }
                else {
                    check = "false";
                }
            }
            day.getEra();

            cursor.close();
            database.close();
        }
        final String finalCheck = check;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerViewClickListener.onItemClicked(position, finalCheck);
            }
        });
    }

    public interface RecyclerViewClickListener{
        void onItemClicked(int position, String check);
    }

    private CalendarAdapter.RecyclerViewClickListener recyclerViewClickListener;

    public void setOnClickListener(CalendarAdapter.RecyclerViewClickListener listener) {
        this.recyclerViewClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder{

        TextView dayText;
        TextView dayIncome, dayOutcome;

        View parentView;
        View childView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);

            dayText = itemView.findViewById(R.id.dayText);
            dayIncome = itemView.findViewById(R.id.dayIncome);
            dayOutcome = itemView.findViewById(R.id.dayOutcome);

            parentView = itemView.findViewById(R.id.parentView);
            childView = itemView.findViewById(R.id.childView);
        }
    }
}