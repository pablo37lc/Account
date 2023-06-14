package com.hypnos.account;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;


public class Day extends Fragment  {

    Day(String date) {
        this.date = date;
    }

    RecyclerView recyclerView;
    LinearLayout lyTitle;
    TextView tvNull;
    ArrayList<DayItem> items = new ArrayList<DayItem>();
    DayAdapter adapter = new DayAdapter(items);

    TextView tvDate, tvIncome, tvOutcome;
    String date;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    int totIncome = 0, totOutcome = 0;

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.day, container, false);

        sqLiteOpenHelper = new SQLiteOpenHelper(view.getContext(), "account.db", null, 1);

        tvNull = (TextView) view.findViewById(R.id.tvNull);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        lyTitle = (LinearLayout) view.findViewById(R.id.lyTitle);

        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvIncome = (TextView) view.findViewById(R.id.tvIncome);
        tvOutcome = (TextView) view.findViewById(R.id.tvOutcome);

        String dateString = date.substring(8,10);
        String day;
        if(dateString.substring(0,1).equals("0")) {
            day = dateString.substring(1,2);
        }
        else {
            day = dateString;
        }

        tvDate.setText(day + "일");

        adapter.clear();

        addItem();

        lyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Add.class);
                intent.putExtra("date", date);
                intent.putExtra("from", "day");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
                getActivity().finish();
            }
        });

        recyclerView.setAdapter(adapter);

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                tvNull.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
            }
        });

        adapter.setOnClickListener(new DayAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position) {
                DayItem item = items.get(position);
                Intent intent = new Intent(getActivity(), Edit.class);
                intent.putExtra("idx", item.getIdx());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
                getActivity().finish();
            }
        });

        return view;
    }

    void addItem() {
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM account WHERE date = '" + date + "'", null);

        while (cursor.moveToNext()) {
            String idx = cursor.getString(0);
            String tag = cursor.getString(2);
            String use = cursor.getString(3);
            String income = cursor.getString(4);
            String outcome = cursor.getString(5);

            DayItem item = new DayItem(idx, tag, use, income, outcome);
            items.add(item);

            if(income != null) {
                totIncome += Integer.parseInt(income);
            }
            if(outcome != null) {
                totOutcome += Integer.parseInt(outcome);
            }
        }

        tvIncome.setText(decimalFormat.format(totIncome));
        tvOutcome.setText(decimalFormat.format(totOutcome));

        cursor.close();
        database.close();
    }

}