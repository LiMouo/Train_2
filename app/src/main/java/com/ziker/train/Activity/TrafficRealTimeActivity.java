package com.ziker.train.Activity;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TrafficRealTimeActivity extends MyAppCompatActivity {
    private static String[] Colors = {"#0ebd12","#98ed1f","#ffff01","#ff0103","#4c060e"};
    private static String[] State = {"通畅","较通畅","拥挤","堵塞","爆表"};
    private static String[] names = {"一号道路:","二号道路:","三号道路:"};
    private static String Url = "GetRoadStatus.do";
    private TextView[] t_r = new TextView[3];
    private TextView[] t_color = new TextView[3];
    private TextView t_pm2,t_temperature,t_humidity;
    private Thread thread = null;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficrealtime);
        super.SetMenu(this,"路况查询",null);
        InitView();
        BindData();
    }
    private void InitView(){
        t_r[0] = findViewById(R.id.t_tr1);
        t_r[1] = findViewById(R.id.t_tr2);
        t_r[2] = findViewById(R.id.t_tr3);
        t_color[0] = findViewById(R.id.t_cr1);
        t_color[1] = findViewById(R.id.t_cr2);
        t_color[2] = findViewById(R.id.t_cr3);
        t_pm2 = findViewById(R.id.t_pm2);
        t_temperature = findViewById(R.id.t_temperature);
        t_humidity = findViewById(R.id.t_humidity);
    }

    private void BindData(){
        thread = new Thread(()->{
            try {
                while (true){
                    Cursor cursor = DB.query("Environment",null,null,null,null,null,"id desc");
                    if(cursor.moveToFirst()){
                        handler.post(()->{
                            t_pm2.setText(cursor.getInt(5)+"μg/m3");
                            t_temperature.setText(cursor.getInt(1)+"℃");
                            t_humidity.setText(cursor.getInt(2)+"%");
                        });
                    }
                    for (int i = 0; i < 3; i++) {
                        Map map = new HashMap();
                        map.put("RoadId",i+1);
                        String Data = Tools.SendPostRequest(Url,map);
                        JSONObject json = new JSONObject(Data);
                        int finalI = i;
                        int j = json.getInt("Status") - 1;
                        handler.post(()->{
                            t_r[finalI].setText(names[finalI]+State[j]);
                            t_color[finalI].setBackgroundColor(Color.parseColor(Colors[j]));
                        });
                    }
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    @Override
    protected void onDestroy() {
        if(thread!=null && thread.isAlive())
        {
            thread.interrupt();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
