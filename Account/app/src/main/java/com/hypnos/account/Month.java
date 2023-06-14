package com.hypnos.account;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Month extends Fragment {

    Month(String date) {
        this.date = date;
    }

    TextView tvNull;
    RecyclerView recyclerView;
    ArrayList<MonthItem> items = new ArrayList<MonthItem>();
    MonthAdapter adapter = new MonthAdapter(items);

    String date;

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.month, container, false);

        sqLiteOpenHelper = new SQLiteOpenHelper(view.getContext(), "account.db", null, 1);

        tvNull = (TextView) view.findViewById(R.id.tvNull);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        items.clear();

        addItem();

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

        adapter.setOnClickListener(new MonthAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position) {
                MonthItem item = items.get(position);

                ((Main) getActivity()).viewDay(item.getDate(), ((Main) getActivity()).dayNum, ((Main) getActivity()).monthNum);
            }
        });

        return view;
    }

    void addItem() {
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT idx, date, SUM(income), SUM(outcome) FROM account WHERE date LIKE '" + date.substring(0,7) + "%' GROUP BY date", null);

        while (cursor.moveToNext()) {
            String idx = cursor.getString(0);
            String date = cursor.getString(1);;
            String income = cursor.getString(2);
            String outcome = cursor.getString(3);

            MonthItem item = new MonthItem(idx, date, income, outcome);
            items.add(item);

        }

        cursor.close();
        database.close();
    }
}