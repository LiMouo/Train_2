package com.ziker.train.Activity;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ziker.train.Adapter.CustomShuttleOrderListAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.CustomShuttleOrderListInfo;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomShuttleOrderListActivity extends MyAppCompatActivity {
    private static String UrlLine= "GetUserBusLine.do";
    @BindView(R.id.t_pay) TextView t_pay;
    @BindView(R.id.t_notpay) TextView t_notpay;
    @BindView(R.id.EL_main) ExpandableListView EL_main;
    private CustomShuttleOrderListAdapter adapter;
    private List<CustomShuttleOrderListInfo> ListData = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customshuttleorderlist);
        super.SetMenu(this,"我的订单",null).SetOnBackClick(R.drawable.back,()->finish());
        InitView();
        BindData();
    }

    private void InitView(){
        ButterKnife.bind(this);
    }
    private void BindData(){
        t_pay.setSelected(true);
        t_pay.setOnClickListener(v->{
            Sort(false);
            t_pay.setSelected(true);
            t_notpay.setSelected(false);
        });
        t_notpay.setOnClickListener(v->{
            Sort(true);
            t_pay.setSelected(false);
            t_notpay.setSelected(true);
        });
        adapter = new CustomShuttleOrderListAdapter(this,ListData);
        EL_main.setAdapter(adapter);
        Sort(false);
    }

    private void Sort(boolean StateOk){
        new Thread(()->{
            try {
                Gson gson = new Gson();
                ListData.clear();
                String Data = Tools.SendPostRequest(UrlLine,new HashMap());
                JSONObject object = new JSONObject(Data);
                JSONArray array = object.getJSONArray("ROWS_DETAIL");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object1 = array.getJSONObject(i);
                    if(StateOk){
                        if(object1.getInt("Flag")==1){
                            CustomShuttleOrderListInfo info = gson.fromJson(object1.toString(),CustomShuttleOrderListInfo.class);
                            ListData.add(info);
                        }
                    }else {
                        if(object1.getInt("Flag")==0){
                            CustomShuttleOrderListInfo info = gson.fromJson(object1.toString(),CustomShuttleOrderListInfo.class);
                            ListData.add(info);
                        }
                    }
                }
                handler.post(()-> adapter.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
