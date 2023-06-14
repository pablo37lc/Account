package com.hypnos.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Intro extends AppCompatActivity {

    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction transaction;

    FrameLayout intro;
    IntroLockPassword introLockPassword = new IntroLockPassword();
    IntroLockPattern introLockPattern = new IntroLockPattern();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);

        intro = (FrameLayout) findViewById(R.id.intro);

        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);

        String isLock = pref.getString("lock", "");

        if(isLock.equals("false") || isLock.equals("")) {
            intro.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Intro.this, Main.class));
                    finish();
                }
            }, 250);

        }
        else if (isLock.equals("password")){
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, introLockPassword).commitAllowingStateLoss();
        }
        else if (isLock.equals("pattern")) {
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout, introLockPattern).commitAllowingStateLoss();
        }
    }
}