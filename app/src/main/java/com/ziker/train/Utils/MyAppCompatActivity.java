package com.ziker.train.Utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ziker.train.ETCActivity;
import com.ziker.train.MainActivity;
import com.ziker.train.ManagerActivity;
import com.ziker.train.R;
import com.ziker.train.RedGreenActivity;
import com.ziker.train.VORActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAppCompatActivity extends AppCompatActivity {
    private final static String TAG="MyAppCompatActivity" ;
    private List<Integer> idList = new ArrayList<>();
    private List<Class<?>> ActivityList = new ArrayList<>();
    private static int i = 0;
    private Menu menu;

    @Override
    protected void onStop() {
        super.onStop();
        menu.hide_menu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        i--;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i++;
        idList.add(R.id.T_Mainactivity);
        idList.add(R.id.T_ETCactivity);
        idList.add(R.id.T_RedGreedactivity);
        idList.add(R.id.T_Manageractivity);
        idList.add(R.id.T_VORactivity);
        ActivityList.add(MainActivity.class);
        ActivityList.add(ETCActivity.class);
        ActivityList.add(RedGreenActivity.class);
        ActivityList.add(ManagerActivity.class);
        ActivityList.add(VORActivity.class);
    }

    public Menu setMenu(Context context, String Title, Integer Top_more_id){
        View Top_View = findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) Top_View.getParent();
        parent.removeView(Top_View);
        menu = new Menu(context,Top_View,Title, R.drawable.amap,Top_more_id,R.layout.linear_menu);
        menu.getLinear_main_menu().setBackground(getResources().getDrawable(R.drawable.long_button_gradient,null));
        AnimationDrawable animationDrawablemenu = (AnimationDrawable) menu.getLinear_main_menu().getBackground();
        animationDrawablemenu.setExitFadeDuration(1000);
        animationDrawablemenu.start();
        View left = menu.getLinear_left();
        left.setBackground(getResources().getDrawable(R.drawable.long_button_gradient,null));
        AnimationDrawable animationDrawableleft = (AnimationDrawable) left.getBackground();
        animationDrawableleft.setExitFadeDuration(1000);
        animationDrawableleft.start();
        try {
            if(idList.size()!=0){
                if(i>1)
                    menu.setOnMenuItemStartActivity(idList, ActivityList, v ->finish());
                else
                    menu.setOnMenuItemStartActivity(idList,ActivityList,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parent.addView(menu);
        try {
            return menu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
