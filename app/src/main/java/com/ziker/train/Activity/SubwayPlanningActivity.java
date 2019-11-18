package com.ziker.train.Activity;

import android.os.Bundle;
import android.os.Handler;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.GestureImageView;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.io.IOException;

public class SubwayPlanningActivity extends MyAppCompatActivity {
    private GestureImageView myImage;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_planning);
        super.SetMenu(this,"地跌规划",null).SetOnBackClick(R.drawable.back,()->finish());
        myImage = findViewById(R.id.i_icon5);
        String url = getIntent().getExtras().getString("map");
        new Thread(()->{
            try {
                byte[] Data = Tools.SendPostRequest(url);
                handler.post(()->{
                    myImage.setImageBitmap(Tools.BitmapFromByte(Data));
//                    Glide.with(this).load("http://"+MyAppCompatActivity.Ip+":"+MyAppCompatActivity.Port+"/transportservice"+url).into(myImage);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
//        myImage.setImage(R.drawable.exception_background);
    }
}
