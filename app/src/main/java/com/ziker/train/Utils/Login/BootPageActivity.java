package com.ziker.train.Utils.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import com.ziker.train.MainActivity;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;

public class BootPageActivity extends MyAppCompatActivity {
    private SharedPreferences sp;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_page);
        super.SetAnimation(FLAG_NO_ANIMATION);
        new Thread(()-> {
            if (!isTaskRoot()){//自己不为栈根
                finish();
            }else {
                sp = getSharedPreferences("MyAppCompatActivity",MODE_PRIVATE);
                long LoginTime = System.currentTimeMillis();
                long OutTime = sp.getLong("OutTime",0);
                boolean isLogin = sp.getBoolean("isLogin",false);
                float disparity = (OutTime - LoginTime) /(1000 * 60.0f);
                handler.postDelayed(()->{
                    if((disparity < 10 && disparity> 0)  && isLogin){//登录间隔时间小于10分钟，且登录状态为true,即登录过后退出后台没超过十分钟，且登录过
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    }else{
                        Intent intent = new Intent(this,LoginActivity.class);
                        Bundle bundle = new Bundle();
                        if(isLogin)
                            bundle.putBoolean("boot",true);
                        else
                            bundle.putBoolean("auto",true);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                },1200);
            }
        }
        ).start();
    }
}
