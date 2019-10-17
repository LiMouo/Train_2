package com.ziker.train;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ziker.train.Utils.CarInfo;
import com.ziker.train.Utils.MyAppCompatActivity;
import com.ziker.train.Utils.MySqliOpenHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ETCActivity extends MyAppCompatActivity implements View.OnClickListener {
    private final static String TAG = "ETCActivity";
    private Button btn_query,btn_insert;
    private Spinner spinner;
    private TextView t_money;
    private EditText e_money;
    private ArrayAdapter<String> adapter ;
    private List<String> teamlist ;
    private int Car_id = 1;
    private int moeny = 0;
    private Handler handler = new Handler();
    private OkHttpClient okHttpClient;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Gson gson = new Gson();
    private MySqliOpenHelper mySqliOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etc);
        super.setMenu(this,"账户管理",null);
        InitView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Car_id = position+1;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        e_money.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                char number[] = v.getText().toString().toCharArray();
                for (int i=0;i<number.length;i++) {
                    if(number[i]=='0'){
                        continue;
                    }
                    moeny = Integer.parseInt(v.getText().toString().substring(i));
                    if(moeny>999)
                        moeny =999;
                    e_money.setText(moeny+"");
                    break;
                }
                return false;
            }
        });
    }

    private void InitView(){//初始化操作
        mySqliOpenHelper = new MySqliOpenHelper(this,"uselist.db",null,1);
        teamlist = new ArrayList<>();
        btn_insert = findViewById(R.id.btn_insert);
        btn_query = findViewById(R.id.btn_query);
        spinner = findViewById(R.id.car_number);
        t_money = findViewById(R.id.t_money);
        e_money = findViewById(R.id.e_money);
        setbuttoncolor(btn_insert);
        setbuttoncolor(btn_query);
        btn_query.setOnClickListener(this);
        btn_insert.setOnClickListener(this);
        teamlist.add("1");
        teamlist.add("2");
        teamlist.add("3");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,teamlist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setDropDownWidth(100);
        spinner.setAdapter(adapter);
        query();
    }

    private void query(){//查询
        new Thread(() -> {
            try {
                Map map = new HashMap<>();
                map.put("CarId",Car_id);
                map.put("UserName","user1");
                String parms = gson.toJson(map);
                RequestBody requestBody = RequestBody.create(JSON,parms);
                okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url("http://192.168.3.5:8088/transportservice/action/GetCarAccountBalance.do").post(requestBody).build();
                Response response = okHttpClient.newCall(request).execute();
                final String Data = response.body().string();
                final CarInfo carInfo = gson.fromJson(Data,CarInfo.class);
                handler.post(() -> t_money.setText(carInfo.getBalance()+"元"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private void insert(){//充值
        new Thread(() -> {
            try {
                Map map = new HashMap<>();
                map.put("CarId",Car_id);
                map.put("Money",moeny);
                map.put("UserName","user1");
                String parms = gson.toJson(map);
                RequestBody requestBody = RequestBody.create(JSON,parms);
                okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url("http://192.168.3.5:8088/transportservice/action/SetCarAccountRecharge.do").post(requestBody).build();
                okHttpClient.newCall(request).execute();
                handler.post(() -> Toast.makeText(getApplicationContext(),"正在充值",Toast.LENGTH_SHORT).show());
                Thread.sleep(700);
                query();
                insertData(Car_id,moeny,"user1");
                Log.d(TAG, "insertData: 存库成功");
                handler.post(() -> {
                    Toast.makeText(getApplicationContext(),"充值成功",Toast.LENGTH_LONG).show();
                    moeny = 0;
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        e_money.setText("");
    }
    private void insertData(int Cid,int Cmoney,String name){//写入记录
        SQLiteDatabase db = mySqliOpenHelper.getWritableDatabase();
        String timestr=new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(System.currentTimeMillis()));
        ContentValues values = new ContentValues();
        values.put("CARID",Cid);
        values.put("USER",name);
        values.put("DATE",timestr);
        values.put("MONEY",Cmoney);
        long i  = db.insert("use",null,values);
        Log.d(TAG, "insertData: "+i);
    }
    @Override
    public void onClick(View v) {//点击事件
        switch (v.getId()){
            case R.id.btn_query:
                query();
                break;
            case R.id.btn_insert:
                if(moeny>0)
                    insert();
                break;
        }
    }

    public void setbuttoncolor(Button button){
        AnimationDrawable animationDrawable = (AnimationDrawable) button.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}
