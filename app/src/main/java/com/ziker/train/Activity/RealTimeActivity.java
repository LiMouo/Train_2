package com.ziker.train.Activity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.ziker.train.Adapter.RealTimeAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.Menu;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MySqLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class RealTimeActivity extends MyAppCompatActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    private static String[] names = {"温度","湿度","光照","CO2","pm2.5","道路状态"};
    private static int[] radioButtonsId = {R.id.rb1,R.id.rb2,R.id.rb3,R.id.rb4,R.id.rb5,R.id.rb6};
    private RadioButton[] radioButtons = new RadioButton[6];
    private List<View> ViewPages = new ArrayList<>();
    private List<String> times = new ArrayList<>();
    private List<Integer>[] Data = new ArrayList[6];//存放六个图表的数据
    private List<String> temp_times = new ArrayList<>();
    private List<Integer>[] tempData = new ArrayList[6];
    private RadioGroup RG_main;
    private ViewPager VP_main;
    private SQLiteDatabase db;
    private Thread QueryThread, BindThread;
    private Handler handler = new Handler();
    private int position = 0;
    private boolean update = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);
        Menu menu = super.SetMenu(this,"实时显示",null);
        menu.needMove = true;
        InitView();
        BindData();
    }

    private void InitView(){
        db = new MySqLiteOpenHelper(this).getWritableDatabase();
        for (int i = 0; i < Data.length; i++) {
            Data[i] = new ArrayList<>();
            tempData[i] = new ArrayList<>();
        }
        QueryData();//开启线程查询数据后面待用
        position = getIntent().getExtras().getInt("position");
        RG_main = findViewById(R.id.RG_main);
        VP_main = findViewById(R.id.VP_main);
        for (int i = 0; i < radioButtons.length; i++)
            radioButtons[i] = findViewById(radioButtonsId[i]);
        for (int i = 0; i < 6; i++)
            ViewPages.add(LayoutInflater.from(this).inflate(R.layout.realtime_mp,null));
    }

    private void BindData(){
        VP_main.setAdapter(new RealTimeAdapter(this, ViewPages));
        VP_main.addOnPageChangeListener(this);
        RG_main.setOnCheckedChangeListener(this);//设置RadioGroup的监听，要在设置选中以前设置监听
        radioButtons[position].setChecked(true);//根据上一页面的点击项选中对应的radio
    }

    private void SetData(int position){
        if(BindThread !=null&& BindThread.isAlive()){//用于刷新上一个图表的线程如果还活着，就发送一个InterruptedException
            BindThread.interrupt();
            try{
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LineChart lineChart = ViewPages.get(position).findViewById(R.id.LineChart);//页面已经变了，获得当前页面的lineChart
        TextView title = ViewPages.get(position).findViewById(R.id.T_title);//获得当前页面的Title
        title.setText(names[position]);//设置当前页面Title
        lineChart.invalidate();//
        BindThread = new Thread(()->{
            List<Entry> entrys = new ArrayList<>();//存放坐标点
            while (true){
                try {
                    long startTime = System.currentTimeMillis();
                    entrys.clear();//清空上一次绘制的坐标点，
                    for (int i = 0; i < Data[position].size(); i++)
                        entrys.add(new Entry(i, Data[position].get(i)));
                    /*原本有数据则只更新数据，不重新创建视图，减少渲染时间*/
                    if(lineChart.getLineData() != null && lineChart.getLineData().getDataSets().size()>0){
                        for (ILineDataSet set : lineChart.getLineData().getDataSets()) {
                            LineDataSet data = (LineDataSet) set;
                            data.setValues(entrys);
                        }
                    }else {
                        LineDataSet lineDataSet = new LineDataSet(entrys,null);
                        LineData lineData = new LineData(lineDataSet);
                        lineDataSet.setColor(Color.parseColor("#8f8f8f"));//线条颜色
                        lineDataSet.setCircleColor(Color.parseColor("#8f8f8f"));//圆点颜色
                        lineDataSet.setDrawCircles(true);//绘制圆点
                        lineDataSet.setDrawCircleHole(false);
                        lineData.setDrawValues(false);//绘制线条上文字
                        lineChart.setLogEnabled(false);
                        lineChart.setData(lineData);//设置数据
                        YAxis yAxisRight = lineChart.getAxisRight();
                        YAxis yAxisLeft = lineChart.getAxisLeft();
                        XAxis xAxis = lineChart.getXAxis();
                        xAxis.setDrawAxisLine(false);//绘制轴线,最下面一根
                        xAxis.setDrawGridLines(false);//设置每个点的线
                        xAxis.setEnabled(true);//轴线启用
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
                        xAxis.setDrawLabels(true);//绘制label
                        xAxis.setAvoidFirstLastClipping(false);
                        xAxis.setLabelRotationAngle(90f);
                        xAxis.setAxisLineWidth(2f);
                        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                            @Override
                            public String getFormattedValue(float value) {
                                /*在更新数据等他一会，回调接口代码运行在主线程，所以这个sleep会抛出的InterruptedException在当前线程不能接收*/
                                if ((int)value < 0 || (int)value >= times.size()) return "";
                                else return times.get((int)value);
                            }
                        });
                        yAxisLeft.setAxisMinimum(0);
                        yAxisLeft.setDrawAxisLine(false);//绘制轴线,最下面一根
                        yAxisLeft.setEnabled(true);//轴线启用
                        yAxisRight.setEnabled(false);
                        Description description = new Description();
                        description.setEnabled(false);//描述启用
                        lineChart.setDescription(description);
                        lineChart.setTouchEnabled(false);
                        lineChart.getLegend().setEnabled(false);
                    }
                    lineChart.getXAxis().setLabelCount(times.size());//设置X轴的label数量
                    lineChart.getData().notifyDataChanged();//更新坐标轴数据,
                    lineChart.notifyDataSetChanged();//更新图表数据
                    handler.post(()->lineChart.invalidate());
                    long endTime = System.currentTimeMillis();
                    if(endTime - startTime <3000)
                        Thread.sleep(3000-(endTime-startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        BindThread.start();
    }

    private void QueryData(){
        QueryThread = new Thread(()->{
            while (true){
                try {
                    long startTime = System.currentTimeMillis();
                    for (int i = 0; i < tempData.length; i++)
                        tempData[i].clear();
                    temp_times.clear();
                    Cursor cursor = db.query("Environment",null,null,null,null,null,"id");
                    if(cursor.moveToFirst()){
                        do {
                            for (int j = 1; j < 7; j++)
                                tempData[j-1].add(cursor.getInt(j));
                            temp_times.add(cursor.getString(7));
                        }while (cursor.moveToNext());
                    }
                    times = temp_times;
                    for (int i = 0; i < Data.length; i++) {
                        Data[i] = tempData[i];
                    }
                    long endTime = System.currentTimeMillis();
                    if(endTime - startTime < 3000)
                        Thread.sleep(3000-(endTime-startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    break;
                }
            }
        });
        QueryThread.start();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageScrollStateChanged(int state) { }
    @Override
    public void onPageSelected(int position) {
        radioButtons[position].setChecked(true);
    }
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb1:
                SetData(0);
                VP_main.setCurrentItem(0);
                break;
            case R.id.rb2:
                SetData(1);
                VP_main.setCurrentItem(1);
                break;
            case R.id.rb3:
                SetData(2);
                VP_main.setCurrentItem(2);
                break;
            case R.id.rb4:
                SetData(3);
                VP_main.setCurrentItem(3);
                break;
            case R.id.rb5:
                SetData(4);
                VP_main.setCurrentItem(4);
                break;
            case R.id.rb6:
                SetData(5);
                VP_main.setCurrentItem(5);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if(QueryThread.isAlive() || BindThread.isAlive()){
            if(QueryThread.isAlive()) QueryThread.interrupt();
            if (BindThread.isAlive()) BindThread.interrupt();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
