package com.ziker.train.Activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MySqLiteOpenHelper;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TrafficActivity extends MyAppCompatActivity {
    private static String[] Colors = {"#6ab82e", "#ece93a", "#f49b25", "#e33532", "#b01e23"};
    private static String RoadStatus = "GetRoadStatus.do";
    private static int[] RoadHckColorTl = {R.drawable.traffic_tl1_board, R.drawable.traffic_tl2_board, R.drawable.traffic_tl3_board, R.drawable.traffic_tl4_board, R.drawable.traffic_tl5_board,};
    private static int[] RoadHckColorBl = {R.drawable.traffic_bl1_board, R.drawable.traffic_bl2_board, R.drawable.traffic_bl3_board, R.drawable.traffic_bl4_board, R.drawable.traffic_bl5_board,};
    private static int[] RoadHcgColorTr = {R.drawable.traffic_tr1_board, R.drawable.traffic_tr2_board, R.drawable.traffic_tr3_board, R.drawable.traffic_tr4_board, R.drawable.traffic_tr5_board,};
    private static int[] RoadHcgColorBr = {R.drawable.traffic_br1_board, R.drawable.traffic_br2_board, R.drawable.traffic_br3_board, R.drawable.traffic_br4_board, R.drawable.traffic_br5_board,};
    private static int[] textRoadsId = {R.id.r_xy, R.id.r_lx, R.id.r_yy, R.id.r_xf, R.id.r_stop};
    private static int[] textHckId = {R.id.r_hck1, R.id.r_hck2, R.id.r_hck3, R.id.r_hck4};
    private static int[] textHcgId = {R.id.r_hcg1, R.id.r_hcg2, R.id.r_hcg3, R.id.r_hcg4};
    private static int[] imageViewsId = {R.id.i_police1, R.id.i_police2, R.id.i_refresh};
    private static int[] textDateId = {R.id.t_date, R.id.t_week};
    private static int[] textEnvironmentId = {R.id.t_temperature, R.id.t_humidity, R.id.t_pm2};
    private static int[] polices1 = {R.drawable.jiaojing1_1, R.drawable.jiaojing1_2};
    private static int[] polices2 = {R.drawable.jiaojing2_1, R.drawable.jiaojing2_2};
    private TextView[] textRoads = new TextView[5];
    private TextView[] textRoadHck = new TextView[4];
    private TextView[] textRoadHcg = new TextView[4];
    private ImageView[] imageViews = new ImageView[3];
    private TextView[] textDate = new TextView[2];
    private TextView[] textEnvironment = new TextView[3];
    private Thread[] Changer = new Thread[3];
    private Handler handler = new Handler();
    private SQLiteDatabase db;
    private Animation operatingAnim;
    private boolean isFirst = true;
    private boolean isDestroy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic);
        super.SetMenu(this, "路况查询", null);
        InitView();
        BindData();
        SetDate();
    }

    private void InitView() {
        db = MySqLiteOpenHelper.getInstance(this).getWritableDatabase();
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        for (int i = 0; i < textRoads.length; i++)
            textRoads[i] = findViewById(textRoadsId[i]);
        for (int i = 0; i < textRoadHcg.length; i++)
            textRoadHcg[i] = findViewById(textHcgId[i]);
        for (int i = 0; i < textRoadHck.length; i++)
            textRoadHck[i] = findViewById(textHckId[i]);
        for (int i = 0; i < textEnvironment.length; i++)
            textEnvironment[i] = findViewById(textEnvironmentId[i]);
        for (int i = 0; i < textDate.length; i++)
            textDate[i] = findViewById(textDateId[i]);
        for (int i = 0; i < imageViews.length; i++)
            imageViews[i] = findViewById(imageViewsId[i]);
    }

    @SuppressLint("SimpleDateFormat")
    private void BindData() {
        imageViews[2].setOnClickListener( v -> SetDate());
        textDate[0].setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        textDate[1].setText(new SimpleDateFormat("EEEE").format(new Date(System.currentTimeMillis())));
        Changer[0] = new Thread(() -> {//改变交警
            try {
                AtomicBoolean isSuccess = new AtomicBoolean(true);
                while (!isDestroy) {
                    handler.post(() -> {
                        if (isSuccess.get()) {
                            imageViews[0].setImageResource(polices1[0]);
                            imageViews[1].setImageResource(polices2[0]);
                        } else {
                            imageViews[0].setImageResource(polices1[1]);
                            imageViews[1].setImageResource(polices2[1]);
                        }
                        isSuccess.set(!isSuccess.get());
                    });
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        Changer[1] = new Thread(() -> {
            List<Integer> RoadState = new ArrayList<>();
            int times = 0;
            while (!isDestroy) {
                try {
                    RoadState.clear();
                    long startTime = System.currentTimeMillis();
                    for (int i = 0; i < 7; i++) {
                        Map map = new HashMap();
                        map.put("RoadId", i + 1);
                        String Data = Tools.SendRequest(RoadStatus, map);
                        JSONObject state = new JSONObject(Data);
                        RoadState.add(state.getInt("Status"));
                    }
                    times++;
                    int finalTimes = times;
                    handler.post(()->{
                        if(RoadState.size() > 6){
                            Log.d(TAG, "BindData: "+ finalTimes);
                            for (int i = 0; i < 4; i++)
                                textRoads[i].setBackgroundColor(Color.parseColor(Colors[RoadState.get(i)-1]));
                            textRoadHck[0].setBackground(getDrawable(RoadHckColorTl[RoadState.get(4)-1]));
                            textRoadHck[1].setBackground(getDrawable(RoadHckColorBl[RoadState.get(4)-1]));
                            textRoadHcg[0].setBackground(getDrawable(RoadHcgColorTr[RoadState.get(5)-1]));
                            textRoadHcg[1].setBackground(getDrawable(RoadHcgColorBr[RoadState.get(5)-1]));
                            for (int i = 0; i < 2; i++) {
                                textRoadHck[i+2].setBackgroundColor(Color.parseColor(Colors[RoadState.get(4)-1]));
                                textRoadHcg[i+2].setBackgroundColor(Color.parseColor(Colors[RoadState.get(5)-1]));
                            }
                            textRoads[4].setBackgroundColor(Color.parseColor(Colors[RoadState.get(6)-1]));
                        }
                    });
                    long endTime = System.currentTimeMillis();
                    if ((endTime - startTime) < 3000)
                        Thread.sleep(3000 - (endTime - startTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Changer[0].start();
        Changer[1].start();
    }

    private void SetDate(){
        if(Changer[2] == null){
            imageViews[2].startAnimation(operatingAnim);
            Changer[2] =new Thread(()->{
                Cursor cursor = db.query("Environment",null,null,null, null,null,"id desc");
                if(cursor.moveToFirst()){
                    handler.postDelayed(()->{
                        textEnvironment[0].setText(cursor.getInt(1)+"℃");
                        textEnvironment[1].setText(cursor.getInt(2)+"%");
                        textEnvironment[2].setText(cursor.getInt(5)+"μg/m3");
                        imageViews[2].clearAnimation();
                        Changer[2] = null;
                    },300);
                }else {
                    handler.postDelayed(()->{
                        if (!isFirst)Tools.Toast(this,"没有数据",false);
                        isFirst = false;
                        imageViews[2].clearAnimation();
                        Changer[2] = null;
                    },300);
                }
            });
            Changer[2].start();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
       isDestroy = true;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
