package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Edit extends AppCompatActivity {

    // menu
    ImageButton btnBack;
    Button btnAdd, btnIncome, btnOutcome, btnDel, btnEdit;
    FrameLayout frameLayout;
    ScrollView viewOutcome;
    LinearLayout lyAdd, lyEdit;

    //add
    ImageButton btnClear;
    Button btnSpinner;
    Spinner spinner;
    TextView tvDate;
    EditText etUse, etPrice, etMemo;

    Calendar calendar = Calendar.getInstance();
    Date currentTime = Calendar.getInstance().getTime();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    Dialog dialog;
    ArrayList<String> items = new ArrayList<String>();

    int click = 3;
    String date, tag, use, price, memo;
    String idx;
    String inout;

    //db
    SQLiteDatabase database;
    SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);

        sqLiteOpenHelper = new SQLiteOpenHelper(this, "account.db", null, 1);

        Intent getData = getIntent();
        idx = getData.getStringExtra("idx");

        lyAdd = (LinearLayout) findViewById(R.id.lyAdd);
        lyEdit = (LinearLayout) findViewById(R.id.lyEdit);

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnDel = (Button) findViewById(R.id.btnDelete);
        btnEdit = (Button) findViewById(R.id.btnEdit);

        btnBack.setOnClickListener(menuClickListener);
        btnAdd.setOnClickListener(menuClickListener);
        btnDel.setOnClickListener(menuClickListener);
        btnEdit.setOnClickListener(menuClickListener);

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

        items.clear();
        items.add("선택필수");

        setData(idx);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, items);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(spinnerAdapter);

        tagSet(tag);

        color(inout);

        btnSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(Edit.this);
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

        etMemo.setTextColor(getColor(R.color.black));
        tvDate.setTextColor(getColor(R.color.black));
        etUse.setTextColor(getColor(R.color.black));
        etPrice.setTextColor(getColor(R.color.black));

        enable(false);
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
            tvDate.setOnFocusChangeListener(outFocusChangeListener);
            spinner.setOnFocusChangeListener(outFocusChangeListener);
            etUse.setOnFocusChangeListener(outFocusChangeListener);
            etPrice.setOnFocusChangeListener(outFocusChangeListener);
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

    View.OnFocusChangeListener outFocusChangeListener = new View.OnFocusChangeListener() {
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(Edit.this,
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
                price = etPrice.getText().toString();
                memo = etMemo.getText().toString();
                if(click == 2) {
                    edit("income");

                }
                if(click == 3) {
                    edit("outcome");
                }
            }

            if(v.getId() == R.id.btnEdit) {
                enable(true);
                color(inout);
                lyAdd.setVisibility(View.VISIBLE);
                lyEdit.setVisibility(View.INVISIBLE);
            }
            if(v.getId() == R.id.btnDelete) {
                dialog = new Dialog(Edit.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_del);

                dialog.show();

                Button btnNo, btnYes;

                btnNo = (Button) dialog.findViewById(R.id.btnNo);
                btnYes = (Button) dialog.findViewById(R.id.btnYes);

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        Toast.makeText(Edit.this, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                        del();
                        exit();
                    }
                });
            }
        }
    };

    void enable(boolean check) {
        tvDate.setEnabled(check);
        spinner.setEnabled(check);
        btnSpinner.setEnabled(check);
        etUse.setEnabled(check);
        etPrice.setEnabled(check);
        etMemo.setEnabled(check);
        btnIncome.setEnabled(check);
        btnOutcome.setEnabled(check);
    }
    void exit() {
        Intent intent = new Intent(Edit.this, Main.class);
        intent.putExtra("date", date);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }
    void del() {
        database = sqLiteOpenHelper.getWritableDatabase();
        database.delete("account", "idx='"+ idx +"'", null);

        database.close();
    }

    void edit(String string) {
        if(tag.equals("선택필수") || price.equals("") || use.equals("")) {
            Toast.makeText(this, "분류/내용/금액 필수", Toast.LENGTH_SHORT).show();
        }
        else {
            dataUpdate(string);
            exit();
        }
    }
    void dataUpdate(String inout) {

        database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("date", date);
        contentValues.put("tag", tag);
        contentValues.put("use", use);
        contentValues.put(inout, price);

        contentValues.put("memo", memo);

        database.update("account", contentValues, "idx='"+ idx +"'", null);

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

    void setData(String idx) {
        database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM account WHERE idx = '" + idx + "'", null);

        while (cursor.moveToNext()) {
            idx = cursor.getString(0);
            date = cursor.getString(1);
            tag = cursor.getString(2);
            use = cursor.getString(3);
            String income = cursor.getString(4);
            String outcome = cursor.getString(5);
            memo = cursor.getString(6);

            tvDate.setText(date);
            etUse.setText(use);
            etMemo.setText(memo);

            if (income != null) {
                inout = "income";
                price = income;
            }
            else {
                inout = "outcome";
                price = outcome;
            }
            etPrice.setText(price);

            tag(inout);
        }

        cursor.close();
        database.close();
    }

    void tagSet(String tag) {
        for(int i = 0; i < items.size()+1; i++) {
            if(items.get(i).toString().equals(tag)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}