package com.hypnos.account;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class LockPattern extends Fragment {

    PatternLockView patternLockView;

    TextView textView;

    String first = "";

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock_pattern, container, false);

        textView = (TextView) view.findViewById(R.id.textView);

        textView.setText("패턴을 설정해주세요");

        patternLockView = (PatternLockView) view.findViewById(R.id.patternLockView);

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

                if (first.equals("")) {
                    first = password;
                    textView.setText("패턴을 다시 확인해주세요");
                }
                else {
                    if(first.equals(password)) {
                        SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("lock", "pattern");
                        editor.putString("password", password);
                        editor.commit();

                        textView.setText("저장되었습니다.");

                        FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                        fragmentManager.beginTransaction().remove(LockPattern.this).commit();
                        fragmentManager.popBackStack();
                    }
                    else {
                        textView.setText("처음부터 다시해주세요");
                    }
                    first = "";
                }
                patternLockView.clearPattern();
            }

            @Override
            public void onCleared() {

            }
        });
        return view;
    }

}