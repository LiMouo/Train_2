package com.ziker.train;

import android.graphics.drawable.AnimationDrawable;
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

import com.google.gson.Gson;
import com.ziker.train.Utils.LightInfo;
import com.ziker.train.Utils.MyAppCompatActivity;
import com.ziker.train.Utils.RedGreenAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ziker.train.ETCActivity.JSON;

public class RedGreenActivity extends MyAppCompatActivity implements View.OnClickListener {
    private final static String TAG = "RedGreenActivity";
    private Button btn_query;
    private Spinner spinner;
    private List<String> item;
    private ArrayAdapter<String> adapter;
    private Handler handler = new Handler();
    private Gson gson = new Gson();
    private LightInfo[] lightinfo = new LightInfo[5];
    private RecyclerView recyclerView;
    private int itemid = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_green);
        super.setMenu(this,"红绿灯管理",null);
        InitView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemid = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
    private void  InitView(){
        btn_query = findViewById(R.id.btn_query);
        btn_query.setOnClickListener(this);
        setbuttoncolor(btn_query);

        recyclerView = findViewById(R.id.show_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        spinner = findViewById(R.id.spinner);
        item = new ArrayList<>();
        item.add("路口升序");
        item.add("路口降序");
        item.add("红灯升序");
        item.add("红灯降序");
        item.add("绿灯升序");
        item.add("绿灯降序");
        item.add("黄灯升序");
        item.add("黄灯降序");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        query();
    }
    private void query(){
        new Thread(() -> {
            try {
                for (int i = 1; i <= 5; i++) {
                    Map map = new HashMap<>();
                    map.put("TrafficLightId",""+i);
                    map.put("UserName","user1");
                    String parms = gson.toJson(map);
                    RequestBody requestBody = RequestBody.create(JSON,parms);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder().url("http://192.168.3.5:8088/transportservice/action/GetTrafficLightConfigAction.do").post(requestBody).build();
                    Response response = okHttpClient.newCall(request).execute();
                    String Data = response.body().string();
                    Log.d(TAG, "query: "+Data);
                    lightinfo[i-1] = gson.fromJson(Data,LightInfo.class);
                    lightinfo[i-1].setId(i);
                }
                sort(1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_query:
                sort(itemid);
                break;
        }
    }
    private void sort(int i){
        switch (i){
            case 1:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getId()>o2.getId())return 1;
                    else return -1;
                });
                break;
            case 2:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getId()<o2.getId())return 1;
                    else return -1;
                });
                break;
            case 3:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getRedTime()>o2.getRedTime())return 1;
                    else return -1;
                });
                break;
            case 4:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getRedTime()<o2.getRedTime())return 1;
                    else return -1;
                });
                break;
            case 5:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getYellowTime()>o2.getYellowTime())return 1;
                    else return -1;
                });
                break;
            case 6:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getYellowTime()<o2.getYellowTime())return 1;
                    else return -1;
                });
                break;
            case 7:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getGreenTime()>o2.getGreenTime())return 1;
                    else return -1;
                });
                break;
            case 8:
                Arrays.sort(lightinfo,(o1, o2)->{
                    if(o1.getGreenTime()<o2.getGreenTime())return 1;
                    else return -1;
                });
                break;
        }
        handler.post(() -> recyclerView.setAdapter(new RedGreenAdapter(getApplicationContext(),lightinfo)));
    }

    public void setbuttoncolor(Button button){
        AnimationDrawable animationDrawable = (AnimationDrawable) button.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}
