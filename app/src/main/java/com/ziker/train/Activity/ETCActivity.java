package com.ziker.train.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.ETCInfo;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ETCActivity extends MyAppCompatActivity {
    private static String SaveUrl = "SetCarAccountRecharge.do";
    private static String QueryUrl = "GetCarAccountBalance.do";
    private ArrayAdapter<String> adapter ;
    private List<String> item = new ArrayList<>();
    private Button btn_query,btn_insert;
    private Spinner spinner;
    private TextView t_money;
    private EditText e_money;
    private int Car_id = 1;
    private int Money = 0;
    public boolean isFirst = true;
    private Handler handler = new Handler();
    private Thread thread = null;
    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc);
        super.SetMenu(this,"账户管理",null);
        InitView();
        BindData();
        Query();
    }

    private void InitView(){//初始化操作
        btn_insert = findViewById(R.id.btn_insert);
        btn_query = findViewById(R.id.btn_query);
        spinner = findViewById(R.id.car_number);
        t_money = findViewById(R.id.t_money);
        e_money = findViewById(R.id.e_money);
    }

    private void BindData(){
        Tools.SetButtonColor(btn_insert);
        Tools.SetButtonColor(btn_query);
        btn_query.setOnClickListener(v->Query());
        btn_insert.setOnClickListener(v->{if(Money >0)SaveData();});
        item.add("1");
        item.add("2");
        item.add("3");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setDropDownWidth(100);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Car_id = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        e_money.setOnEditorActionListener((v, actionId, event) -> {
            char number[] = v.getText().toString().toCharArray();
            for (int i=0;i<number.length;i++) {
                if(number[i]=='0')
                    continue;
                Money = Integer.parseInt(v.getText().toString().substring(i));
                if(Money >999)
                    Money =999;
                e_money.setText(Money +"");
                break;
            }
            return false;
        });
    }

    private void Query(){//查询
        if(NetworkState){//有网络状态
            ProgressDialog dialog = Tools.WaitDialog(this,"正在查询");
            dialog.setCancelable(false);
            new Thread(() -> {
                try {
                    handler.post(()->dialog.show());
                    Map map = new HashMap<>();
                    map.put("CarId",Car_id);
                    final String Data = Tools.SendPostRequest(QueryUrl,map);
                    final ETCInfo ETCinfo = gson.fromJson(Data, ETCInfo.class);
                    handler.postDelayed(() -> {
                        dialog.dismiss();
                        Tools.Toast(this,"查询成功",false);
                        t_money.setText(ETCinfo.getBalance()+"元");
                    },400);
                    isFirst = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.postDelayed(()->{
                        dialog.dismiss();
                        Tools.Toast(this,"查询失败",false);
                    },0);
                    isFirst = false;
                }
            }).start();
        }
    }
    private void SaveData(){//充值
        if(thread == null){
            ProgressDialog waitDialog = Tools.WaitDialog(this,"正在充值");
            thread = new Thread(() -> {
                try {
                    handler.post(() -> waitDialog.show());
                    Map map = new HashMap<>();
                    map.put("CarId",Car_id);
                    map.put("Money", Money);
                    Tools.SendPostRequest(SaveUrl,map);
                    ContentValues values = new ContentValues();
                    values.put("CARID",Car_id);
                    values.put("USER", user);
                    values.put("DATE",new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(System.currentTimeMillis())));
                    values.put("MONEY", Money);
                    DB.insert("use",null,values);
                    handler.postDelayed(() -> {
                        waitDialog.dismiss();
                        Query();
                        Tools.Toast(this,"充值成功",false);
                        Money = 0;
                        thread = null;
                        e_money.setText("");
                    },700);
                } catch (IOException e) {
                    handler.postDelayed(()->{
                        waitDialog.dismiss();
                        Tools.Toast(this,"充值失败",false);
                        thread = null;
                        e_money.setText("");
                    },700);
                    e.printStackTrace();
                }
            });
            thread.start();
        }
    }
}
