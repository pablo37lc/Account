package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Add extends AppCompatActivity {

    // menu
    ImageButton btnBack;
    Button btnAdd, btnIncome, btnOutcome;
    FrameLayout frameLayout;
    ScrollView viewOutcome;

    //add
    ImageButton btnClear;
    Button btnSpinner;
    Spinner spinner;
    TextView tvDate;
    EditText etUse, etPrice, etMemo;

    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Dialog dialog;
    ArrayList<String> items = new ArrayList<String>();

    int click = 3;
    String tag, use, price, memo;

    String date = dateFormat.format(currentTime);
    String from = null;

    //db
    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        sqLiteOpenHelper = new SQLiteOpenHelper(this, "account.db", null, 1);

        Intent getData = getIntent();
        if(getData.getStringExtra("date") != null) {
            date = getData.getStringExtra("date");
        }
        if(getData.getStringExtra("from") != null) {
            from = getData.getStringExtra("from");
        }

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        btnBack.setOnClickListener(menuClickListener);
        btnAdd.setOnClickListener(menuClickListener);

        btnIncome = (Button) findViewById(R.id.btnIncome);
        btnOutcome = (Button) findViewById(R.id.btnOutcome);

        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);

        viewOutcome = (ScrollView) findViewById(R.id.viewOutcome);

        btnIncome.setOnClickListener(clickListener);
        btnOutcome.setOnClickListener(clickListener);

        tvDate = (TextView) findViewById(R.id.tvDate);
        spinner = (Spinner) findViewById(R.id.spinner);
        btnSpinner = (Button) findViewById(R.id.btnSpinner);
        btnClear = (ImageButton) findViewById(R.id.btnClear);
        etUse = (EditText) findViewById(R.id.etUse);
        etPrice = (EditText) findViewById(R.id.etPrice);
        etMemo = (EditText) findViewById(R.id.etMemo);

        tvDate.setOnClickListener(dateClickListener);

        tvDate.setText(date);

        items.add("선택필수");
        tag("outcome");
        color("outcome");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, items);
        spinner.setAdapter(spinnerAdapter);

        btnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Add.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.add_tag);

                dialog.show();

                Button btnNo, btnYes;
                EditText etTag;

                btnNo = (Button) dialog.findViewById(R.id.btnNo);
                btnYes = (Button) dialog.findViewById(R.id.btnYes);
                etTag = (EditText) dialog.findViewById(R.id.etTag);

                if(click == 3) {
                    btnYes.setBackgroundColor(getColor(R.color.outcome));
                }
                else {
                    btnYes.setBackgroundColor(getColor(R.color.income));
                }

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        etTag.setText(null);
                        dialog.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        items.add(etTag.getText().toString());
                        spinner.setSelection(items.size());
                        spinnerAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMemo.setText(null);
            }
        });

    }

    void color(String inout) {
        if(inout == "income") {
            setTheme(R.style.Theme_income);
            btnAdd.setBackgroundColor(getColor(R.color.income));
            btnIncome.setBackgroundColor(getColor(R.color.income));
            btnIncome.setTextColor(getColor(R.color.white));
            tvDate.setOnFocusChangeListener(incomeFocusChangeListener);
            spinner.setOnFocusChangeListener(incomeFocusChangeListener);
            etUse.setOnFocusChangeListener(incomeFocusChangeListener);
            etPrice.setOnFocusChangeListener(incomeFocusChangeListener);
            etMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.income)));
                        btnClear.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                        btnClear.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
        else {
            setTheme(R.style.Theme_outcome);
            btnAdd.setBackgroundColor(getColor(R.color.outcome));
            btnOutcome.setBackgroundColor(getColor(R.color.outcome));
            btnOutcome.setTextColor(getColor(R.color.white));
            tvDate.setOnFocusChangeListener(outcomeFocusChangeListener);
            spinner.setOnFocusChangeListener(outcomeFocusChangeListener);
            etUse.setOnFocusChangeListener(outcomeFocusChangeListener);
            etPrice.setOnFocusChangeListener(outcomeFocusChangeListener);
            etMemo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if (hasFocus) {
                        view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.outcome)));
                        btnClear.setVisibility(View.VISIBLE);
                    }
                    else {
                        view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
                        btnClear.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    View.OnFocusChangeListener incomeFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.income)));
            }
            else {
                view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            }
        }
    };

    View.OnFocusChangeListener outcomeFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.outcome)));
            }
            else {
                view.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.black)));
            }
        }
    };

    View.OnClickListener dateClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            DatePickerDialog datePickerDialog = new DatePickerDialog(Add.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            month = month + 1;
                            Date date = null;

                            try {
                                date = dateFormat.parse(year + "-" + month + "-" + day);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            tvDate.setText(dateFormat.format(date));
                        }
                    }, Integer.parseInt(date.substring(0,4)), Integer.parseInt(date.substring(5,7))-1, Integer.parseInt(date.substring(8,10)));
            datePickerDialog.show();
        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnIncome.setBackgroundColor(getColor(R.color.lightGrey));
            btnIncome.setTextColor(getColor(R.color.black));
            btnOutcome.setBackgroundColor(getColor(R.color.lightGrey));
            btnOutcome.setTextColor(getColor(R.color.black));

            viewOutcome.setVisibility(View.INVISIBLE);
            items.clear();
            items.add("선택필수");

            if (v.getId() == R.id.btnIncome) {
                color("income");
                viewOutcome.setVisibility(View.VISIBLE);
                tag("income");
                click = 2;
            }
            if (v.getId() == R.id.btnOutcome) {
                color("outcome");
                viewOutcome.setVisibility(View.VISIBLE);
                tag("outcome");
                click = 3;

            }
        }
    };

    View.OnClickListener menuClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnBack) {
                exit();
            }

            if (v.getId() == R.id.btnAdd) {
                date = tvDate.getText().toString();
                tag = spinner.getSelectedItem().toString();
                use = etUse.getText().toString();
                price = etPrice.getText().toString().replaceAll(",","");
                memo = etMemo.getText().toString();
                if(click == 2) {
                    add("income");
                }
                if(click == 3) {
                    add("outcome");
                }
            }

        }
    };

    void exit() {
        Intent intent = new Intent(Add.this, Main.class);
        intent.putExtra("date", date);
        intent.putExtra("from", from);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    void add(String string) {
        if(tag.equals("선택필수") || price.equals("") || use.equals("")) {
            Toast.makeText(this, "분류/내용/금액 필수", Toast.LENGTH_SHORT).show();
        }
        else {
            dataInsert(string);
            exit();
        }
    }
    void dataInsert(String inout) {

        database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("date", date);
        contentValues.put("tag", tag);
        contentValues.put("use", use);
        contentValues.put(inout, price);
        contentValues.put("memo", memo);

        database.insert("account", null, contentValues);

        database.close();
    }

    void tag(String inout) {
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT DISTINCT tag FROM account WHERE " + inout + " > 0 ORDER BY tag", null);

        while (cursor.moveToNext()) {

            String tag = cursor.getString(0);

            items.add(tag);
        }

        cursor.close();
        database.close();
    }
}