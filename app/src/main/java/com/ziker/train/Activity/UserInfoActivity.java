package com.ziker.train.Activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.UserInfoAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.CircleImageView;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoActivity extends MyAppCompatActivity {
    private View[] roots = new View[3];
    private RadioButton[] radios = new RadioButton[3];
    private int[] rootsID = {R.id.v_info,R.id.v_histroy,R.id.v_set};
    private int[] radiosID = {R.id.r_info,R.id.r_history,R.id.r_trip};
    private RecyclerView RV_history,RV_info;
    private CircleImageView i_icon1,i_icon2;
    private TextView t_name1,t_name2,t_sex,t_telephone,t_idcardnumber,t_time,t_money1,t_money2;
    private EditText e_money;
    private Button btn_set;
    private UserInfoAdapter historyAdapter;
    private UserInfoAdapter InfoAdapter;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private List<Map> HistoryList = new ArrayList<>();
    private List<Map> InfoList = new ArrayList<>();
    private int[] icons = new int[]{R.drawable.car1,R.drawable.car2,R.drawable.car3,R.drawable.car4};
    private static String[] car_id = new String[]{"京A123456","川A147369","渝A123789","广A159357"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        super.SetMenu(this,"个人中心",null).SetOnBackClick(R.drawable.back,()->finish());
        super.SetAnimation(FLAG_GRADUAL);//设置跳转动画
        InitView();
        BindData();
        QueryData();
    }

    private void InitView(){
        for (int i = 0; i < roots.length; i++)
            roots[i] = findViewById(rootsID[i]);
        for (int i = 0; i < radios.length; i++)
            radios[i] = findViewById(radiosID[i]);
        RV_history = findViewById(R.id.RV_history);
        RV_info = findViewById(R.id.RV_info);
        i_icon1 = findViewById(R.id.i_icon1);
        i_icon2 = findViewById(R.id.i_icon2);
        t_name1 = findViewById(R.id.t_name1);
        t_name2 = findViewById(R.id.t_name2);
        t_sex = findViewById(R.id.t_sex);
        t_telephone = findViewById(R.id.t_telephone);
        t_idcardnumber = findViewById(R.id.t_idcardnumber);
        t_time = findViewById(R.id.t_time);
        t_money1 = findViewById(R.id.t_money1);
        t_money2 = findViewById(R.id.t_money2);
        e_money = findViewById(R.id.e_money);
        btn_set = findViewById(R.id.btn_set);
        sp = getSharedPreferences("UserInfo",MODE_PRIVATE);
        editor = sp.edit();
    }

    private void BindData(){
        Cursor cursor = DB.query("User", null, "username='"+username+"'", null, null, null, null);
        i_icon1.setImageBitmap(Tools.BitmapFromByte(userImage));
        i_icon2.setImageBitmap(Tools.BitmapFromByte(userImage));
        t_name1.setText(username);
        t_name2.setText(username);
        t_sex.setText(Sex ?"男":"女");
        if(cursor.moveToFirst()){
            t_telephone.setText(cursor.getString(5));
            t_idcardnumber.setText("身份证号:"+cursor.getString(6));//注册时间:
            String[] t = cursor.getString(7).split("-");
            String time="";
            for (int i = 0; i < t.length; i++) {
                time += t[i];
                if(i!=t.length-1)
                    time+=".";
            }
            t_time.setText("注册时间:"+time);
        }
        int trip_money = sp.getInt("TripMoney",0);
        e_money.setHint(trip_money+"");
        t_money2.setText("为"+trip_money+"元");
        btn_set.setOnClickListener(v->{
            if(!Tools.isEmpty(e_money.getText().toString())){
                Trip_money = Integer.parseInt(e_money.getText().toString());
                editor.putInt("TripMoney",Trip_money);
                editor.apply();
                t_money2.setText("为"+Trip_money+"元");
                e_money.setText("");
                e_money.setHint(Trip_money+"");
            }
        });
        Tools.SetButtonColor(btn_set);
        for (int i = 0; i < radios.length; i++) {
            int finalI = i;
            radios[i].setOnCheckedChangeListener((v, isCheck)->{
                if(isCheck)
                    roots[finalI].setVisibility(View.VISIBLE);
                else
                    roots[finalI].setVisibility(View.GONE);
            });
        }
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null && bundle.getBoolean("isList"))
            radios[1].setChecked(true);
        else
            radios[0].setChecked(true);
        RV_history.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new UserInfoAdapter(this, HistoryList,false);
        RV_history.setAdapter(historyAdapter);

        RV_info.setLayoutManager(new LinearLayoutManager(this));
        InfoAdapter = new UserInfoAdapter(this,InfoList,true);
        RV_info.setAdapter(InfoAdapter);
    }

    private void QueryData(){
        Cursor HistoryCursor = DB.query("Account", null, "user='"+username+"'", null, null, null, null);
        int sum = 0;
        if(HistoryCursor.moveToFirst()){
            HistoryList.clear();
            do {
                Map map = new HashMap();
                sum +=HistoryCursor.getInt(2);
                String name = HistoryCursor.getString(1);
                map.put("CARID",name);
                map.put("MONEY",HistoryCursor.getInt(2));
                map.put("balance",HistoryCursor.getInt(3));
                map.put("user",HistoryCursor.getString(4));
                map.put("date",HistoryCursor.getString(5));
                HistoryList.add(map);
            }while (HistoryCursor.moveToNext());
        }
        t_money1.setText(sum+"");
        Cursor InfoCursor = DB.query("Account",null,"user='"+username+"'",null,"CARID",null,null);
        if(InfoCursor.moveToFirst()){
            InfoList.clear();
            for (int i = 0; i < InfoCursor.getCount(); i++) {
                Map map = new HashMap();
                map.put("icon",InfoCursor.getBlob(6));
                map.put("CARID", InfoCursor.getString(1));
                map.put("money", InfoCursor.getString(2));
                InfoList.add(map);
                Log.d(TAG, "QueryData: "+map+"\t"+InfoList);
                InfoCursor.moveToNext();
            }
        }
        Log.d(TAG, "QueryData: "+InfoList.size());
        InfoAdapter.notifyDataSetChanged();
        historyAdapter.notifyDataSetChanged();
    }
}
