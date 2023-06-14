package com.hypnos.account;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Lock  extends Fragment {

    FrameLayout frameLayout;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    Button btnPassword, btnPattern, btnDelete;
    TextView textView;

    LockPattern lockPattern = new LockPattern();
    LockPassword lockPassword = new LockPassword();

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock, container, false);

        btnPassword = (Button) view.findViewById(R.id.btnPassword);
        btnPattern = (Button) view.findViewById(R.id.btnPattern);
        btnDelete = (Button) view.findViewById(R.id.btnDelete);

        textView = (TextView) view.findViewById(R.id.textView);

        btnPassword.setOnClickListener(clickListener);
        btnPattern.setOnClickListener(clickListener);

        fragmentManager = getChildFragmentManager();
        transaction = fragmentManager.beginTransaction();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnPassword.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
                btnPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
                btnPattern.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
                btnPattern.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

                SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("lock", "false");
                editor.putString("password", "");
                editor.commit();

                transaction = fragmentManager.beginTransaction();
                transaction.remove(lockPattern);
                transaction.remove(lockPassword);
                transaction.commit();
                textView.setText("잠금화면을 해제했습니다");
            }
        });

        return view;
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            btnPassword.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
            btnPattern.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.lightGrey));
            btnPattern.setTextColor(ContextCompat.getColor(getContext(), R.color.black));

            textView.setText("잠금화면을 설정하였습니다");
            if (v.getId() == R.id.btnPassword) {
                btnPassword.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.income));
                btnPassword.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, lockPassword).commitAllowingStateLoss();

            }
            if (v.getId() == R.id.btnPattern) {
                btnPattern.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.income));
                btnPattern.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.frameLayout, lockPattern).commitAllowingStateLoss();
            }
        }
    };


}
