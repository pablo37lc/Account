package com.hypnos.account;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DataDel extends Fragment {

    Button btnIncome, btnOutcome, btnBoth;
    Button btnDay, btnMonth, btnYear, btnAll;
    Button btnDelete;
    TextView textView, tvNum;
    Spinner spinner;
    ArrayList<String> items = new ArrayList<String>();

    String inout;
    String tag, sqlTag;
    String date, sqlDate;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Calendar calendar = Calendar.getInstance();

    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.data_del, container, false);

        sqLiteOpenHelper = new SQLiteOpenHelper(getContext(), "account.db", null, 1);

        inout = "(income > 0 OR outcome > 0)";
        sqlDate = "date > 0";
        sqlTag = "tag NOT null";

        btnIncome = (Button) view.findViewById(R.id.btnIncome);
        btnOutcome = (Button) view.findViewById(R.id.btnOutcome);
        btnBoth = (Button) view.findViewById(R.id.btnBoth);

        btnDay = (Button) view.findViewById(R.id.btnDay);
        btnMonth = (Button) view.findViewById(R.id.btnMonth);
        btnYear = (Button) view.findViewById(R.id.btnYear);
        btnAll = (Button) view.findViewById(R.id.btnAll);

        btnDelete = (Button) view.findViewById(R.id.btnDelete);

        textView = (TextView) view.findViewById(R.id.textView);
        tvNum = (TextView) view.findViewById(R.id.tvNum);

        spinner = (Spinner) view.findViewById(R.id.spinner);

        tag();

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_layout_chart, items);
        spinner.setAdapter(spinnerAdapter);

        btnBoth.setOnClickListener(inoutClickListener);
        btnIncome.setOnClickListener(inoutClickListener);
        btnOutcome.setOnClickListener(inoutClickListener);

        btnDay.setOnClickListener(dateClickListener);
        btnMonth.setOnClickListener(dateClickListener);
        btnYear.setOnClickListener(dateClickListener);
        btnAll.setOnClickListener(dateClickListener);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tag = spinner.getSelectedItem().toString();
                if (tag.equals("모두")) {
                    sqlTag = "tag NOT NULL";
                }
                else {
                    sqlTag = "tag = '" + tag + "'";
                }
                get();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                database = sqLiteOpenHelper.getWritableDatabase();
                database.delete("account", inout + " AND " + sqlDate + " AND " + sqlTag, null);

                database.close();

                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction().remove(DataDel.this).commit();
                fragmentManager.popBackStack();
            }
        });

        get();
        return view;
    }

    View.OnClickListener inoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnBoth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnBoth.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnIncome.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnOutcome.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnOutcome.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            if(v.getId() == R.id.btnBoth){
                btnBoth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plan));
                btnBoth.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                inout = "(income > 0 OR outcome > 0)";
            }
            if(v.getId() == R.id.btnIncome){
                btnIncome.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.income));
                btnIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                inout = "income > 0";

            }
            if(v.getId() == R.id.btnOutcome){
                btnOutcome.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.outcome));
                btnOutcome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                inout = "outcome > 0";
            }
            tag();
            get();
        }
    };

    View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnDay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnDay.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnMonth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnYear.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnYear.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnAll.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            if(v.getId() == R.id.btnDay) {
                btnDay.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plan));
                btnDay.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                                month = month + 1;

                                String m, d;
                                if(month < 9) {
                                    m = "0" + Integer.toString(month);
                                }
                                else {
                                    m = Integer.toString(month);
                                }
                                if(day < 9) {
                                    d = "0" + Integer.toString(day);
                                }
                                else {
                                    d = Integer.toString(day);
                                }

                                date = Integer.toString(year) + "-" + m + "-" + d;

                                textView.setText(date);
                                sqlDate = "date = '" + date + "'";
                                tag();
                                get();
                            }
                        }, year, month, day);

                datePickerDialog.show();

            }
            if(v.getId() == R.id.btnMonth) {
                btnMonth.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plan));
                btnMonth.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                Calendar time = Calendar.getInstance();

                String today = dateFormat.format(time.getTime());

                final String[] year = {today.substring(0,4)};
                final String[] month = {today.substring(5,7)};

                Dialog dialog = new Dialog(getContext());
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
                        btn1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "02" :
                        btn2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn2.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "03" :
                        btn3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "04" :
                        btn4.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn4.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "05" :
                        btn5.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn5.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "06" :
                        btn6.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn6.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "07" :
                        btn7.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn7.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "08" :
                        btn8.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn8.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "09" :
                        btn9.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn9.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "10" :
                        btn10.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn10.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "11" :
                        btn11.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn11.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case "12" :
                        btn12.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                        btn12.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                }

                View.OnClickListener monthClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn1.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn2.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn2.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn3.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn3.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn4.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn4.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn5.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn5.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn6.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn6.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn7.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn7.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn8.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn8.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn9.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn9.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn10.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn10.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn11.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn11.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                        btn12.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
                        btn12.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                        if(v.getId() == R.id.btn1) {
                            btn1.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn1.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "01";
                        }
                        if(v.getId() == R.id.btn2) {
                            btn2.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn2.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "02";
                        }
                        if(v.getId() == R.id.btn3) {
                            btn3.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn3.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "03";
                        }
                        if(v.getId() == R.id.btn4) {
                            btn4.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn4.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "04";
                        }
                        if(v.getId() == R.id.btn5) {
                            btn5.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn5.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "05";
                        }
                        if(v.getId() == R.id.btn6) {
                            btn6.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn6.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "06";
                        }
                        if(v.getId() == R.id.btn7) {
                            btn7.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn7.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "07";
                        }
                        if(v.getId() == R.id.btn8) {
                            btn8.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn8.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "08";
                        }
                        if(v.getId() == R.id.btn9) {
                            btn9.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn9.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "09";
                        }
                        if(v.getId() == R.id.btn10) {
                            btn10.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn10.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "10";
                        }
                        if(v.getId() == R.id.btn11) {
                            btn11.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn11.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                            month[0] = "11";
                        }
                        if(v.getId() == R.id.btn12) {
                            btn12.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.round));
                            btn12.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
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
                        date = year[0] + "-" + month[0];

                        textView.setText(date);
                        sqlDate = "date LIKE '" + date + "%'";
                        tag();
                        get();

                        dialog.dismiss();
                    }
                });
            }
            if(v.getId() == R.id.btnYear) {
                btnYear.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plan));
                btnYear.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                Calendar time = Calendar.getInstance();

                String today = dateFormat.format(time.getTime());

                final String[] year = {today.substring(0,4)};
                final String[] month = {today.substring(5,7)};

                Dialog dialog = new Dialog(getContext());
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
                        date = year[0];

                        textView.setText(date);
                        sqlDate = "date LIKE '" + date + "%'";
                        tag();
                        get();

                        dialog.dismiss();
                    }
                });
            }
            if(v.getId() == R.id.btnAll) {
                btnAll.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.plan));
                btnAll.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                textView.setText("전체기간");
                sqlDate = "date > 0";
            }
        }
    };

    void tag() {
        items.clear();
        items.add("모두");

        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT DISTINCT tag FROM account WHERE " + inout + " AND " + sqlDate + " ORDER BY tag", null);

        while (cursor.moveToNext()) {

            String tag = cursor.getString(0);

            items.add(tag);
        }

        cursor.close();
        database.close();
    }

    void get() {

        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT COUNT(*) FROM account WHERE " + inout + " AND " + sqlDate + " AND " + sqlTag , null);

        while (cursor.moveToNext()) {
            tvNum.setText(cursor.getString(0));
        }

        cursor.close();
        database.close();
    }
}
