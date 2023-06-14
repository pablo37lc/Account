package com.hypnos.account;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public class LockPassword extends Fragment {

    TextView textView;
    LinearLayout lyInput;
    ImageView iv1, iv2, iv3, iv4, iv5, iv6;
    Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9, btnClear, btnDelete;

    String password = "";
    String first = "";

    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lock_password, container, false);

        lyInput = (LinearLayout) view.findViewById(R.id.lyInput);
        textView = (TextView) view.findViewById(R.id.textView);
        iv1 = (ImageView) view.findViewById(R.id.iv1);
        iv2 = (ImageView) view.findViewById(R.id.iv2);
        iv3 = (ImageView) view.findViewById(R.id.iv3);
        iv4 = (ImageView) view.findViewById(R.id.iv4);
        iv5 = (ImageView) view.findViewById(R.id.iv5);
        iv6 = (ImageView) view.findViewById(R.id.iv6);
        btn0 = (Button) view.findViewById(R.id.btn0);
        btn1 = (Button) view.findViewById(R.id.btn1);
        btn2 = (Button) view.findViewById(R.id.btn2);
        btn3 = (Button) view.findViewById(R.id.btn3);
        btn4 = (Button) view.findViewById(R.id.btn4);
        btn5 = (Button) view.findViewById(R.id.btn5);
        btn6 = (Button) view.findViewById(R.id.btn6);
        btn7 = (Button) view.findViewById(R.id.btn7);
        btn8 = (Button) view.findViewById(R.id.btn8);
        btn9 = (Button) view.findViewById(R.id.btn9);
        btnClear = (Button) view.findViewById(R.id.btnClear);
        btnDelete = (Button) view.findViewById(R.id.btnDelete);

        lyInput.setVisibility(View.INVISIBLE);
        textView.setText("비밀번호를 설정해주세요");

        btn0.setOnClickListener(btnClickListener);
        btn1.setOnClickListener(btnClickListener);
        btn2.setOnClickListener(btnClickListener);
        btn3.setOnClickListener(btnClickListener);
        btn4.setOnClickListener(btnClickListener);
        btn5.setOnClickListener(btnClickListener);
        btn6.setOnClickListener(btnClickListener);
        btn7.setOnClickListener(btnClickListener);
        btn8.setOnClickListener(btnClickListener);
        btn9.setOnClickListener(btnClickListener);
        btnClear.setOnClickListener(btnClickListener);
        btnDelete.setOnClickListener(btnClickListener);

        return view;
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lyInput.setVisibility(View.VISIBLE);
            textView.setVisibility(View.INVISIBLE);
            if(v.getId() == R.id.btnDelete) {
                if(!password.equals("")) {
                    iv1.setImageResource(R.drawable.dot);
                    iv2.setImageResource(R.drawable.dot);
                    iv3.setImageResource(R.drawable.dot);
                    iv4.setImageResource(R.drawable.dot);
                    iv5.setImageResource(R.drawable.dot);
                    iv6.setImageResource(R.drawable.dot);
                    password = password.substring(0,password.length()-1);
                }
            }
            if(v.getId() == R.id.btnClear) {
                iv1.setImageResource(R.drawable.dot);
                iv2.setImageResource(R.drawable.dot);
                iv3.setImageResource(R.drawable.dot);
                iv4.setImageResource(R.drawable.dot);
                iv5.setImageResource(R.drawable.dot);
                iv6.setImageResource(R.drawable.dot);
                password = "";
            }
            if(v.getId() == R.id.btn0) {
                password += 0;
            }
            if(v.getId() == R.id.btn1) {
                password += 1;
            }
            if(v.getId() == R.id.btn2) {
                password += 2;
            }
            if(v.getId() == R.id.btn3) {
                password += 3;
            }
            if(v.getId() == R.id.btn4) {
                password += 4;
            }
            if(v.getId() == R.id.btn5) {
                password += 5;
            }
            if(v.getId() == R.id.btn6) {
                password += 6;
            }
            if(v.getId() == R.id.btn7) {
                password += 7;
            }
            if(v.getId() == R.id.btn8) {
                password += 8;
            }
            if(v.getId() == R.id.btn9) {
                password += 9;
            }

            switch (password.length()) {
                case 6 :
                    iv6.setImageResource(R.drawable.dot_fill);
                    lyInput.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    if(first.equals("")) {
                        first = password;
                        textView.setText("비밀번호를 다시 확인해주세요");
                    }
                    else {
                        if(first.equals(password)) {
                            SharedPreferences pref = getActivity().getSharedPreferences("pref", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("lock", "password");
                            editor.putString("password", password);
                            editor.commit();

                            textView.setText("저장되었습니다.");

                            FragmentManager fragmentManager = getParentFragment().getChildFragmentManager();
                            fragmentManager.beginTransaction().remove(LockPassword.this).commit();
                            fragmentManager.popBackStack();
                        }
                        else {
                            textView.setText("처음부터 다시해주세요");
                        }
                        first = "";
                    }

                    password = "";
                    iv1.setImageResource(R.drawable.dot);
                    iv2.setImageResource(R.drawable.dot);
                    iv3.setImageResource(R.drawable.dot);
                    iv4.setImageResource(R.drawable.dot);
                    iv5.setImageResource(R.drawable.dot);
                    iv6.setImageResource(R.drawable.dot);
                    break;
                case 5 :
                    iv5.setImageResource(R.drawable.dot_fill);
                case 4 :
                    iv4.setImageResource(R.drawable.dot_fill);
                case 3 :
                    iv3.setImageResource(R.drawable.dot_fill);
                case 2 :
                    iv2.setImageResource(R.drawable.dot_fill);
                case 1 :
                    iv1.setImageResource(R.drawable.dot_fill);
            }
        }
    };
}
