package com.ziker.train.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.EnvironmentalAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;


public class EnvironmentalActivity extends MyAppCompatActivity{
    private static String[] names = {"温度","湿度","光照","CO2","pm2.5","道路状态"};
    private RecyclerView RV_main;
    private EnvironmentalAdapter adapter;
    private Handler handler = new Handler();
    private Thread thread;
    private int[] list= new int[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environmental);
        super.SetMenu(this,"环境指标",null);
        InitView();
        BindData();
        QueryData();
    }

    private void InitView(){
        RV_main = findViewById(R.id.RV_main);
    }

    private void BindData(){
        RV_main.setLayoutManager(new GridLayoutManager(this,3));
        adapter = new EnvironmentalAdapter(this, list,names);
        RV_main.setAdapter(adapter);
    }
    private void QueryData(){
        thread = new Thread(() -> {
            while (true) {
                Cursor cursor = DB.query("Environment",null,null,null,null,null,"id desc");
                if(cursor.moveToFirst()){
                    for (int j = 1; j < 7; j++) {
                        list[j-1] = cursor.getInt(j);
                    }
                }
                handler.post(() -> adapter.notifyDataSetChanged());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        thread.interrupt();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
