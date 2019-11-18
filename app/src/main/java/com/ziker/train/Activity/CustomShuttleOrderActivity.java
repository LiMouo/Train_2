package com.ziker.train.Activity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.CustomShuttleInfo;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomShuttleOrderActivity extends MyAppCompatActivity {
    private static String UserLineUrl = "SetUserBusLine";
    /*三个页面*/
    private View[] L_view = new View[3];
    private int[] LID = {R.id.l_view1,R.id.l_view2,R.id.l_view3};
    /*第一个页面控件*/
    @BindView(R.id.i_map) ImageView iMap;
    @BindView(R.id.t_route1) TextView tRoute1;@BindView(R.id.t_money1) TextView tMoney1;@BindView(R.id.t_distance1) TextView tDistance1;@BindView(R.id.t_select) TextView tSelect;
    @BindView(R.id.btn_next1) Button btnNext1;
    @BindView(R.id.C_calendar) CalendarView cCalendar;
    /*第二个页面控件*/
    @BindView(R.id.t_route2) TextView tRoute2;
    @BindView(R.id.t_name2) TextView tName2;
    @BindView(R.id.t_telephone2) TextView tTelephone2;
    @BindView(R.id.s_begin2) Spinner sBegin2;
    @BindView(R.id.s_end2) Spinner sEnd2;
    @BindView(R.id.btn_next2) Button btnNext2;
    private List<String> beginList = new ArrayList<>();
    private List<String> endList = new ArrayList<>();
    /*第三个页面控件*/
    @BindView(R.id.t_route3) TextView tRoute3;
    @BindView(R.id.t_name3) TextView tName3;
    @BindView(R.id.t_telephone3) TextView tTelephone3;
    @BindView(R.id.t_begin3) TextView tBegin3;
    @BindView(R.id.t_end3) TextView tEnd3;
    @BindView(R.id.t_time3) TextView tTime3;
    @BindView(R.id.btn_next3) Button btnNext3;
    /*公用对象*/
    private CustomShuttleInfo info;
    private List<String> times = new ArrayList<>();
    private Handler handler = new Handler();
    private int selectBeginId = 0 ;
    private int selectEndId = 1 ;
    private int PageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customshuttleorder);
        super.SetAnimation(FLAG_NO_ANIMATION);
        super.SetMenu(this, "定制班车", null).SetOnBackClick(R.drawable.back, () -> {
            PageIndex--;
            if(PageIndex < 0)
                finish();
            else{
                for (int i = 0; i < L_view.length; i++) {
                    L_view[i].setVisibility(View.GONE);
                }
                L_view[PageIndex].setVisibility(View.VISIBLE);
            }
        });
        InitView();
        PageOne();
    }

    private void InitView() {
        ButterKnife.bind(this);
        info = getIntent().getParcelableExtra("info");
        for (int i = 0; i < L_view.length; i++) {
            L_view[i] = findViewById(LID[i]);
        }
    }

    /**
     * 第一个页面处理逻辑
     */
    private void PageOne(){
        L_view[0].setVisibility(View.VISIBLE);//显示第一个页面
        Glide.with(this).load("http://192.168.3.5:8088/transportservice/" + info.getMap()).into(iMap);
        tRoute1.setText(info.getSites().get(0)+"—"+info.getSites().get(info.getSites().size()-1));//设置路线
        tMoney1.setText("票价:￥"+info.getMileage());//设置票价
        tDistance1.setText("里程:"+info.getTicket()+".0km");//设置距离
        times.add("乘车日期:");//给乘车时间列添加一个乘车日期，这样可以环绕的效果，不会左右或者上下分隔，后面舍弃第一个值就行了
        tSelect.setText(times.get(0));
        cCalendar.setMinDate(new Date().getTime());//设置最小时间为当前时间
        cCalendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String day = year +"-"+month+"-"+dayOfMonth;//组合时间字符串
            boolean have = false;//判断当前选择时间在集合中是否存在，默认不存在
            for (int i = 1; i < times.size(); i++)
                if(times.get(i).equals(day))
                    have = true;//当前选择时间存在集合中
            if(have) times.remove(day);//在集合中存在则移除
            else  times.add(day);//不存在则添加
            String show = "";
            for (int i = 0; i < times.size(); i++) {
                show += times.get(i);
                if(i != times.size()-1 && i!=0)//用逗号连接,不连接第0个，第零个为前面添加的乘车日期
                    show += ",";
            }
            tSelect.setText(show);
        });
        btnNext1.setOnClickListener(v->{
            if(times.size()==1)//默认有一个乘车时间，如果为1，则集合中没有其他数据
                Tools.Toast(this,"请选择乘车日期好吗",false);
            else{
                L_view[0].setVisibility(View.GONE);//隐藏第一页
                PageIndex = 1;//设置页面下标为1
                PageTwo();
            }
        });
    }
    /**
     * 第二个页面处理逻辑
     */
    private void PageTwo(){
        L_view[1].setVisibility(View.VISIBLE);//显示第二页
        tName2.setText(username);//username 为登录时全局MyAppCompatActivity的静态属性
        Cursor cursor = DB.query("User",new String[]{"telephone"},"username='"+username+"'",null,null,null,null);
        if(cursor.moveToFirst())
            tTelephone2.setText(cursor.getString(0));//取出数据库的手机号
        tRoute2.setText(info.getSites().get(0)+"—"+info.getSites().get(info.getSites().size()-1));
        beginList.clear();
        endList.clear();//集合清空，如果不清空，回退页面了，添加就会重复
        beginList.addAll(info.getSites().subList(0,info.getSites().size()-1));//默认上车位置为第一个到倒数第二个
        endList.addAll(info.getSites().subList(selectBeginId+1,beginList.size()));//默认下车位置为第二个到倒数第一个
        ArrayAdapter beginAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, beginList);
        ArrayAdapter endAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, endList);
        sBegin2.setAdapter(beginAdapter);
        sEnd2.setAdapter(endAdapter);
        sBegin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectBeginId = position;
                selectEndId = 0;
                endList.clear();
                endList.addAll(info.getSites().subList(position+1,info.getSites().size()));
                endAdapter.notifyDataSetChanged();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        sEnd2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectEndId = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        btnNext2.setOnClickListener(v->{
            L_view[1].setVisibility(View.GONE);
            PageThree();
            PageIndex = 2;
        });
    }
    /**
     * 第三个页面处理逻辑
     */
    private void PageThree(){
        L_view[2].setVisibility(View.VISIBLE);
        tRoute3.setText("乘车线路:"+info.getSites().get(0)+"—"+info.getSites().get(info.getSites().size()-1));
        tName3.setText(username);
        tTelephone3.setText(tTelephone2.getText());
        tBegin3.setText(beginList.get(selectBeginId));
        tEnd3.setText(endList.get(selectEndId));
        String show = "";
        for (int i = 1; i < times.size(); i++) {
            show += times.get(i);
            if(i != times.size()-1)
                show += ",";
        }
        tTime3.setText(show);
        btnNext3.setOnClickListener(v->{
            ProgressDialog dialog = Tools.WaitDialog(this,"正在提交...");
            dialog.show();
            new Thread(()->{
                try {
                    Map map = new HashMap();
                    map.put("Id",info.getId());
                    map.put("PhoneNumber",tTelephone3.getText());
                    map.put("StartSite",tBegin3.getText());
                    map.put("EndSite",tEnd3.getText());
                    map.put("BusDate",tTime3.getText());
                    String Data = Tools.SendPostRequest(UserLineUrl,map);
                    boolean isSuccess = false;
                    if(Data.indexOf("成功")>0)
                        isSuccess = true;
                    boolean finalIsSuccess = isSuccess;
                    handler.post(()->{
                        dialog.dismiss();
                        if(finalIsSuccess)
                            Tools.Toast(this,"订单提交成功",false);
                        else
                            Tools.Toast(this,"订单提交失败",false);
                    });
                    handler.postDelayed(()-> finish(),700);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(()->{
                        dialog.dismiss();
                        Tools.Toast(this,"订单提交失败",true);
                    });
                }
            }).start();
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0 && getIntent().getExtras()!=null){
            PageIndex--;
            if(PageIndex < 0)
                finish();
            else{
                for (int i = 0; i < L_view.length; i++) {
                    L_view[i].setVisibility(View.GONE);
                }
                L_view[PageIndex].setVisibility(View.VISIBLE);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
