package com.ziker.train.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.ziker.train.Adapter.RedGreenControllerAdapter;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class RedGreenControllerActivity extends MyAppCompatActivity {
    private static String RedGreenUrl = "GetTrafficLightConfigAction.do";
    private static String RedGreenController = "SetTrafficLightConfig.do";
    private List<String> item = new ArrayList<>();
    private List<RedGreenInfo> redGreenInfo = new ArrayList<>();
    private List<Integer> isChecks = new ArrayList<>();
    private ArrayAdapter<String> SpinnerAdapter;
    private Spinner SP_main;
    private RecyclerView RV_main;
    private Button btn_query,btn_set;
    private RedGreenControllerAdapter adapter;
    private int SpinnerSelectPosition = 0;
    private Handler handler = new Handler();
    private Gson gson = new Gson();

    private AlertDialog alertDialog;
    private View view;
    private EditText e_red ,e_yellow,e_green;
    private Button btn_ok,btn_cancle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_green_controller);
        SetMenu(this,"红绿灯管理",null);
        InitView();
        BindData();
        ChangeData();
    }

    /*初始化控件变量*/
    private void InitView(){
        SP_main = findViewById(R.id.SP_main);
        RV_main = findViewById(R.id.RV_main);
        btn_query = findViewById(R.id.btn_query);
        btn_set = findViewById(R.id.btn_set);
        view = LayoutInflater.from(this).inflate(R.layout.redgreencontroller_dialog, null);
        e_red = view.findViewById(R.id.e_red);
        e_yellow = view.findViewById(R.id.e_yellow);
        e_green = view.findViewById(R.id.e_green);
        btn_ok = view.findViewById(R.id.btn_ok);
        btn_cancle = view.findViewById(R.id.btn_cancle);
    }

    private void BindData(){
        Tools.SetButtonColor(btn_cancle);
        Tools.SetButtonColor(btn_ok);
        Tools.SetButtonColor(btn_query);
        Tools.SetButtonColor(btn_set);
        item.add("路口升序");
        item.add("路口降序");
        item.add("红灯升序");
        item.add("红灯降序");
        item.add("绿灯升序");
        item.add("绿灯降序");
        item.add("黄灯升序");
        item.add("黄灯降序");
        SpinnerAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,item);
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        SP_main.setAdapter(SpinnerAdapter);
        SP_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerSelectPosition = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        RV_main.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RedGreenControllerAdapter(this, redGreenInfo, position->{
            List<Integer> list = new ArrayList<>();
            list.add(position);
            SetInfo(list);
        }, isChecks->this.isChecks = isChecks);
        RV_main.setAdapter(adapter);
        btn_query.setOnClickListener( v -> Sort(SpinnerSelectPosition+1));
        btn_set.setOnClickListener( v -> SetInfo(isChecks));
    }

    private void ChangeData(){
        new Thread(()->{
            try {
                redGreenInfo.clear();
                for (int i = 0; i < 5; i++) {
                    Map map = new HashMap();
                    map.put("TrafficLightId",i+1);
                    String Data = Tools.SendRequest(RedGreenUrl,map);
                    RedGreenInfo red = gson.fromJson(Data,RedGreenInfo.class);
                    red.setId(i+1);
                    redGreenInfo.add(red);
                }
                Sort(SpinnerSelectPosition);
                handler.post(()-> adapter.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void SetInfo(List<Integer> sChecks){
        if(sChecks.size()>0){
            if(sChecks.size() == 1){
                e_red.setHint(redGreenInfo.get(sChecks.get(0)).getRedTime()+"");
                e_yellow.setHint(redGreenInfo.get(sChecks.get(0)).getYellowTime()+"");
                e_green.setHint(redGreenInfo.get(sChecks.get(0)).getGreenTime()+"");
            }else {
                e_red.setHint("");
                e_yellow.setHint("");
                e_green.setHint("");
            }
            if(alertDialog ==null)
                alertDialog = new AlertDialog.Builder(this).setView(view).create();
            alertDialog.show();
            btn_ok.setOnClickListener(v->{
                ProgressDialog progressDialog = Tools.WaitDialog(this,"正在设置");
                new Thread(()->{
                    AtomicBoolean isSuccess = new AtomicBoolean(true);
                    handler.post(()->alertDialog.dismiss());
                    handler.post(()->progressDialog.show());
                    for (int i = 0; i < sChecks.size(); i++) {
                        try {
                            Map map = new HashMap();
                            map.put("TrafficLightId", redGreenInfo.get(sChecks.get(i)).getId());
                            map.put("RedTime",e_red.getText().toString().equals("")? redGreenInfo.get(sChecks.get(i)).getRedTime() : Integer.parseInt(e_red.getText().toString()));
                            map.put("YellowTime",e_yellow.getText().toString().equals("")? redGreenInfo.get(sChecks.get(i)).getRedTime() : Integer.parseInt(e_yellow.getText().toString()));
                            map.put("GreenTime",e_green.getText().toString().equals("")? redGreenInfo.get(sChecks.get(i)).getRedTime() : Integer.parseInt(e_green.getText().toString()));
                            Tools.SendRequest(RedGreenController,map);
                        } catch (IOException e) {
                            e.printStackTrace();
                            isSuccess.set(false);
                        }
                    }
                    ChangeData();
                    handler.postDelayed(()->{
                        e_green.setText("");
                        e_red.setText("");
                        e_yellow.setText("");
                        progressDialog.dismiss();
                        if(isSuccess.get()){
                            Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show();
                        }else
                            Toast.makeText(this,"设置失败",Toast.LENGTH_SHORT).show();
                    },500);
                }).start();});
            btn_cancle.setOnClickListener(v->alertDialog.dismiss());
        }
    }

    private void Sort(int position){
        switch (position){
            case 1:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getId()>o2.getId()) return 1;
                    else return  -1;
                });
                break;
            case 2:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getId()<o2.getId()) return 1;
                    else return  -1;
                });
                break;
            case 3:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getRedTime()>o2.getRedTime()) return 1;
                    else return  -1;
                });
                break;
            case 4:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getRedTime()<o2.getRedTime()) return 1;
                    else return  -1;
                });
                break;
            case 5:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getGreenTime()>o2.getGreenTime()) return 1;
                    else return  -1;
                });
                break;
            case 6:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getGreenTime()<o2.getGreenTime()) return 1;
                    else return  -1;
                });
                break;
            case 7:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getYellowTime()>o2.getYellowTime()) return 1;
                    else return  -1;
                });
                break;
            case 8:
                Collections.sort(redGreenInfo, (o1, o2) -> {
                    if(o1.getYellowTime()<o2.getYellowTime()) return 1;
                    else return  -1;
                });
                break;
        }
        handler.post(()->adapter.notifyDataSetChanged());
    }

}
