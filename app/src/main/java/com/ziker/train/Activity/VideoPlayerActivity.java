package com.ziker.train.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.Menu;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MyImage;

public class VideoPlayerActivity extends MyAppCompatActivity{
    private LinearLayout video_root;
    private LinearLayout L_root;
    private VideoView video;
    private MyImage image;
    private Button btn_begin,btn_pause,btn_stop;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        Initview();
        BindData();
    }

    private void Initview(){
        bundle = getIntent().getExtras();
        video = findViewById(R.id.video);
        image = findViewById(R.id.image);
        btn_begin = findViewById(R.id.video_begin);
        btn_pause = findViewById(R.id.video_pause);
        btn_stop = findViewById(R.id.video_stop);
        video_root = findViewById(R.id.video_root);
        L_root = findViewById(R.id.L_image);
    }
    private void BindData(){
        int v = bundle.getInt("video",0);
        int i = bundle.getInt("image",0);
        if(v != 0){
            MediaController mediaController = new MediaController(this);
            mediaController.setVisibility(View.VISIBLE);
            video.setMediaController(mediaController);
            super.SetMenu(this,"视频播放",null);
            video_root.setVisibility(View.VISIBLE);
            video.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+v));
        }else if(i != 0){
            Menu menu= super.SetMenu(this,"图片预览",null);
            menu.needMove = true;
            L_root.setVisibility(View.VISIBLE);
            image.setImage(i);
        }
        btn_begin.setOnClickListener(view->{
            if(!video.isPlaying())
                video.start();
        });
        btn_pause.setOnClickListener(view->{
            if(video.isPlaying())
                video.pause();
        });
        btn_stop.setOnClickListener(view->{
            if(video.isPlaying())
                video.resume();
        });
    }

}
