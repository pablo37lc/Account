package com.hypnos.account;

import static com.github.mikephil.charting.utils.ColorTemplate.createColors;
import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

public class ChartPie extends Fragment {

    ChartPie(String date, String inout, String period) {
        this.date = date;
        this.inout = inout;
        this.period = period;
    }
    String date;
    String inout;
    String period;
    TextView tvNull;
    PieChart pieChart;
    RecyclerView recyclerView;
    ArrayList<ChartPieItem> items = new ArrayList<ChartPieItem>();
    ChartPieAdapter adapter = new ChartPieAdapter(items);

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chart_pie, container, false);

        sqLiteOpenHelper = new SQLiteOpenHelper(view.getContext(), "account.db", null, 1);

        tvNull = (TextView) view.findViewById(R.id.tvNull);

        pieChart = view.findViewById(R.id.pieChart);

        pieChart.setDrawHoleEnabled(true);

        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD);

        pieChart.setExtraOffsets(5,10,5,15);

        pieChart.setUsePercentValues(true);
        pieChart.getLegend().setEnabled(false);

        pieChart.setDrawEntryLabels(false);
        pieChart.animate();

        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate();
        pieChart.setTouchEnabled(false);
        pieChart.setMinAngleForSlices(10f);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter.clear();

        addPie(inout);

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

        return view;
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

    void addPie(String inout) {
        ArrayList<PieEntry> pie = new ArrayList<>();

        String term = "";
        if(period.equals("월간")) {
            term = date.substring(0,7);
        }
        else{
            term = date.substring(0,4);
        }

        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT DISTINCT tag, SUM(" + inout + ") FROM account WHERE date LIKE '" + term + "%' AND "+ inout +" > 0 GROUP BY tag ORDER BY SUM(" + inout + ") DESC ", null);

        int i = 0;

        while (cursor.moveToNext()) {
            String tag = cursor.getString(0);
            String price = cursor.getString(1);;

            pie.add(new PieEntry(Integer.parseInt(price), tag));

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

        PieDataSet pieDataSet = new PieDataSet(pie, "");

        pieDataSet.setValueFormatter(new PercentFormatter(pieChart));


        pieDataSet.setValueLinePart1Length(0.5f);
        pieDataSet.setValueLinePart2Length(0.2f);
        pieDataSet.setValueLineWidth(2f);
        pieDataSet.setValueLinePart1OffsetPercentage(115f);
        pieDataSet.setUsingSliceColorAsValueLineColor(true);

        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        if(inout.equals("outcome")) {
            pieDataSet.setValueTextColors(createColors(rainbow));
            pieDataSet.setColors(rainbow);
        }
        else {
            pieDataSet.setValueTextColors(createColors(ocean));
            pieDataSet.setColors(ocean);
        }

        pieDataSet.setValueTypeface(Typeface.DEFAULT_BOLD);
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setSliceSpace(1);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
    }
}