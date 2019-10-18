package com.ziker.train;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ziker.train.Utils.MyAppCompatActivity;

public class MainActivity extends MyAppCompatActivity implements View.OnClickListener {
    private Button btn_ETC,btn_RedGreen,btn_Manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        super.setMenu(this,"首页",null);
        InitView();
    }

    private void InitView(){//初始化操作
        btn_ETC = findViewById(R.id.btn_ETC);
        btn_RedGreen = findViewById(R.id.btn_RedGreen);
        btn_Manager = findViewById(R.id.btn_Manager);
        setbuttoncolor(btn_ETC);
        setbuttoncolor(btn_RedGreen);
        setbuttoncolor(btn_Manager);
        btn_ETC.setOnClickListener(this);
        btn_RedGreen.setOnClickListener(this);
        btn_Manager.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {//点击事件
        Intent intent;
        switch (v.getId()){
            case R.id.btn_ETC:
                intent = new Intent(this,ETCActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_RedGreen:
                intent = new Intent(this,RedGreenActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_Manager:
                intent = new Intent(this,ManagerActivity.class);
                startActivity(intent);
                break;
        }
    }
    public void setbuttoncolor(Button button){//设置动画
        AnimationDrawable animationDrawable = (AnimationDrawable) button.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}
