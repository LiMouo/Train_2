package com.ziker.train.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MyImage;

public class Violation_CaptureActivity extends MyAppCompatActivity {
    private static int[] ID = {R.id.I_image1,R.id.I_image2,R.id.I_image3,R.id.I_image4};
    private static int[] ImageResource = {R.drawable.wz1,R.drawable.wz2,R.drawable.wz3,R.drawable.wz4};
    private ImageView imageView[] = new ImageView[4];
    private LinearLayout L_all,L_show;
    private MyImage image_show;
    private Button btn_cancle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_capture);
        super.SetMenu(this,"监控抓拍",null).needMove = true;
        super.SetAnimation(FLAG_NO_ANIMATION);
        InitView();
        BindData();
    }

    private void InitView(){
        btn_cancle = findViewById(R.id.btn_cancle);
        L_all = findViewById(R.id.L_all);
        L_show = findViewById(R.id.L_show);
        image_show = findViewById(R.id.I_show);
        for (int i = 0; i < imageView.length; i++)
            imageView[i] = findViewById(ID[i]);
    }

    private void BindData(){
        btn_cancle.setOnClickListener(v -> finish());
        for (int i = 0; i < imageView.length; i++) {
            imageView[i].setImageResource(ImageResource[i]);
            int finalI = i;
            imageView[i].setOnClickListener(v->{
                L_all.setVisibility(View.GONE);
                L_show.setVisibility(View.VISIBLE);
                image_show.setImage(ImageResource[finalI]);
            });
        }
    }
}
