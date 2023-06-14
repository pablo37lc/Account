package com.hypnos.account;

import static com.github.mikephil.charting.utils.ColorTemplate.createColors;
import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class ChartLine extends Fragment {

    ChartLine(String date, String inout) {
        this.date = date;
        this.inout = inout;
    }
    String date;
    String inout;
    TextView tvNull;
    LineChart lineChart;
    ArrayList<Entry> line = new ArrayList<>();
    ArrayList<Entry> main;
    LineDataSet set;
    LineData data;

    RecyclerView recyclerView;
    ArrayList<ChartPieItem> items = new ArrayList<ChartPieItem>();
    ChartPieAdapter adapter = new ChartPieAdapter(items);

    ArrayList<String> tag = new ArrayList<>();
    ArrayList<Integer> sel = new ArrayList<>();
    String[][] price;

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_line, container, false);

        sqLiteOpenHelper = new SQLiteOpenHelper(view.getContext(), "account.db", null, 1);

        tvNull = (TextView) view.findViewById(R.id.tvNull);

        lineChart = view.findViewById(R.id.lineChart);

        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {

            @Override
            public String getFormattedValue(float value) {
                int num = (int) value;
                return month[num];
            }
        });

        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);

        line.clear();

        tag();
        price();
        addMain();
        addLine();

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // x축 데이터 표시 위치
        xAxis.setTextSize(10f);
        xAxis.setSpaceMin(0.3f); // Chart 맨 왼쪽 간격 띄우기
        xAxis.setSpaceMax(0.3f); // Chart 맨 오른쪽 간격 띄우기

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum((float) (lineChart.getYChartMax()));

        lineChart.setTouchEnabled(false);

        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);

        lineChart.setPinchZoom(false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter.clear();

        addPie();

        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new DayAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position) {
                ChartPieItem item = items.get(position);
                if(item.getColor() == Color.LTGRAY) {
                    if(inout.equals("outcome")) {
                        item.setColor(rainbow[position % 11]);
                    }
                    else {
                        item.setColor(ocean[position % 9]);
                    }
                    sel.add(position);
                    sel.sort(Comparator.naturalOrder());
                }
                else {
                    item.setColor(Color.LTGRAY);
                    sel.remove((Integer) position);
                }
                adapter.notifyItemChanged(position);

                line.clear();
                set.clear();

                addLine();

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();

            }
        });

        adapter.setOnLongClickListener(new DayAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position) {
                ChartPieItem item = items.get(position);
                if (sel.size() == 0 || (sel.size() == 1 && sel.get(0) == position)) {
                    for (int pos = 0; pos < items.size(); pos++) {
                        if(inout.equals("outcome")) {
                            items.get(pos).setColor(rainbow[pos % 11]);
                        }
                        else {
                            items.get(pos).setColor(ocean[pos % 9]);
                        }
                        sel.add(pos);
                    }
                }
                else {
                    for (int pos = 0; pos < items.size(); pos++) {
                        items.get(pos).setColor(Color.LTGRAY);
                    }

                    sel.clear();

                    if(inout.equals("outcome")) {
                        item.setColor(rainbow[position % 11]);
                    }
                    else {
                        item.setColor(ocean[position % 9]);
                    }

                    sel.add(position);
                }


                adapter.notifyItemRangeChanged(0, items.size());

                line.clear();
                set.clear();

                addLine();

                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }
        });

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                tvNull.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
            }
        });

        return view;
    }

    String[] month = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    void addMain() {
        main = new ArrayList<>();
        database = sqLiteOpenHelper.getReadableDatabase();

        for (int m = 0; m < 12; m++) {
            int totPrice = 0;
            for(int i = 0; i < sel.size(); i++) {
                totPrice += Integer.parseInt(price[sel.get(i)][m]);
            }
            main.add(new Entry(m, totPrice));
        }
        LineDataSet MainSet = new LineDataSet(main, "DataMain");

        MainSet.setColor(Color.LTGRAY);
        MainSet.setCircleColor(Color.LTGRAY);
        MainSet.setDrawValues(false);

        data = new LineData(MainSet);

        lineChart.setData(data);
    }

    void addLine() {
        database = sqLiteOpenHelper.getReadableDatabase();

        for (int m = 0; m < 12; m++) {
            int totPrice = 0;
            for(int i = 0; i < sel.size(); i++) {
                totPrice += Integer.parseInt(price[sel.get(i)][m]);
            }
            line.add(new Entry(m, totPrice));
        }
        set = new LineDataSet(line, "DataSet");

        if(inout.equals("income")) {
            set.setColor(ContextCompat.getColor(getContext(), R.color.income));
            set.setCircleColor(ContextCompat.getColor(getContext(), R.color.income));
        }
        else {
            set.setColor(ContextCompat.getColor(getContext(), R.color.outcome));
            set.setCircleColor(ContextCompat.getColor(getContext(), R.color.outcome));
        }
        set.setDrawValues(false);
        set.setLineWidth(3);

        data.addDataSet(set);

        lineChart.setData(data);
    }

    void price() {
        database = sqLiteOpenHelper.getReadableDatabase();

        price = new String[tag.size()][12];

        Cursor cursor = null;

        for (int t = 0; t < tag.size(); t++) {
            for (int i = 0; i < 12; i++) {

                cursor = database.rawQuery("SELECT SUM(" + inout + ") FROM account WHERE date like '" + date.substring(0,4) + "-" + month[i] + "%' AND " + inout + " > 0 AND tag = '" + tag.get(t) + "'", null);

                int totPrice = 0;

                while (cursor.moveToNext()) {

                    String cost  = cursor.getString(0);

                    if(cost == null) {
                        price[t][i] = "0";
                    }
                    else {
                        price[t][i] = cost;
                    }
                }
            }
        }

        cursor.close();

        database.close();
    }

    void tag() {
        tag.clear();
        sel.clear();
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT DISTINCT tag, SUM(" + inout + ") FROM account WHERE " + inout + " > 0 GROUP BY tag ORDER BY SUM(" + inout + ") DESC", null);

        int i = 0;
        while (cursor.moveToNext()) {

            tag.add(cursor.getString(0));
            sel.add(i);
            i++;
        }

        cursor.close();
        database.close();
    }

    void addPie() {

        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT DISTINCT tag, SUM(" + inout + ") FROM account WHERE date LIKE '" + date.substring(0,4) + "%' AND "+ inout +" > 0 GROUP BY tag ORDER BY SUM(" + inout + ") DESC ", null);

        int i = 0;

        while (cursor.moveToNext()) {
            String tag = cursor.getString(0);
            String price = cursor.getString(1);;

            ChartPieItem item;

            if(inout.equals("outcome")) {
                item = new ChartPieItem(tag, price, rainbow[i % 11]);
            }
            else {
                item = new ChartPieItem(tag, price, ocean[i % 9]);
            }

            items.add(item);

            i ++;
        }

        cursor.close();
        database.close();

    }

    int[] rainbow = {
            rgb("#D84315"),
            rgb("#FF8F00"),
            rgb("#FFEB3B"),
            rgb("#DCE775"),
            rgb("#8BC34A"),
            rgb("#009688"),
            rgb("#00BCD4"),
            rgb("#2196F3"),
            rgb("#3F51B5"),
            rgb("#6A1B9A"),
            rgb("#AD1457")
    };
    int[] ocean = {
            rgb("#1565C0"),
            rgb("#1E88E5"),
            rgb("#42A5F5"),
            rgb("#90CAF9"),
            rgb("#BBDEFB"),
            rgb("#64B5F6"),
            rgb("#2196F3"),
            rgb("#1976D2"),
            rgb("#0D47A1")
    };
}
