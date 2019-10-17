package com.ziker.train;

import android.os.Bundle;

import com.ziker.train.Utils.MyAppCompatActivity;

public class TestActivity extends MyAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        super.setMenu(this,"测试页名称",null);
    }
}
