package com.ziker.train;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import com.ziker.train.Utils.Histroyinfo;
import com.ziker.train.Utils.ManagerAdapter;
import com.ziker.train.Utils.MyAppCompatActivity;
import com.ziker.train.Utils.MySqliOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ManagerActivity extends MyAppCompatActivity implements View.OnClickListener {
    private final String TAG="ManagerActivity";
    private Button btn_query;
    private Spinner spinner;
    private List<String> item;
    private ArrayAdapter<String> adapter;
    private RecyclerView recyclerView;
    private int itemid = 0;
    private Handler handler = new Handler();
    private MySqliOpenHelper mySqliOpenHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        super.setMenu(this,"充值记录",null);
        InitView();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemid = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }
    private void InitView(){//初始化操作
        mySqliOpenHelper = new MySqliOpenHelper(this,"uselist.db",null,1);
        btn_query = findViewById(R.id.btn_query);
        setbuttoncolor(btn_query);
        btn_query.setOnClickListener(this);
        spinner = findViewById(R.id.spinner);
        recyclerView = findViewById(R.id.show_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        item = new ArrayList<>();
        item.add("时间升序");
        item.add("时间降序");
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        query();
    }

    private void query(){//查询
        new Thread(() -> {
            Cursor cursor;
            SQLiteDatabase db = mySqliOpenHelper.getWritableDatabase();
            if(itemid ==0){
                cursor = db.query("use",null,null,null,null,null,"DATE");
            }else{
                cursor = db.query("use",null,null,null,null,null,"DATE desc");
            }
            List<Histroyinfo> list = new ArrayList<>();
            if (cursor.moveToFirst()) {
                for (int i =0;i < cursor.getCount();i++){
                    Histroyinfo histroyinfo = new Histroyinfo();
                    histroyinfo.setId(cursor.getInt(0));
                    histroyinfo.setCarid(cursor.getInt(1));
                    histroyinfo.setUser(cursor.getString(2));
                    histroyinfo.setTime(cursor.getString(3));
                    histroyinfo.setMoney(cursor.getInt(4));
                    list.add(histroyinfo);
                    cursor.moveToNext();
                }
            }else {
                Log.d(TAG, "query: cursor null");
            }
            handler.post(() -> recyclerView.setAdapter(new ManagerAdapter(this,list)));
            cursor.close();
        }).start();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_query:
                    query();
                break;
        }
    }

    public void setbuttoncolor(Button button){
        AnimationDrawable animationDrawable = (AnimationDrawable) button.getBackground();
        animationDrawable.setExitFadeDuration(1000);
        animationDrawable.start();
    }
}
