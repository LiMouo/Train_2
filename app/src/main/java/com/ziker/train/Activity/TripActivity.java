package com.ziker.train.Activity;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TripActivity extends MyAppCompatActivity implements View.OnClickListener,TextWatcher {
    private Switch[] s_Car = new Switch[3];
    private PopupWindow popupWindow;
    private TextView t_date,t_single,t_number;
    private int Year,Mouth,Day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        super.SetMenu(this,"出行管理",null);
        InitView();
        setAnimation(new int[]{R.id.light_red,R.id.light_yellow,R.id.light_greed});
    }

    private void InitView(){
        t_date = findViewById(R.id.t_date);
        t_single = findViewById(R.id.t_single);
        t_number = findViewById(R.id.t_number);
        s_Car[0] = findViewById(R.id.s_one);
        s_Car[1] = findViewById(R.id.s_two);
        s_Car[2] = findViewById(R.id.s_three);
        for (int i = 0; i < s_Car.length; i++) {
            int finalI = i;
            s_Car[i].setOnCheckedChangeListener((view, isChecked)->{
                if(isChecked)
                    s_Car[finalI].setText("开");
                else
                    s_Car[finalI].setText("停");
            });
        }
        t_date.setOnClickListener(this);
        t_date.addTextChangedListener(this);
        Year = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date(System.currentTimeMillis())));
        Mouth = Integer.parseInt(new SimpleDateFormat("MM").format(new Date(System.currentTimeMillis())));
        Day = Integer.parseInt(new SimpleDateFormat("dd").format(new Date(System.currentTimeMillis())));
        t_date.setText(Year+"年"+Mouth+"月"+Day+"日");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.t_date:
                try {
                    View view = LayoutInflater.from(this).inflate(R.layout.trip_calendar,null);
                    CalendarView calendar = view.findViewById(R.id.calendar);
                    calendar.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Year+"-"+Mouth+"-"+Day+" 00:00:01").getTime());
                    calendar.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
                        popupWindow.dismiss();
                        Year = year;
                        Mouth = month+1;
                        Day = dayOfMonth;
                        t_date.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");
                    });
                    popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
                    popupWindow.setOutsideTouchable(true);
                    popupWindow.setFocusable(true);
                    popupWindow.showAtLocation(view, Gravity.RIGHT,0,0);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void setAnimation(int[] id){//设置动画
        for (int i = 0; i < id.length; i++) {
            AnimationDrawable animationDrawable = (AnimationDrawable) findViewById(id[i]).getBackground();
            animationDrawable.setExitFadeDuration(1000);
            animationDrawable.start();
        }
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }
    @Override
    public void afterTextChanged(Editable s) {
        if(Day%2!=0){
            t_single.setText("单号出行车辆:");
            t_number.setText("1、3");
            s_Car[0].setChecked(true);
            s_Car[0].setEnabled(true);
            s_Car[1].setChecked(false);
            s_Car[1].setEnabled(false);
            s_Car[2].setChecked(true);
            s_Car[2].setEnabled(true);
        }else {
            t_single.setText("双号出行车辆:");
            t_number.setText("2");
            s_Car[0].setChecked(false);
            s_Car[0].setEnabled(false);
            s_Car[1].setChecked(true);
            s_Car[1].setEnabled(true);
            s_Car[2].setChecked(false);
            s_Car[2].setEnabled(false);
        }
    }
}
