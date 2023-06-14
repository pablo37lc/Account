package com.hypnos.account;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Calender extends Fragment {

    Calender(String date) {
        this.date = date;
    }
    String date;
    LocalDate selectedDate;
    CalendarAdapter adapter;
    RecyclerView recyclerView;
    ArrayList<LocalDate> dayList;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calender, container, false);

        //초기화
        recyclerView = view.findViewById(R.id.recyclerView);

        //현재 날짜
        selectedDate = LocalDate.parse(date);

        dayList = daysInMonthArray(selectedDate);

        adapter = new CalendarAdapter(dayList);

        recyclerView.setAdapter(adapter);

        //레이아웃 설정(열 7개)
        RecyclerView.LayoutManager manager = new GridLayoutManager(getContext(), 7);

        //레이아웃 적용
        recyclerView.setLayoutManager(manager);

        //화면 설정
        setMonthView();

        return view;
    }//onCreate

    //날짜 타입 설정(2020년 4월)
    private String yearMonthFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");

        return date.format(formatter);
    }

    //화면 설정
    private void setMonthView(){

        dayList = daysInMonthArray(selectedDate);

        adapter = new CalendarAdapter(dayList);

        recyclerView.setAdapter(adapter);

        adapter.setOnClickListener(new CalendarAdapter.RecyclerViewClickListener() {
            @Override
            public void onItemClicked(int position, String check) {
                LocalDate day = dayList.get(position);
                if(check.equals("true")) {
                    ((Main) getActivity()).btnBack.setRotation(90);
                    date = day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    ((Main) getActivity()).viewDay(date, ((Main) getActivity()).dayNum, ((Main) getActivity()).monthNum);
                }
                else if (check.equals("false")){
                    Intent intent = new Intent(getActivity(), Add.class);
                    intent.putExtra("date", day.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                    intent.putExtra("from", "calendar");
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    getActivity().finish();
                }
            }
        });

    }

    //날짜 생성
    private ArrayList<LocalDate> daysInMonthArray(LocalDate date){

        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        //해당 월 마지막 날짜 가져오기(예 28, 30, 31)
        int lastDay = yearMonth.lengthOfMonth();

        //해당 월의 첫 번째 날 가져오기(예 4월1일)
        LocalDate firstDay = selectedDate.withDayOfMonth(1);

        //첫 번째 날 요일 가져오기(월:1 , 토:6, 일:0)
        int dayOfWeek = firstDay.getDayOfWeek().getValue() % 7;

        //날짜 생성
        int columDate = 43;
        if(lastDay + dayOfWeek < 35) {
                columDate = 36;
        }
        for(int i = 1; i < columDate; i++){

            if( i - 1 < dayOfWeek || i - 1 >= lastDay + dayOfWeek){

                dayList.add(null);
            }else{
                dayList.add(LocalDate.of(selectedDate.getYear(), selectedDate.getMonth(),
                        i - dayOfWeek));
            }
        }

        return dayList;
    }
}