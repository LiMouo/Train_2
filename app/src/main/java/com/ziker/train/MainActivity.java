package com.ziker.train;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ziker.train.Activity.AccountMangerActivity;
import com.ziker.train.Activity.BusQueryActivity;
import com.ziker.train.Activity.ETCActivity;
import com.ziker.train.Activity.EnvironmentalActivity;
import com.ziker.train.Activity.ManagerActivity;
import com.ziker.train.Activity.RedGreenActivity;
import com.ziker.train.Activity.RedGreenControllerActivity;
import com.ziker.train.Activity.ThresholdSettingActivity;
import com.ziker.train.Activity.TrafficActivity;
import com.ziker.train.Activity.TripActivity;
import com.ziker.train.Activity.VORActivity;
import com.ziker.train.Activity.ViolationActivity;
import com.ziker.train.Service.SaveEnvironmental;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

public class MainActivity extends MyAppCompatActivity implements View.OnClickListener {
    private Button btn_ETC,btn_RedGreen,btn_Manager;
    private Button btn_VOR,btn_Environmental,btn_Threshold;
    private Button btn_Trip,btn_account,btn_BusQuery,btn_RedGreenController;
    private Button btn_Violation,btn_Traffic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.SetPort("192.168.3.5","8088");
        setContentView(R.layout.activity_main);
        super.SetMenu(this,"首页",null);
        InitView();
        BindData();
        startService();
        CheckPermission();
    }

    private void InitView(){//初始化操作
        btn_ETC = findViewById(R.id.btn_ETC);
        btn_RedGreen = findViewById(R.id.btn_RedGreen);
        btn_Manager = findViewById(R.id.btn_Manager);
        btn_VOR = findViewById(R.id.btn_VOR);
        btn_Environmental = findViewById(R.id.btn_Environmental);
        btn_Threshold = findViewById(R.id.btn_Threshold);
        btn_Trip = findViewById(R.id.btn_Trip);
        btn_account = findViewById(R.id.btn_Account);
        btn_BusQuery = findViewById(R.id.btn_BusQuery);
        btn_RedGreenController = findViewById(R.id.btn_RedGreenController);
        btn_Violation = findViewById(R.id.btn_Violation);
        btn_Traffic = findViewById(R.id.btn_Traffic);
    }

    private void BindData(){
        setBackColor();
        btn_ETC.setOnClickListener(this);
        btn_RedGreen.setOnClickListener(this);
        btn_Manager.setOnClickListener(this);
        btn_VOR.setOnClickListener(this);
        btn_Environmental.setOnClickListener(this);
        btn_Threshold.setOnClickListener(this);
        btn_Trip.setOnClickListener(this);
        btn_account.setOnClickListener(this);
        btn_BusQuery.setOnClickListener(this);
        btn_RedGreenController.setOnClickListener(this);
        btn_Violation.setOnClickListener(this);
        btn_Traffic.setOnClickListener(this);
    }

    private void setBackColor(){
        Tools.SetButtonColor(btn_ETC);
        Tools.SetButtonColor(btn_RedGreen);
        Tools.SetButtonColor(btn_Manager);
        Tools.SetButtonColor(btn_VOR);
        Tools.SetButtonColor(btn_Environmental);
        Tools.SetButtonColor(btn_Threshold);
        Tools.SetButtonColor(btn_Trip);
        Tools.SetButtonColor(btn_account);
        Tools.SetButtonColor(btn_BusQuery);
        Tools.SetButtonColor(btn_RedGreenController);
        Tools.SetButtonColor(btn_Violation);
        Tools.SetButtonColor(btn_Traffic);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setBackColor();
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
        }
        startActivity(intent);
    }

    private void startService(){
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
