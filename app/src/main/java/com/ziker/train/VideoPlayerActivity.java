package com.ziker.train;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.ziker.train.Utils.Menu;
import com.ziker.train.Utils.MyAppCompatActivity;
import com.ziker.train.Utils.MyImage;

public class VideoPlayerActivity extends MyAppCompatActivity implements View.OnClickListener {
    private Bundle bundle;
    private VideoView video;
    private MyImage image;
    private Button btn_begin,btn_pause,btn_stop;
    private LinearLayout video_root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Initview();

    }

    private void Initview(){
        bundle = getIntent().getExtras();
        video = findViewById(R.id.video);
        image = findViewById(R.id.image);
        btn_begin = findViewById(R.id.video_begin);
        btn_pause = findViewById(R.id.video_pause);
        btn_stop = findViewById(R.id.video_stop);
        video_root = findViewById(R.id.video_root);
        int v = bundle.getInt("video",0);
        int i = bundle.getInt("image",0);
        if(v != 0){
            super.setMenu(this,"视频播放",null);
            video_root.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+v));
        }else if(i != 0){
            Menu menu= super.setMenu(this,"图片预览",null);
            menu.needMove = true;
            image.setVisibility(View.VISIBLE);
            image.setImage(i);
        }
        btn_begin.setOnClickListener(this);
        btn_pause.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_begin:
                if(!video.isPlaying())
                    video.start();
                break;
            case R.id.video_pause:
                if(video.isPlaying())
                    video.pause();
                break;
            case R.id.video_stop:
                if(video.isPlaying())
                    video.resume();
                break;
        }
    }
}
