package com.ziker.train;

import android.os.Bundle;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Utils.MyAppCompatActivity;
import com.ziker.train.Utils.VORAdapter;
import com.ziker.train.Utils.VORinfo;

import java.util.ArrayList;
import java.util.List;

public class VORActivity extends MyAppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup RG_main;
    private RecyclerView RV_main;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vor);
        super.setMenu(this,"车辆违章",null);
        InitView();
    }

    private void InitView(){
        RG_main = findViewById(R.id.RG_main);
        RG_main.setOnCheckedChangeListener(this);
        RV_main = findViewById(R.id.RV_main);
        RV_main.setLayoutManager(new GridLayoutManager(this,4));
        RG_main.check(R.id.RG_video);
    }

    private List setVorinfo(boolean image){
        List<VORinfo> v = new ArrayList<>();
        if(image){
            for (int i = 0; i < 4; i++) {
                VORinfo voRinfo = new VORinfo();
                v.add(voRinfo);
            }
            v.get(0).setImage(R.raw.i1);
            v.get(1).setImage(R.raw.i2);
            v.get(2).setImage(R.raw.i3);
            v.get(3).setImage(R.raw.i4);
        }else{
            for (int i = 0; i < 4; i++) {
                VORinfo voRinfo = new VORinfo();
                v.add(voRinfo);
            }
            v.get(0).setTitle("视频一");
            v.get(0).setVideo(R.raw.v1);
            v.get(1).setTitle("视频二");
            v.get(1).setVideo(R.raw.v2);
            v.get(2).setTitle("视频三");
            v.get(2).setVideo(R.raw.v3);
            v.get(3).setTitle("视频四");
            v.get(3).setVideo(R.raw.v4);
        }
        return v;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.RG_video:
                RV_main.setAdapter(new VORAdapter(this,setVorinfo(false)));
                break;
            case R.id.RG_picture:
                RV_main.setAdapter(new VORAdapter(this,setVorinfo(true)));

                break;
        }
    }
}
