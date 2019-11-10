package com.ziker.train.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.ManagerAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.ManagerInfo;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends MyAppCompatActivity {
    private List<ManagerInfo> AllInfo = new ArrayList<>();
    private RecyclerView RV_main;
    private ManagerAdapter RecycleAdapter;
    private Spinner spinner;
    private Button btn_query;
    private Handler handler = new Handler();
    private int ItemID = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        super.SetMenu(this,"充值记录",null);
        InitView();
        BindData();
        Query();
    }
    private void InitView(){//初始化操作
        btn_query = findViewById(R.id.btn_query);
        spinner = findViewById(R.id.spinner);
        RV_main = findViewById(R.id.RV_main);
    }

    private void BindData(){
        Tools.SetButtonColor(btn_query);
        btn_query.setOnClickListener(v->Query());
        RecycleAdapter = new ManagerAdapter(this,AllInfo);
        RV_main.setLayoutManager(new LinearLayoutManager(this));
        RV_main.setAdapter(RecycleAdapter);
        List<String> item = new ArrayList<>();
        item.add("时间升序");
        item.add("时间降序");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemID = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void Query(){//查询
        new Thread(() -> {
            Cursor cursor;
            if(ItemID ==0){
                cursor = DB.query("use",null,null,null,null,null,"DATE");
            }else{
                cursor = DB.query("use",null,null,null,null,null,"DATE desc");
            }
            List<ManagerInfo> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                for (int i =0;i < cursor.getCount();i++){
                    ManagerInfo managerinfo = new ManagerInfo();
                    managerinfo.setId(cursor.getInt(0));
                    managerinfo.setCarId(cursor.getInt(1));
                    managerinfo.setUser(cursor.getString(2));
                    managerinfo.setTime(cursor.getString(3));
                    managerinfo.setMoney(cursor.getInt(4));
                    list.add(managerinfo);
                    cursor.moveToNext();
                }
            }
            Log.d(TAG, "Query: "+AllInfo.hashCode());
            AllInfo.clear();
            AllInfo.addAll(list);
            Log.d(TAG, "Query: "+AllInfo.hashCode());
            handler.post(() -> RecycleAdapter.notifyDataSetChanged());
        }).start();
    }

}
