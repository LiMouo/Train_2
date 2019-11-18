package com.ziker.train.Activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziker.train.Adapter.SubwayQueryAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubwayQueryActivity extends MyAppCompatActivity {
    private static String Url = "GetMetroInfo.do";
    private RecyclerView RV_main;
    private SubwayQueryAdapter adapter;
    private Gson gson = new Gson();
    private List<Map> AllInfo = new ArrayList<>();
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_query);
        super.SetMenu(this,"地铁查询",R.layout.subwayquery_more).
                getExpand().findViewById(R.id.t_Planning).setOnClickListener(v->{
                    Intent intent = new Intent(this,SubwayPlanningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("map",AllInfo.get(0).get("map").toString());
                    intent.putExtras(bundle);
                    startActivity(intent);
                });
        InitView();
        BindData();
    }

    private void InitView(){
        RV_main = findViewById(R.id.RV_main);
    }

    private void BindData(){
        adapter = new SubwayQueryAdapter(this,AllInfo);
        RV_main.setLayoutManager(new LinearLayoutManager(this));
        RV_main.setAdapter(adapter);
        RV_main.addItemDecoration(new MyItemDecoration());
        new Thread(()->{
            try {
                Map map = new HashMap();
                map.put("Line",0);
                String Data = Tools.SendPostRequest(Url,map);
                Log.d(TAG, "BindData: "+Data);
                JSONArray All = new JSONObject(Data).getJSONArray("ROWS_DETAIL");
                AllInfo.clear();
                for (int i = 0; i < All.length(); i++) {
                    JSONObject info = All.getJSONObject(i);
                    Log.d(TAG, "BindData: "+info);
                    Map m = new HashMap();
                    m.put("name",info.getString("name"));
                    m.put("map",info.getString("map"));
                    AllInfo.add(m);
                }
                handler.post(()->adapter.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0,0,0,1);
        }
    }
}
