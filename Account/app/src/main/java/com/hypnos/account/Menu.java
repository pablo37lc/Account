package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class Menu extends AppCompatActivity {

    FrameLayout frameLayout;

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction;

    Lock lock = new Lock();
    DataDel dataDel = new DataDel();
    ImageButton btnBack;
    TextView textView;
    Button btnLock, btnData;
    String date, from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        Intent getData = getIntent();
        if(getData.getStringExtra("date") != null) {
            date = getData.getStringExtra("date");
        }
        if(getData.getStringExtra("from") != null) {
            from = getData.getStringExtra("from");
        }

        btnBack = (ImageButton) findViewById(R.id.btnBack);
        btnLock = (Button) findViewById(R.id.btnLock);
        btnData = (Button) findViewById(R.id.btnData);

        textView = (TextView) findViewById(R.id.textView);

        btnLock.setOnClickListener(clickListener);
        btnData.setOnClickListener(clickListener);

        transaction = fragmentManager.beginTransaction();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Menu.this, Main.class);
                intent.putExtra("date", date);
                intent.putExtra("from", from);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
                finish();
            }
        });
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnLock.setBackgroundColor(getColor(R.color.lightGrey));
            btnLock.setTextColor(getColor(R.color.black));
            btnData.setBackgroundColor(getColor(R.color.lightGrey));
            btnData.setTextColor(getColor(R.color.black));

            if (v.getId() == R.id.btnLock) {
                btnLock.setBackgroundColor(getColor(R.color.income));
                btnLock.setTextColor(getColor(R.color.white));
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, lock).commitAllowingStateLoss();
            }
            if (v.getId() == R.id.btnData) {
                btnData.setBackgroundColor(getColor(R.color.outcome));
                btnData.setTextColor(getColor(R.color.white));
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, dataDel).commitAllowingStateLoss();
                textView.setText("데이터를 삭제했습니다");
            }
            textView.setVisibility(View.INVISIBLE);
        }
    };

}