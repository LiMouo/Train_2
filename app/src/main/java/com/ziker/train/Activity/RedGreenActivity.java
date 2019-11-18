package com.ziker.train.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziker.train.Adapter.RedGreenAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.Info.RedGreenInfo;
import com.ziker.train.Utils.ToolClass.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedGreenActivity extends MyAppCompatActivity {
    private static String TrafficUrl = "GetTrafficLightConfigAction.do";
    private List<String> item;
    private List<RedGreenInfo> lightInfo = new ArrayList<>();
    private ArrayAdapter<String> SpinnerAdapter;
    private RecyclerView recyclerView;
    private RedGreenAdapter RecycleAdapter;
    private Spinner spinner;
    private Button btn_query;
    private Handler handler = new Handler();
    private Gson gson = new Gson();
    private int ItemId = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_green);
        super.SetMenu(this,"红绿灯管理",null);
        InitView();
        BindData();
        Query();
    }
    private void  InitView(){
        btn_query = findViewById(R.id.btn_query);
        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.show_recycleview);
    }

    private void BindData(){
        Tools.SetButtonColor(btn_query);
        btn_query.setOnClickListener(v-> Sort(ItemId));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecycleAdapter = new RedGreenAdapter(this,lightInfo);
        recyclerView.setAdapter(RecycleAdapter);

        item = new ArrayList<>();
        item.add("路口升序");
        item.add("路口降序");
        item.add("红灯升序");
        item.add("红灯降序");
        item.add("绿灯升序");
        item.add("绿灯降序");
        item.add("黄灯升序");
        item.add("黄灯降序");
        SpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,item);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(SpinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ItemId = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void Query(){
        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Map map = new HashMap<>();
                    map.put("TrafficLightId",""+i);
                    String Data = Tools.SendPostRequest(TrafficUrl,map);
                    RedGreenInfo redGreenInfo = gson.fromJson(Data, RedGreenInfo.class);
                    redGreenInfo.setId(i);
                    lightInfo.add(redGreenInfo);
                }
                Sort(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void Sort(int i){
        switch (i){
            case 1:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getId()>o2.getId())return 1;
                    else return -1;
                });
                break;
            case 2:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getId()<o2.getId())return 1;
                    else return -1;
                });
                break;
            case 3:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getRedTime()>o2.getRedTime())return 1;
                    else return -1;
                });
                break;
            case 4:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getRedTime()<o2.getRedTime())return 1;
                    else return -1;
                });
                break;
            case 5:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getYellowTime()>o2.getYellowTime())return 1;
                    else return -1;
                });
                break;
            case 6:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getYellowTime()<o2.getYellowTime())return 1;
                    else return -1;
                });
                break;
            case 7:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getGreenTime()>o2.getGreenTime())return 1;
                    else return -1;
                });
                break;
            case 8:
                Collections.sort(lightInfo,(o1, o2)->{
                    if(o1.getGreenTime()<o2.getGreenTime())return 1;
                    else return -1;
                });
                break;
        }
        handler.post(() -> RecycleAdapter.notifyDataSetChanged());
    }
}
