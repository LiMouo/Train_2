package com.ziker.train.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.ziker.train.Adapter.BusQueryAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BusQueryActivity extends MyAppCompatActivity {
    private static String PlatformUrl = "GetBusStationInfo.do";//站台信息
    private static String CapacityUrl = "GetBusCapacity.do";//车载容量
    private static String[] GroupNames = {"中医院站"," 联想大厦"};
    private List<Map>[] ChildData = new LinkedList[2];//person,carid,distance,time
    private ExpandableListView EL_main;
    private BusQueryAdapter adapter;
    private Button btn_info;
    private Handler handler = new Handler();
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_query);
        super.SetMenu(this,"公交查询",null);
        InitView();
        BindData();
    }

    private void InitView(){
        btn_info = findViewById(R.id.btn_info);
        EL_main = findViewById(R.id.EL_main);
        for (int i = 0; i < ChildData.length; i++) {
            ChildData[i] = new LinkedList<>();
        }
    }

    private void BindData(){
        Tools.SetButtonColor(btn_info);
        btn_info.setOnClickListener(v->{ });
        adapter = new BusQueryAdapter(this,GroupNames, ChildData);
        EL_main.setAdapter(adapter);
        thread = new Thread(()->{
            while (true){
                try {
                    for (int i = 0; i < 2; i++) {
                        Map PlatformMap= new HashMap();
                        Map CapacityMap = new HashMap();
                        PlatformMap.put("BusStationId",i+1);
                        CapacityMap.put("BusId",i+1);
                        String PlatformData = Tools.SendPostRequest(PlatformUrl,PlatformMap);
                        JSONObject Platform = new JSONObject(PlatformData);
                        JSONArray  distance = Platform.getJSONArray("ROWS_DETAIL");
                        List<Map> list = new LinkedList<>();
                        for (int j = 0; j < distance.length(); j++) {
                            JSONObject info = distance.getJSONObject(j);
                            int BusId = info.getInt("BusId")-1;
                            String CapacityData = Tools.SendPostRequest(CapacityUrl,CapacityMap);
                            JSONObject Capacity = new JSONObject(CapacityData);
                            Map map = new HashMap();
                            map.put("person",Capacity.getInt("BusCapacity"));
                            map.put("distance",info.getInt("Distance") /10);
                            map.put("carId",BusId+1);
                            map.put("time",info.getInt("Distance")/10/334);
                            list.add(map);
                        }
                        ChildData[i].clear();
                        ChildData[i] = list;
                    }
                    for (int i = 0; i < ChildData.length; i++) {
                        Collections.sort(ChildData[i], (o1, o2) -> {
                            if(Integer.parseInt(o1.get("distance").toString())>Integer.parseInt(o2.get("distance").toString())) return 1;
                            else return -1;
                        });
                    }
                    handler.post(()->adapter.notifyDataSetChanged());
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    @Override
    protected void onDestroy() {
        if(thread.isAlive()){
            thread.interrupt();
            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
