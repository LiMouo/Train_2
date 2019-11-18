package com.ziker.train;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.ziker.train.Activity.AccountMangerActivity;
import com.ziker.train.Activity.BusQueryActivity;
import com.ziker.train.Activity.CustomShuttleActivity;
import com.ziker.train.Activity.ETCActivity;
import com.ziker.train.Activity.EnvironmentalActivity;
import com.ziker.train.Activity.ManagerActivity;
import com.ziker.train.Activity.RedGreenActivity;
import com.ziker.train.Activity.RedGreenControllerActivity;
import com.ziker.train.Activity.SubwayQueryActivity;
import com.ziker.train.Activity.ThresholdSettingActivity;
import com.ziker.train.Activity.TrafficActivity;
import com.ziker.train.Activity.TrafficRealTimeActivity;
import com.ziker.train.Activity.TripActivity;
import com.ziker.train.Activity.VORActivity;
import com.ziker.train.Activity.ViolationActivity;
import com.ziker.train.Activity.WeatherInformationActivity;
import com.ziker.train.Service.SaveEnvironmental;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends MyAppCompatActivity implements View.OnClickListener {
    private List<Button> buttons = new ArrayList<>();
    private int[] buttonsID = {R.id.btn_ETC,R.id.btn_RedGreen,R.id.btn_Manager,R.id.btn_VOR,R.id.btn_Environmental,R.id.btn_Threshold,R.id.btn_Trip,R.id.btn_Account,R.id.btn_BusQuery,
            R.id.btn_RedGreenController,R.id.btn_Violation,R.id.btn_Traffic,R.id.btn_TrafficRealTime,R.id.btn_SubwayQuery,R.id.btn_WeatherInformation,R.id.btn_CustomShuttle};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        super.SetPort("192.168.3.5","8088");
        Log.d(TAG, "onCreate: "+Ip+"\t"+Port);
        setContentView(R.layout.activity_main);
        super.SetMenu(this,"首页",null);
        InitView();
        BindData();
        StartService();
//        CheckPermission();
    }

    private void InitView(){//初始化操作
        for (int i = 0; i < buttonsID.length; i++) {
            Button button = findViewById(buttonsID[i]);
            buttons.add(button);
        }
    }

    private void BindData(){
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            button.setOnClickListener(this);
            Tools.SetButtonColor(button);
        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            Tools.SetButtonColor(button);
        }
    }

    private void CheckPermission(){
//        SharedPreferences sp = getSharedPreferences(TAG,MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        if(sp.getBoolean("isFirst",true)){
//            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//
//            }
//            editor.putBoolean("isFirst",false);
//            editor.apply();
//        }
    }

    @Override
    public void onClick(View v) {//点击事件
        Intent intent = null;
        switch (v.getId()){
            case R.id.btn_ETC:
                intent = new Intent(this, ETCActivity.class);
                break;
            case R.id.btn_RedGreen:
                intent = new Intent(this, RedGreenActivity.class);
                break;
            case R.id.btn_Manager:
                intent = new Intent(this, ManagerActivity.class);
                break;
            case R.id.btn_VOR:
                intent = new Intent(this, VORActivity.class);
                break;
            case R.id.btn_Environmental:
                intent = new Intent(this, EnvironmentalActivity.class);
                break;
            case R.id.btn_Threshold:
                intent = new Intent(this, ThresholdSettingActivity.class);
                break;
            case R.id.btn_Trip:
                intent = new Intent(this, TripActivity.class);
                break;
            case R.id.btn_Account:
                intent = new Intent(this, AccountMangerActivity.class);
                break;
            case R.id.btn_BusQuery:
                intent = new Intent(this, BusQueryActivity.class);
                break;
            case R.id.btn_RedGreenController:
                intent = new Intent(this, RedGreenControllerActivity.class);
                break;
            case R.id.btn_Violation:
                intent = new Intent(this, ViolationActivity.class);
                break;
            case R.id.btn_Traffic:
                intent = new Intent(this, TrafficActivity.class);
                break;
            case R.id.btn_TrafficRealTime:
                intent = new Intent(this, TrafficRealTimeActivity.class);
                break;
            case R.id.btn_SubwayQuery:
                intent = new Intent(this, SubwayQueryActivity.class);
                break;
            case R.id.btn_WeatherInformation:
                intent = new Intent(this, WeatherInformationActivity.class);
                break;
            case R.id.btn_CustomShuttle:
                intent = new Intent(this, CustomShuttleActivity.class);
                break;
        }
        startActivity(intent);
    }

    private void StartService(){
        startService(new Intent(this, SaveEnvironmental.class));
    }

    private void stopService(){
        stopService(new Intent(this, SaveEnvironmental.class));
    }

    @Override
    protected void onDestroy() {
        stopService();
        super.onDestroy();
    }
}
