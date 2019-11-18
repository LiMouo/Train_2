package com.ziker.train.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.WeatherInformationAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeatherInformationActivity extends MyAppCompatActivity {
    private static String Url = "GetWeather.do";
    private RecyclerView RV_main;
    private WeatherInformationAdapter adapter;
    private TextView t_date,t_week,t_temperature;
    private List<Map> AllInfo = new ArrayList<>();
    private Handler handler = new Handler();
    private Animation animation;
    private View Icon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weatherinformation);
        LinearLayout view =(LinearLayout) super.SetMenu(this,"天气信息",R.layout.weatherinformation_more).getExpand();
        view.setGravity(Gravity.END);
        Icon = view.findViewById(R.id.i_icon);
        Icon.setOnClickListener(v-> SetData(true));
        InitView();
        BindData();
    }

    private void InitView(){
        RV_main = findViewById(R.id.RV_main);
        t_date = findViewById(R.id.t_date);
        t_week = findViewById(R.id.t_week);
        t_temperature = findViewById(R.id.t_temperature);
    }

    private void BindData(){
        animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator l = new LinearInterpolator();
        animation.setInterpolator(l);
        RV_main.setLayoutManager(new GridLayoutManager(this,5));
        adapter = new WeatherInformationAdapter(this,AllInfo);
        RV_main.setAdapter(adapter);
        SetData(false);
    }

    private void SetData(boolean isClick){
        if(isClick){
            Icon.startAnimation(animation);
            Icon.setEnabled(false);
        }
        new Thread(()->{
            try {
                AllInfo.clear();
                String Data = Tools.SendPostRequest(Url,new HashMap());
                JSONObject json = new JSONObject(Data);
                String temperature = json.getString("WCurrent");
                JSONArray jsonArray = json.getJSONArray("ROWS_DETAIL");
                //要求显示五条，给了六条，留一条
                for (int i = 0; i < jsonArray.length()-1; i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Map map = new HashMap();
                    map.put("WData",jsonObject.getString("WData"));
                    map.put("type",jsonObject.getString("type"));
                    map.put("temperature",jsonObject.getString("temperature"));
                    AllInfo.add(map);
                }
                handler.postDelayed(()->{
                    try {
                        if(isClick){
                            Icon.clearAnimation();
                            Icon.setEnabled(true);
                        }
                        Map map =AllInfo.get(0);
                        long oldDate = 0;//字符串转时间
                        oldDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("WData").toString() + " 00:00:01").getTime();
                        String week = new SimpleDateFormat("EEEE").format(new Date(oldDate));//时间转字符串
                        String[] Date = map.get("WData").toString().split("-");
                        t_date.setText(Date[0]+"年"+Date[1]+"月"+Date[2]+"日");
                        t_week.setText("周"+week.substring(2,3));
                        t_temperature.setText(temperature+"度");
                        adapter.notifyDataSetChanged();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                },300);
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(()->{
                    if(isClick){
                        Icon.clearAnimation();
                        Icon.setEnabled(true);
                    }
                    Tools.Toast(this,"更新数据失败",false);
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
