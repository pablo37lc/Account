package com.hypnos.account;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class IntroLockPattern extends Fragment {

    PatternLockView patternLockView;

    TextView textView;

    FrameLayout intro;
    String restorePassword = "";

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock_pattern, container, false);

        textView = (TextView) view.findViewById(R.id.textView);

        textView.setText("패턴을 입력해주세요");

        intro = (FrameLayout) view.findViewById(R.id.intro);

        patternLockView = (PatternLockView) view.findViewById(R.id.patternLockView);

        restorePassword();

        patternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {

            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {

            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {

                String password = PatternLockUtils.patternToString(patternLockView, pattern);

                if (password.equals(restorePassword)) {
                    patternLockView.setCorrectStateColor(ContextCompat.getColor(getContext(), R.color.income));
                    intro.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getContext(), Main.class));
                            getActivity().finish();
                        }
                    }, 250);
                }

                else {
                    textView.setText("다시 확인해주세요");
                    patternLockView.setCorrectStateColor(ContextCompat.getColor(getContext(), R.color.outcome));
                }
            }

            @Override
            public void onCleared() {

            }
        });

        return view;
    }

    void restorePassword() {

        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);

        restorePassword = pref.getString("password", "");

    }
}