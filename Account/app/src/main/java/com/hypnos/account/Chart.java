package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Dialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Chart extends AppCompatActivity {

    ViewPager2 viewPager;
    PagerAdapter pagerAdapter;

    TextView tvTitle, tvNow;
    ImageButton btnBack, btnChange, btnBefore, btnAfter;
    Button btnIncome, btnOutcome;
    Spinner spinner;
    ArrayList<String> spinnerItem = new ArrayList<String>();

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(currentTime);

    String date;
    String from;
    String period;
    String centerPie, centerLine, centerDate;
    String inout = "outcome";

    int lineNum = 50;
    int pieNum = 50;
    int page;


    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        sqLiteOpenHelper = new SQLiteOpenHelper(this, "account.db", null, 1);

        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnChange = (ImageButton) findViewById(R.id.btnChange);
        spinner = (Spinner) findViewById(R.id.spinner);

        tvNow = (TextView) findViewById(R.id.tvNow);
        btnBefore = (ImageButton) findViewById(R.id.btnBefore);
        btnAfter = (ImageButton) findViewById(R.id.btnAfter);

        btnIncome = (Button) findViewById(R.id.btnIncome);
        btnOutcome = (Button) findViewById(R.id.btnOutcome);

        btnChange.setOnClickListener(menuClickListener);

        btnBack.setOnClickListener(clickListener);

        btnBefore.setOnClickListener(dateClickListener);
        btnAfter.setOnClickListener(dateClickListener);
        tvNow.setOnClickListener(dateClickListener);

        btnIncome.setOnClickListener(inoutClickListener);
        btnOutcome.setOnClickListener(inoutClickListener);

        Intent getData = getIntent();
        if(getData.getStringExtra("date") != null) {
            date = getData.getStringExtra("date");
        }
        if(getData.getStringExtra("from") != null) {
            from = getData.getStringExtra("from");
        }

        tvNow.setText(date.substring(0,4) + "년 " + date.substring(5,7) + "월");

        spinnerItem.add("월간");
        spinnerItem.add("년간");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout_chart, spinnerItem);
        spinner.setAdapter(spinnerAdapter);

        centerPie = date;

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                period = spinner.getSelectedItem().toString();
                if(page == 0) {
                    viewPie(date, pieNum, pieNum);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if(page == 0) {
                    if(period.equals("월간")) {
                        if (position > pieNum) {
                            date = LocalDate.parse(centerDate).plusMonths(position - pieNum).toString();
                        }
                        else {
                            date = LocalDate.parse(centerDate).minusMonths(pieNum - position).toString();
                        }
                    }
                    else if (period.equals("년간")) {
                        if (position > pieNum) {
                            date = LocalDate.parse(centerDate).plusYears(position - pieNum).toString();
                        }
                        else {
                            date = LocalDate.parse(centerDate).minusYears(pieNum - position).toString();
                        }
                    }
                }
                else {
                    if (position > pieNum) {
                        date = LocalDate.parse(centerDate).plusYears(position - pieNum).toString();
                    }
                    else {
                        date = LocalDate.parse(centerDate).minusYears(pieNum - position).toString();
                    }
                }
                if(position == 0 ) {

                }
                else {
                    if(period.equals("월간")) {
                        tvNow.setText(date.substring(0,4) + "년 " + date.substring(5,7) + "월");
                    }
                    else if(period.equals("년간")) {
                        tvNow.setText(date.substring(0,4) + "년");
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(page == 0) {
                    viewPie(today, pieNum, pieNum);
                }
                else if(page == 1) {
                    viewLine(today, lineNum, lineNum);
                }
            }
        });
    }

    View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (tvTitle.getText().equals("통계")) {
                tvTitle.setText("비교");
                btnChange.setImageResource(R.drawable.chartline);
                spinner.setSelection(1);
                spinner.setEnabled(false);
                viewLine(date, lineNum, pieNum);
            }
            else {
                tvTitle.setText("통계");
                btnChange.setImageResource(R.drawable.chartpie);
                spinner.setEnabled(true);
                viewPie(date, pieNum, lineNum);
            }
        }
    };
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnBack) {
                Intent intent = new Intent(Chart.this, Main.class);
                intent.putExtra("date", date);
                intent.putExtra("from", from);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            }
        }
    };

    View.OnClickListener inoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnIncome.setBackgroundColor(Color.LTGRAY);
            btnIncome.setTextColor(Color.BLACK);
            btnOutcome.setBackgroundColor(Color.LTGRAY);
            btnOutcome.setTextColor(Color.BLACK);

            if(v.getId() == R.id.btnIncome) {
                inout = "income";
                btnIncome.setBackgroundColor(getColor(R.color.income));
                btnIncome.setTextColor(Color.WHITE);
            }
            if(v.getId() == R.id.btnOutcome) {
                inout = "outcome";
                btnOutcome.setBackgroundColor(getColor(R.color.outcome));
                btnOutcome.setTextColor(Color.WHITE);
            }
            if(page == 0) {
                viewPie(date, pieNum, pieNum);
            }
            else {
                viewLine(date, lineNum, lineNum);
            }
        }
    };

    View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnBefore) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
            if(v.getId() == R.id.btnAfter) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
            if(v.getId() == R.id.tvNow) {
                final String[] year = {date.substring(0, 4)};
                final String[] month = {date.substring(5, 7)};

                if(period.equals("월간")) {
                    Dialog dialog = new Dialog(Chart.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.month_year_picker);

                    dialog.show();

                    TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                    TextView tvYear = (TextView) dialog.findViewById(R.id.tvYear);

                    ImageButton btnBack = (ImageButton) dialog.findViewById(R.id.btnBack);
                    ImageButton btnNext = (ImageButton) dialog.findViewById(R.id.btnNext);

                    TextView btn1 = (TextView) dialog.findViewById(R.id.btn1);
                    TextView btn2 = (TextView) dialog.findViewById(R.id.btn2);
                    TextView btn3 = (TextView) dialog.findViewById(R.id.btn3);
                    TextView btn4 = (TextView) dialog.findViewById(R.id.btn4);
                    TextView btn5 = (TextView) dialog.findViewById(R.id.btn5);
                    TextView btn6 = (TextView) dialog.findViewById(R.id.btn6);
                    TextView btn7 = (TextView) dialog.findViewById(R.id.btn7);
                    TextView btn8 = (TextView) dialog.findViewById(R.id.btn8);
                    TextView btn9 = (TextView) dialog.findViewById(R.id.btn9);
                    TextView btn10 = (TextView) dialog.findViewById(R.id.btn10);
                    TextView btn11 = (TextView) dialog.findViewById(R.id.btn11);
                    TextView btn12 = (TextView) dialog.findViewById(R.id.btn12);

                    TextView btnNo = (TextView) dialog.findViewById(R.id.btnNo);
                    TextView btnYes = (TextView) dialog.findViewById(R.id.btnYes);

                    tvTitle.setText(year[0] + "년 " + month[0] + "월");
                    tvYear.setText(year[0]);
                    switch (month[0]) {
                        case "01" :
                            btn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "02" :
                            btn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "03" :
                            btn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "04" :
                            btn4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "05" :
                            btn5.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "06" :
                            btn6.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "07" :
                            btn7.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "08" :
                            btn8.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn8.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "09" :
                            btn9.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn9.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "10" :
                            btn10.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "11" :
                            btn11.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn11.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                        case "12" :
                            btn12.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                            btn12.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            break;
                    }

                    View.OnClickListener monthClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btn1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn2.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn3.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn4.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn5.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn6.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn7.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn8.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn8.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn9.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn9.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn10.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn11.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn11.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                            btn12.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                            btn12.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                            if(v.getId() == R.id.btn1) {
                                btn1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "01";
                            }
                            if(v.getId() == R.id.btn2) {
                                btn2.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn2.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "02";
                            }
                            if(v.getId() == R.id.btn3) {
                                btn3.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn3.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "03";
                            }
                            if(v.getId() == R.id.btn4) {
                                btn4.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn4.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "04";
                            }
                            if(v.getId() == R.id.btn5) {
                                btn5.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn5.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "05";
                            }
                            if(v.getId() == R.id.btn6) {
                                btn6.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn6.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "06";
                            }
                            if(v.getId() == R.id.btn7) {
                                btn7.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn7.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "07";
                            }
                            if(v.getId() == R.id.btn8) {
                                btn8.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn8.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "08";
                            }
                            if(v.getId() == R.id.btn9) {
                                btn9.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn9.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "09";
                            }
                            if(v.getId() == R.id.btn10) {
                                btn10.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn10.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "10";
                            }
                            if(v.getId() == R.id.btn11) {
                                btn11.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn11.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "11";
                            }
                            if(v.getId() == R.id.btn12) {
                                btn12.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.round));
                                btn12.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                                month[0] = "12";
                            }
                            tvTitle.setText(year[0] + "년 " + month[0] + "월");
                        }
                    };

                    btn1.setOnClickListener(monthClickListener);
                    btn2.setOnClickListener(monthClickListener);
                    btn3.setOnClickListener(monthClickListener);
                    btn4.setOnClickListener(monthClickListener);
                    btn5.setOnClickListener(monthClickListener);
                    btn6.setOnClickListener(monthClickListener);
                    btn7.setOnClickListener(monthClickListener);
                    btn8.setOnClickListener(monthClickListener);
                    btn9.setOnClickListener(monthClickListener);
                    btn10.setOnClickListener(monthClickListener);
                    btn11.setOnClickListener(monthClickListener);
                    btn12.setOnClickListener(monthClickListener);

                    View.OnClickListener yearClickListener = new View.OnClickListener() {
                        int yearInt = Integer.parseInt(year[0]);
                        @Override
                        public void onClick(View v) {
                            if(v.getId() == R.id.btnBack) {
                                yearInt --;
                            }
                            if(v.getId() == R.id.btnNext) {
                                yearInt ++;
                            }
                            year[0] = Integer.toString(yearInt);
                            tvYear.setText(year[0]);
                            tvTitle.setText(year[0] + "년 " + month[0] + "월");
                        }
                    };

                    btnBack.setOnClickListener(yearClickListener);
                    btnNext.setOnClickListener(yearClickListener);

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            date = year[0] + "-" + month[0] + "-" + date.substring(9,10) ;

                            if(page == 0) {
                                viewPie(date, pieNum, pieNum);
                            }

                            dialog.dismiss();
                        }
                    });
                }
                else if (period.equals("년간")) {
                    Dialog dialog = new Dialog(Chart.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.year_picker);

                    dialog.show();

                    TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
                    TextView tvYear = (TextView) dialog.findViewById(R.id.tvYear);

                    ImageButton btnBack = (ImageButton) dialog.findViewById(R.id.btnBack);
                    ImageButton btnNext = (ImageButton) dialog.findViewById(R.id.btnNext);

                    TextView btnNo = (TextView) dialog.findViewById(R.id.btnNo);
                    TextView btnYes = (TextView) dialog.findViewById(R.id.btnYes);

                    tvTitle.setText(year[0] + "년");
                    tvYear.setText(year[0]);

                    View.OnClickListener yearClickListener = new View.OnClickListener() {
                        int yearInt = Integer.parseInt(year[0]);
                        @Override
                        public void onClick(View v) {
                            if(v.getId() == R.id.btnBack) {
                                yearInt --;
                            }
                            if(v.getId() == R.id.btnNext) {
                                yearInt ++;
                            }
                            year[0] = Integer.toString(yearInt);
                            tvYear.setText(year[0]);
                            tvTitle.setText(year[0] + "년");
                        }
                    };

                    btnBack.setOnClickListener(yearClickListener);
                    btnNext.setOnClickListener(yearClickListener);

                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Integer.parseInt(tvYear.getText().toString()) < 2000 || Integer.parseInt(tvYear.getText().toString()) > 2100) {
                                tvYear.setText(year[0]);
                            }
                            year[0] = tvYear.getText().toString();
                            date = year[0] + "-" + date.substring(5,7) + "-" + date.substring(8,10) ;

                            if(page == 0) {
                                viewPie(date, pieNum, pieNum);
                            }
                            else {
                                viewLine(date, lineNum, lineNum);
                            }

                            dialog.dismiss();
                        }
                    });

                }

            }
        }
    };

    void addPie(int num){
        date = centerPie;
        centerDate = date;

        if(period.equals("월간")) {
            if(num == viewPager.getCurrentItem()) {
                centerDate = String.valueOf(LocalDate.parse(date).minusMonths(1));
            }
            for(int i = 0; i < num; i ++) {
                pagerAdapter.addFragment(new ChartPie(LocalDate.parse(centerDate).minusMonths(num - i).toString(), inout, period));
            }
            pagerAdapter.addFragment(new ChartPie(centerDate, inout, period));
            for(int i = 0; i < num; i ++) {
                pagerAdapter.addFragment(new ChartPie(LocalDate.parse(centerDate).plusMonths(1 + i).toString(), inout, period));
            }
        }
        if(period.equals("년간") || page == 1) {
            if(num == viewPager.getCurrentItem()) {
                centerDate = String.valueOf(LocalDate.parse(date).minusYears(1));
            }
            for(int i = 0; i < num; i ++) {
                pagerAdapter.addFragment(new ChartPie(LocalDate.parse(centerDate).minusYears(num - i).toString(), inout, period));
            }
            pagerAdapter.addFragment(new ChartPie(centerDate, inout, period));
            for(int i = 0; i < num; i ++) {
                pagerAdapter.addFragment(new ChartPie(LocalDate.parse(centerDate).plusYears(1 + i).toString(), inout, period));
            }
        }

        pagerAdapter.notifyItemRangeInserted(0, num *2+1);

        if(num == viewPager.getCurrentItem()) {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(num + 1, false);
                }
            },100);
        }
        else {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(num, false);
                }
            },100);
        }
        page = 0;
    }

    void viewPie(String date, int pieNum, int oriNum) {
        centerPie = date;
        pagerAdapter.clear();
        pagerAdapter.notifyItemRangeRemoved(0, oriNum *2+1);

        addPie(pieNum);
    }

    void addLine(int num){
        date = centerLine;
        centerDate = date;

        if(num == viewPager.getCurrentItem()) {
            centerDate = String.valueOf(LocalDate.parse(date).minusYears(1));
        }
        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new ChartLine(LocalDate.parse(centerDate).minusYears(num - i).toString(), inout));
        }
        pagerAdapter.addFragment(new ChartLine(centerDate, inout));
        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new ChartLine(LocalDate.parse(centerDate).plusYears(1 + i).toString(), inout));
        }

        pagerAdapter.notifyItemRangeInserted(0, num *2+1);

        if(num == viewPager.getCurrentItem()) {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(num + 1, false);
                }
            },100);
        }
        else {
            viewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    viewPager.setCurrentItem(num, false);
                }
            },100);
        }
        page = 1;
    }

    void viewLine(String date, int lineNum, int oriNum) {
        centerLine = date;
        pagerAdapter.clear();
        pagerAdapter.notifyItemRangeRemoved(0, oriNum *2+1);

        addLine(lineNum);
    }
}