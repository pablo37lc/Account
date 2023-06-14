package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

public class Main extends AppCompatActivity {

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String today = dateFormat.format(currentTime);
    int page = 0;
    TextView tvTitle, tvNow, tvMoney, tvIncome, tvOutcome;

    ImageButton btnMenu, btnBack, btnGraph, btnAdd, btnBefore, btnAfter;

    String date;
    String from = "day";
    String centerDate, centerCal, centerMonth, centerDay;

    ViewPager2 viewPager;
    PagerAdapter pagerAdapter;
    int dayNum = 1000;
    int monthNum = 120;
    int calendarNum = 120;

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        sqLiteOpenHelper = new SQLiteOpenHelper(this, "account.db", null, 1);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        btnMenu = (ImageButton) findViewById(R.id.btnMenu);
        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnGraph = (ImageButton) findViewById(R.id.btnChart);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);

        tvNow = (TextView) findViewById(R.id.tvNow);
        btnBefore = (ImageButton) findViewById(R.id.btnBefore);
        btnAfter = (ImageButton) findViewById(R.id.btnAfter);

        tvMoney = (TextView) findViewById(R.id.tvMoney);
        tvIncome = (TextView) findViewById(R.id.tvIncome);
        tvOutcome = (TextView) findViewById(R.id.tvOutcome);

        btnMenu.setOnClickListener(clickListener);
        btnBack.setOnClickListener(clickListener);
        tvTitle.setOnClickListener(clickListener);
        btnGraph.setOnClickListener(clickListener);
        btnAdd.setOnClickListener(clickListener);

        btnBefore.setOnClickListener(menuClickListener);
        btnAfter.setOnClickListener(menuClickListener);
        tvNow.setOnClickListener(menuClickListener);

        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());

        date = today;

        Intent getData = getIntent();
        if(getData.getStringExtra("date") != null) {
            date = getData.getStringExtra("date");
        }
        if(getData.getStringExtra("from") != null) {
            from = getData.getStringExtra("from");
        }

        tvNow.setText(date.substring(0,4) + "년 " + date.substring(5,7) + "월");

        if(from.equals("calendar")) {
            centerCal = date;
            addCalender(calendarNum);
        }
        else if (from.equals("month")) {
            centerMonth = date;
            addMonth(monthNum);
        }
        else {
            centerDay = date;
            addDay(dayNum);
        }

        summery(date);

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
                    if (position > dayNum) {
                        date = LocalDate.parse(centerDate).plusDays(position - dayNum).toString();
                    }
                    else {
                        date = LocalDate.parse(centerDate).minusDays(dayNum - position).toString();
                    }
                }
                if(page == 1) {
                    if (position > monthNum) {
                        date = LocalDate.parse(centerDate).plusMonths(position - monthNum).toString();
                    }
                    else {
                        date = LocalDate.parse(centerDate).minusMonths(monthNum - position).toString();
                    }
                }
                if(page == 2) {
                    if (position > calendarNum) {
                        date = LocalDate.parse(centerDate).plusMonths(position - calendarNum).toString();
                    }
                    else {
                        date = LocalDate.parse(centerDate).minusMonths(calendarNum - position).toString();
                    }
                }
                if(position == 0 ) {

                }
                else {
                    tvNow.setText(date.substring(0,4) + "년 " + date.substring(5,7) + "월");
                    summery(date);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnMenu) {
                Intent intent = new Intent(Main.this, Menu.class);
                intent.putExtra("date", date);
                intent.putExtra("from", from);
                startActivity(intent);
                overridePendingTransition(R.anim.left_in, R.anim.left_out);
                finish();
            }
            if (v.getId() == R.id.btnBack) {
                if(page == 1) {
                    viewCalendar(date, calendarNum, dayNum);
                    btnBack.setRotation(270);
                }
                else {
                    viewMonth(date, monthNum, dayNum);
                    btnBack.setRotation(90);
                }
            }
            if (v.getId() == R.id.tvTitle) {
                if (page == 0) {
                    viewDay(today, dayNum, dayNum);
                }
                if (page == 1) {
                    viewMonth(today, monthNum, monthNum);
                }
                if (page == 2) {
                    viewCalendar(today, calendarNum, calendarNum);
                }
            }
            if (v.getId() == R.id.btnChart) {
                Intent intent = new Intent(Main.this, Chart.class);
                intent.putExtra("date", date);
                if (page == 2) {
                    intent.putExtra("from", "calendar");
                }
                else if (page == 1) {
                    intent.putExtra("from", "month");
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
            if (v.getId() == R.id.btnAdd) {
                Intent intent = new Intent(Main.this, Add.class);
                intent.putExtra("date", date);
                if (page == 2) {
                    intent.putExtra("from", "calendar");
                }
                else if (page == 1) {
                    intent.putExtra("from", "month");
                }
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();

            }
        }
    };

    View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.btnBefore) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
            if(v.getId() == R.id.btnAfter) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
            if(v.getId() == R.id.tvNow) {
                if(page == 0) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(Main.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                    month = month + 1;
                                    Date datePick = null;
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    try {
                                        datePick = dateFormat.parse(year + "-" + month + "-" + day);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                    date = (dateFormat.format(datePick));
                                    viewDay(date, dayNum, dayNum);
                                }
                            }, Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.substring(5,7))-1, Integer.parseInt(date.substring(8,10)));
                    datePickerDialog.show();
                }
                else {
                    final String[] year = {date.substring(0, 4)};
                    final String[] month = {date.substring(5, 7)};

                    Dialog dialog = new Dialog(Main.this);
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
                            tvTitle.setText(year[0] + "년 " + month + "월");
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
                            date = year[0] + "-" + month[0] + "-" + date.substring(8,10) ;

                            if(page == 1) {
                                viewMonth(date, monthNum, monthNum);
                            }
                            if(page == 2) {
                                viewCalendar(date, calendarNum, calendarNum);
                            }

                            dialog.dismiss();
                        }
                    });
                }
            }
        }
    };


    void summery(String date) {
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT substr(date, 1, 7), SUM(income), SUM(outcome) FROM account WHERE substr(date, 1, 7) = '" + date.substring(0,7) + "' GROUP BY substr(date, 1, 7)", null);

        tvIncome.setText("0");
        tvOutcome.setText("0");
        tvMoney.setText("0");

        int incomeInt = 0, outcomeInt = 0;
        while (cursor.moveToNext()) {
            String income = cursor.getString(1);
            String outcome = cursor.getString(2);


            if(income != null) {
                incomeInt = Integer.parseInt(income);
                tvIncome.setText(decimalFormat.format(incomeInt));
            }
            if(outcome != null) {
                outcomeInt = Integer.parseInt(outcome);
                tvOutcome.setText(decimalFormat.format(outcomeInt));
            }

            int left = incomeInt - outcomeInt;
            if(left != 0) {
                tvMoney.setText(decimalFormat.format(left));
            }
        }

        cursor.close();
        database.close();
    }

    void addDay(int num) {
        date = centerDay;
        centerDate = date;
        if(num == viewPager.getCurrentItem()) {
            centerDate = String.valueOf(LocalDate.parse(date).minusDays(1));
        }

        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Day(LocalDate.parse(centerDate).minusDays(num - i).toString()));
        }
        pagerAdapter.addFragment(new Day(centerDate));
        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Day(LocalDate.parse(centerDate).plusDays(1 + i).toString()));
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
    void addMonth(int num) {
        date = centerMonth;
        centerDate = date;
        if(num == viewPager.getCurrentItem()) {
            centerDate = String.valueOf(LocalDate.parse(date).minusMonths(1));
        }

        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Month(LocalDate.parse(centerDate).minusMonths(num - i).toString()));
        }
        pagerAdapter.addFragment(new Month(centerDate));
        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Month(LocalDate.parse(centerDate).plusMonths(1 + i).toString()));
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

    void addCalender(int num){
        date = centerCal;
        centerDate = date;
        if(num == viewPager.getCurrentItem()) {
            centerDate = String.valueOf(LocalDate.parse(date).minusMonths(1));
        }

        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Calender(LocalDate.parse(centerDate).minusMonths(num - i).toString()));
        }
        pagerAdapter.addFragment(new Calender(centerDate));
        for(int i = 0; i < num; i ++) {
            pagerAdapter.addFragment(new Calender(LocalDate.parse(centerDate).plusMonths(1 + i).toString()));
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
        page = 2;
    }

    void viewDay(String date, int dayNum, int oriNum) {
        centerDay = date;
        pagerAdapter.clear();
        pagerAdapter.notifyItemRangeRemoved(0, oriNum *2+1);

        addDay(dayNum);

        from = "day";
    }
    void viewMonth(String date, int monthNum, int oriNum) {
        centerMonth = date;
        pagerAdapter.clear();
        pagerAdapter.notifyItemRangeRemoved(0, oriNum *2+1);

        addMonth(monthNum);

        from = "month";
    }

    void viewCalendar(String date, int calendarNum, int oriNum) {
        centerCal = date;
        pagerAdapter.clear();
        pagerAdapter.notifyItemRangeRemoved(0, oriNum *2+1);

        addCalender(calendarNum);

        from = "calendar";
    }
}